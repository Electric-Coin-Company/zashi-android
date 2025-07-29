package co.electriccoin.zcash.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.provider.ShieldFundsInfoProvider
import co.electriccoin.zcash.ui.common.repository.HomeMessageData
import co.electriccoin.zcash.ui.common.usecase.ErrorArgs
import co.electriccoin.zcash.ui.common.usecase.GetHomeMessageUseCase
import co.electriccoin.zcash.ui.common.usecase.IsRestoreSuccessDialogVisibleUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToErrorUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToReceiveUseCase
import co.electriccoin.zcash.ui.common.usecase.ShieldFundsMessageUseCase
import co.electriccoin.zcash.ui.design.component.BigIconButtonState
import co.electriccoin.zcash.ui.design.util.TickerLocation.HIDDEN
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.home.backup.SeedBackupInfo
import co.electriccoin.zcash.ui.screen.home.backup.WalletBackupDetail
import co.electriccoin.zcash.ui.screen.home.backup.WalletBackupMessageState
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
import co.electriccoin.zcash.ui.screen.home.tor.EnableTorMessageState
import co.electriccoin.zcash.ui.screen.home.updating.WalletUpdatingInfo
import co.electriccoin.zcash.ui.screen.home.updating.WalletUpdatingMessageState
import co.electriccoin.zcash.ui.screen.integrations.IntegrationsArgs
import co.electriccoin.zcash.ui.screen.scan.ScanArgs
import co.electriccoin.zcash.ui.screen.scan.ScanFlow
import co.electriccoin.zcash.ui.screen.send.Send
import co.electriccoin.zcash.ui.screen.tor.optin.TorOptInArgs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Suppress("TooManyFunctions")
class HomeViewModel(
    getHomeMessage: GetHomeMessageUseCase,
    shieldFundsInfoProvider: ShieldFundsInfoProvider,
    private val navigationRouter: NavigationRouter,
    private val isRestoreSuccessDialogVisible: IsRestoreSuccessDialogVisibleUseCase,
    private val shieldFunds: ShieldFundsMessageUseCase,
    private val navigateToError: NavigateToErrorUseCase,
    private val navigateToReceive: NavigateToReceiveUseCase,
) : ViewModel() {
    private val messageState =
        combine(
            getHomeMessage.observe(),
            shieldFundsInfoProvider.observe()
        ) { message, isShieldFundsInfoEnabled ->
            createMessageState(message, isShieldFundsInfoEnabled)
        }.stateIn(
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

    val state: StateFlow<HomeState?> = messageState
        .map { messageState ->
            createState(messageState)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    private fun createState(
        messageState: HomeMessageState?,
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
        fourthButton = BigIconButtonState(
            text = stringRes(R.string.home_button_more),
            icon = R.drawable.ic_home_more,
            onClick = ::onMoreButtonClick,
        ),
        message = messageState
    )

    private fun createMessageState(it: HomeMessageData?, isShieldFundsInfoEnabled: Boolean) =
        when (it) {
            is HomeMessageData.Backup ->
                WalletBackupMessageState(
                    onClick = ::onWalletBackupMessageClick,
                    onButtonClick = ::onWalletBackupMessageButtonClick,
                )

            HomeMessageData.Disconnected ->
                WalletDisconnectedMessageState(
                    onClick = ::onWalletDisconnectedMessageClick
                )

            is HomeMessageData.Error ->
                WalletErrorMessageState(
                    onClick = { onWalletErrorMessageClick(it) }
                )

            is HomeMessageData.Restoring ->
                WalletRestoringMessageState(
                    isSpendable = it.isSpendable,
                    progress = it.progress,
                    onClick = ::onWalletRestoringMessageClick
                )

            is HomeMessageData.Syncing ->
                WalletSyncingMessageState(
                    progress = it.progress,
                    onClick = ::onWalletSyncingMessageClick
                )

            is HomeMessageData.ShieldFunds ->
                ShieldFundsMessageState(
                    subtitle =
                        stringRes(
                            R.string.home_message_transparent_balance_subtitle,
                            stringRes(it.zatoshi, HIDDEN)
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

            HomeMessageData.EnableTor ->
                EnableTorMessageState(
                    onClick = ::onEnableTorMessageClick,
                    onButtonClick = ::onEnableTorMessageClick,
                )

            null -> null
        }

    private fun onCrashReportMessageClick() = navigationRouter.forward(CrashReportOptIn)

    private fun onRestoreDialogSeenClick() = viewModelScope.launch { isRestoreSuccessDialogVisible.setSeen() }

    private fun onMoreButtonClick() = navigationRouter.forward(IntegrationsArgs)

    private fun onSendButtonClick() = navigationRouter.forward(Send())

    private fun onReceiveButtonClick() = viewModelScope.launch { navigateToReceive() }

    private fun onScanButtonClick() = navigationRouter.forward(ScanArgs(ScanFlow.HOMEPAGE))

    private fun onWalletUpdatingMessageClick() = navigationRouter.forward(WalletUpdatingInfo)

    private fun onWalletSyncingMessageClick() = navigationRouter.forward(WalletSyncingInfo)

    private fun onWalletRestoringMessageClick() = navigationRouter.forward(WalletRestoringInfo)

    private fun onWalletDisconnectedMessageClick() = navigationRouter.forward(WalletDisconnectedInfo)

    private fun onWalletBackupMessageClick() = navigationRouter.forward(SeedBackupInfo)

    private fun onWalletBackupMessageButtonClick() = navigationRouter.forward(WalletBackupDetail(false))

    private fun onShieldFundsMessageClick() = viewModelScope.launch { shieldFunds() }

    private fun onShieldFundsMessageButtonClick() = viewModelScope.launch { shieldFunds() }

    private fun onWalletErrorMessageClick(homeMessageData: HomeMessageData.Error) =
        navigateToError(ErrorArgs.SyncError(homeMessageData.synchronizerError))

    private fun onEnableTorMessageClick() = navigationRouter.forward(TorOptInArgs)
}
