package co.electriccoin.zcash.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.provider.ShieldFundsInfoProvider
import co.electriccoin.zcash.ui.common.repository.HomeMessageData
import co.electriccoin.zcash.ui.common.usecase.GetHomeMessageUseCase
import co.electriccoin.zcash.ui.common.usecase.IsRestoreSuccessDialogVisibleUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToNearPayUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToReceiveUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToSwapUseCase
import co.electriccoin.zcash.ui.common.usecase.ShieldFundsFromMessageUseCase
import co.electriccoin.zcash.ui.design.component.BigIconButtonState
import co.electriccoin.zcash.ui.design.util.TickerLocation.HIDDEN
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.error.ErrorArgs
import co.electriccoin.zcash.ui.screen.error.NavigateToErrorUseCase
import co.electriccoin.zcash.ui.screen.exchangerate.optin.ExchangeRateOptInArgs
import co.electriccoin.zcash.ui.screen.home.backup.SeedBackupInfo
import co.electriccoin.zcash.ui.screen.home.backup.WalletBackupDetail
import co.electriccoin.zcash.ui.screen.home.backup.WalletBackupMessageState
import co.electriccoin.zcash.ui.screen.home.currency.EnableCurrencyConversionMessageState
import co.electriccoin.zcash.ui.screen.home.disconnected.WalletDisconnectedInfo
import co.electriccoin.zcash.ui.screen.home.disconnected.WalletDisconnectedMessageState
import co.electriccoin.zcash.ui.screen.home.error.WalletErrorMessageState
import co.electriccoin.zcash.ui.screen.home.reporting.CrashReportMessageState
import co.electriccoin.zcash.ui.screen.home.reporting.CrashReportOptIn
import co.electriccoin.zcash.ui.screen.home.restoring.WalletRestoringInfo
import co.electriccoin.zcash.ui.screen.home.restoring.WalletRestoringMessageState
import co.electriccoin.zcash.ui.screen.home.shieldfunds.ShieldFundsMessageState
import co.electriccoin.zcash.ui.screen.home.syncing.WalletSyncingInfo
import co.electriccoin.zcash.ui.screen.home.syncing.WalletSyncingMessageState
import co.electriccoin.zcash.ui.screen.home.updating.WalletUpdatingInfo
import co.electriccoin.zcash.ui.screen.home.updating.WalletUpdatingMessageState
import co.electriccoin.zcash.ui.screen.send.Send
import co.electriccoin.zcash.ui.util.CURRENCY_TICKER
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Suppress("TooManyFunctions")
class HomeVM(
    getHomeMessage: GetHomeMessageUseCase,
    shieldFundsInfoProvider: ShieldFundsInfoProvider,
    isRestoreSuccessDialogVisible: IsRestoreSuccessDialogVisibleUseCase,
    private val navigationRouter: NavigationRouter,
    private val shieldFundsFromMessage: ShieldFundsFromMessageUseCase,
    private val navigateToError: NavigateToErrorUseCase,
    private val navigateToReceive: NavigateToReceiveUseCase,
    private val navigateToNearPay: NavigateToNearPayUseCase,
    private val navigateToSwap: NavigateToSwapUseCase
) : ViewModel() {
    private val messageData =
        getHomeMessage
            .observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = null
            )

    private val messageState =
        combine(
            messageData,
            shieldFundsInfoProvider.observe(),
        ) { message, isShieldFundsInfoEnabled ->
            createMessageState(message, isShieldFundsInfoEnabled)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(0, 0),
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
                HomeRestoreSuccessDialogState.takeIf { isVisible == true }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    val state: StateFlow<HomeState?> =
        messageState
            .map { messageState ->
                createState(
                    messageState = messageState
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    private var onPayButtonClickJob: Job? = null

    private var onSwapButtonClick: Job? = null

    private fun createState(messageState: HomeMessageState?) =
        HomeState(
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
                    text = stringRes(R.string.home_button_pay),
                    icon = R.drawable.ic_home_pay,
                    onClick = ::onPayButtonClick,
                ),
            fourthButton =
                BigIconButtonState(
                    text = stringRes(R.string.home_button_swap),
                    icon = R.drawable.ic_home_swap,
                    onClick = ::onSwapButtonClick,
                ),
            message = messageState
        )

    private fun createMessageState(data: HomeMessageData?, isShieldFundsInfoEnabled: Boolean) =
        when (data) {
            is HomeMessageData.Backup ->
                WalletBackupMessageState(
                    onClick = ::onWalletBackupMessageClick,
                    onButtonClick = ::onWalletBackupMessageButtonClick,
                )

            HomeMessageData.Disconnected ->
                WalletDisconnectedMessageState(
                    onClick = ::onWalletDisconnectedMessageClick
                )

            HomeMessageData.EnableCurrencyConversion ->
                EnableCurrencyConversionMessageState(
                    onClick = ::onEnableCurrencyConversionClick,
                    onButtonClick = ::onEnableCurrencyConversionClick
                )

            is HomeMessageData.Error ->
                WalletErrorMessageState(
                    onClick = { onWalletErrorMessageClick(data) }
                )

            is HomeMessageData.Restoring ->
                WalletRestoringMessageState(
                    isSpendable = data.isSpendable,
                    progress = data.progress,
                    onClick = ::onWalletRestoringMessageClick
                )

            is HomeMessageData.Syncing ->
                WalletSyncingMessageState(
                    progress = data.progress,
                    onClick = ::onWalletSyncingMessageClick
                )

            is HomeMessageData.ShieldFunds ->
                ShieldFundsMessageState(
                    subtitle =
                        stringRes(
                            R.string.home_message_transparent_balance_subtitle,
                            stringRes(data.zatoshi, HIDDEN),
                            CURRENCY_TICKER
                        ),
                    onClick =
                        if (isShieldFundsInfoEnabled) {
                            { onShieldFundsMessageClick() }
                        } else {
                            null
                        },
                    onButtonClick = ::onShieldFundsMessageButtonClick,
                )

            HomeMessageData.Updating ->
                WalletUpdatingMessageState(
                    onClick = ::onWalletUpdatingMessageClick
                )

            HomeMessageData.CrashReport ->
                CrashReportMessageState(
                    onClick = ::onCrashReportMessageClick,
                    onButtonClick = ::onCrashReportMessageClick
                )

            null -> null
        }

    init {
        messageData
            .onEach {
                navigateToError.navigateAutomaticallyToSyncError(it)
            }.launchIn(viewModelScope)
    }

    private fun onCrashReportMessageClick() = navigationRouter.forward(CrashReportOptIn)

    private fun onSwapButtonClick() {
        if (onSwapButtonClick?.isActive == true) return
        onSwapButtonClick = viewModelScope.launch { navigateToSwap() }
    }

    private fun onSendButtonClick() = navigationRouter.forward(Send())

    private fun onReceiveButtonClick() = viewModelScope.launch { navigateToReceive() }

    private fun onPayButtonClick() {
        if (onPayButtonClickJob?.isActive == true) return
        onPayButtonClickJob = viewModelScope.launch { navigateToNearPay() }
    }

    private fun onWalletUpdatingMessageClick() = navigationRouter.forward(WalletUpdatingInfo)

    private fun onWalletSyncingMessageClick() = navigationRouter.forward(WalletSyncingInfo)

    private fun onWalletRestoringMessageClick() = navigationRouter.forward(WalletRestoringInfo)

    private fun onEnableCurrencyConversionClick() = navigationRouter.forward(ExchangeRateOptInArgs)

    private fun onWalletDisconnectedMessageClick() = navigationRouter.forward(WalletDisconnectedInfo)

    private fun onWalletBackupMessageClick() = navigationRouter.forward(SeedBackupInfo)

    private fun onWalletBackupMessageButtonClick() = navigationRouter.forward(WalletBackupDetail(false))

    private fun onShieldFundsMessageClick() = viewModelScope.launch { shieldFundsFromMessage() }

    private fun onShieldFundsMessageButtonClick() = viewModelScope.launch { shieldFundsFromMessage() }

    private fun onWalletErrorMessageClick(homeMessageData: HomeMessageData.Error) =
        navigateToError(ErrorArgs.SyncError(homeMessageData.synchronizerError))
}
