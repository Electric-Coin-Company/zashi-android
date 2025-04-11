package co.electriccoin.zcash.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.NavigationTargets
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.DistributionDimension
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.usecase.GetHomeMessageUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.common.repository.HomeMessageData
import co.electriccoin.zcash.ui.common.usecase.IsRestoreSuccessDialogVisibleUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToCoinbaseUseCase
import co.electriccoin.zcash.ui.common.usecase.ShieldFundsUseCase
import co.electriccoin.zcash.ui.design.component.BigIconButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.exchangerate.optin.ExchangeRateOptIn
import co.electriccoin.zcash.ui.screen.home.backup.SeedBackupInfo
import co.electriccoin.zcash.ui.screen.home.backup.WalletBackupDetail
import co.electriccoin.zcash.ui.screen.home.backup.WalletBackupMessageState
import co.electriccoin.zcash.ui.screen.home.currency.EnableCurrencyConversionMessageState
import co.electriccoin.zcash.ui.screen.home.disconnected.WalletDisconnectedInfo
import co.electriccoin.zcash.ui.screen.home.disconnected.WalletDisconnectedMessageState
import co.electriccoin.zcash.ui.screen.home.error.WalletErrorMessageState
import co.electriccoin.zcash.ui.screen.home.restoring.WalletRestoringInfo
import co.electriccoin.zcash.ui.screen.home.restoring.WalletRestoringMessageState
import co.electriccoin.zcash.ui.screen.home.shieldfunds.ShieldFundsInfo
import co.electriccoin.zcash.ui.screen.home.shieldfunds.ShieldFundsMessageState
import co.electriccoin.zcash.ui.screen.home.syncing.WalletSyncingInfo
import co.electriccoin.zcash.ui.screen.home.syncing.WalletSyncingMessageState
import co.electriccoin.zcash.ui.screen.home.updating.WalletUpdatingInfo
import co.electriccoin.zcash.ui.screen.home.updating.WalletUpdatingMessageState
import co.electriccoin.zcash.ui.screen.integrations.DialogIntegrations
import co.electriccoin.zcash.ui.screen.receive.Receive
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveAddressType
import co.electriccoin.zcash.ui.screen.scan.Scan
import co.electriccoin.zcash.ui.screen.scan.ScanFlow
import co.electriccoin.zcash.ui.screen.send.Send
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    getHomeMessage: GetHomeMessageUseCase,
    getVersionInfoProvider: GetVersionInfoProvider,
    getSelectedWalletAccountUseCase: GetSelectedWalletAccountUseCase,
    private val navigationRouter: NavigationRouter,
    private val isRestoreSuccessDialogVisible: IsRestoreSuccessDialogVisibleUseCase,
    private val navigateToCoinbase: NavigateToCoinbaseUseCase,
    private val shieldFunds: ShieldFundsUseCase
) : ViewModel() {

    private val messageState = getHomeMessage
        .observe()
        .map { createMessageState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    private val isRestoreDialogVisible: Flow<Boolean?> =
        isRestoreSuccessDialogVisible
            .observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    val restoreDialogState: StateFlow<HomeRestoreSuccessDialogState?> =
        isRestoreDialogVisible
            .map { isVisible ->
                HomeRestoreSuccessDialogState(
                    onClick = ::onRestoreDialogSeenClick
                ).takeIf { isVisible == true }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    val state: StateFlow<HomeState?> =
        combine(
            getSelectedWalletAccountUseCase.observe(),
            messageState
        ) { selectedAccount, messageState ->
            createState(getVersionInfoProvider, selectedAccount, messageState)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    private fun createState(
        getVersionInfoProvider: GetVersionInfoProvider,
        selectedAccount: WalletAccount?,
        messageState: HomeMessageState?
    ) = HomeState(
        firstButton =
            BigIconButtonState(
                text = stringRes(R.string.home_button_receive),
                icon = R.drawable.ic_home_receive,
                onClick = ::onReceiveButtonClick,
            ),
        secondButton =
            BigIconButtonState(
                text = stringRes(R.string.home_button_send),
                icon = R.drawable.ic_home_send,
                onClick = ::onSendButtonClick,
            ),
        thirdButton =
            BigIconButtonState(
                text = stringRes(R.string.home_button_scan),
                icon = R.drawable.ic_home_scan,
                onClick = ::onScanButtonClick,
            ),
        fourthButton =
            when {
                getVersionInfoProvider().distributionDimension == DistributionDimension.FOSS ->
                    BigIconButtonState(
                        text = stringRes(R.string.home_button_request),
                        icon = R.drawable.ic_home_request,
                        onClick = ::onRequestClick,
                    )

                selectedAccount is KeystoneAccount ->
                    BigIconButtonState(
                        text = stringRes(R.string.home_button_buy),
                        icon = R.drawable.ic_home_buy,
                        onClick = ::onBuyClick,
                    )

                else ->
                    BigIconButtonState(
                        text = stringRes(R.string.home_button_more),
                        icon = R.drawable.ic_home_more,
                        onClick = ::onMoreButtonClick,
                    )
            },
        message = messageState
    )

    private fun createMessageState(it: HomeMessageData?) = when (it) {
        is HomeMessageData.Backup -> WalletBackupMessageState(
            onClick = ::onWalletBackupMessageClick,
            onButtonClick = ::onWalletBackupMessageButtonClick,
        )

        HomeMessageData.Disconnected -> WalletDisconnectedMessageState(
            onClick = ::onWalletDisconnectedMessageClick
        )

        HomeMessageData.EnableCurrencyConversion -> EnableCurrencyConversionMessageState(
            onClick = ::onEnableCurrencyConversionClick,
            onButtonClick = ::onEnableCurrencyConversionClick
        )

        is HomeMessageData.Error -> WalletErrorMessageState(
            onClick = { onWalletErrorMessageClick(it) }
        )

        is HomeMessageData.Restoring -> WalletRestoringMessageState(
            progress = it.progress,
            onClick = ::onWalletRestoringMessageClick
        )

        is HomeMessageData.Syncing -> WalletSyncingMessageState(
            progress = it.progress,
            onClick = ::onWalletSyncingMessageClick
        )

        is HomeMessageData.ShieldFunds -> ShieldFundsMessageState(
            subtitle = stringRes(
                R.string.home_message_transparent_balance_subtitle,
                stringRes(it.zatoshi)
            ),
            onClick = ::onShieldFundsMessageClick,
            onButtonClick = ::onShieldFundsMessageButtonClick,
        )

        HomeMessageData.Updating -> WalletUpdatingMessageState(
            onClick = ::onWalletUpdatingMessageClick
        )

        null -> null
    }

    private fun onRestoreDialogSeenClick() = viewModelScope.launch { isRestoreSuccessDialogVisible.setSeen() }

    private fun onMoreButtonClick() = navigationRouter.forward(DialogIntegrations)

    private fun onSendButtonClick() = navigationRouter.forward(Send())

    private fun onReceiveButtonClick() = navigationRouter.forward(Receive)

    private fun onScanButtonClick() = navigationRouter.forward(Scan(ScanFlow.HOMEPAGE))

    private fun onBuyClick() = viewModelScope.launch { navigateToCoinbase(replaceCurrentScreen = false) }

    private fun onRequestClick() =
        navigationRouter.forward("${NavigationTargets.REQUEST}/${ReceiveAddressType.Unified.ordinal}")

    private fun onWalletUpdatingMessageClick() = navigationRouter.forward(WalletUpdatingInfo)

    private fun onWalletSyncingMessageClick() = navigationRouter.forward(WalletSyncingInfo)

    private fun onWalletRestoringMessageClick() = navigationRouter.forward(WalletRestoringInfo)

    private fun onEnableCurrencyConversionClick() = navigationRouter.forward(ExchangeRateOptIn)

    private fun onWalletDisconnectedMessageClick() = navigationRouter.forward(WalletDisconnectedInfo)

    private fun onWalletBackupMessageClick() = navigationRouter.forward(SeedBackupInfo)

    private fun onWalletBackupMessageButtonClick() = navigationRouter.forward(WalletBackupDetail(false))

    private fun onShieldFundsMessageClick() = navigationRouter.forward(ShieldFundsInfo)

    private fun onShieldFundsMessageButtonClick() = shieldFunds(navigateBackAfterSuccess = false)

    private fun onWalletErrorMessageClick(homeMessageData: HomeMessageData.Error) {
        // statusText =
        //     context.getString(
        //         R.string.balances_status_error_simple,
        //         context.getString(R.string.app_name)
        //     )
        // statusAction =
        //     StatusAction.Error(
        //         details =
        //             context.getString(
        //                 R.string.balances_status_error_dialog_cause,
        //                 walletSnapshot.synchronizerError.getCauseMessage()
        //                     ?: context.getString(R.string.balances_status_error_dialog_cause_unknown),
        //                 walletSnapshot.synchronizerError.getStackTrace(limit = STACKTRACE_LIMIT)
        //                     ?: context.getString(R.string.balances_status_error_dialog_stacktrace_unknown)
        //             ),
        //         fullStackTrace = walletSnapshot.synchronizerError.getStackTrace(limit = null)
        //     )
        // TODO()
    }
}
