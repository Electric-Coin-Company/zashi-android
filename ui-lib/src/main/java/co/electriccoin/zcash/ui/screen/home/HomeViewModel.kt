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
import co.electriccoin.zcash.ui.common.usecase.GetSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.IsRestoreSuccessDialogVisibleUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToCoinbaseUseCase
import co.electriccoin.zcash.ui.design.component.BigIconButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.home.messages.WalletBackupMessageState
import co.electriccoin.zcash.ui.screen.integrations.DialogIntegrations
import co.electriccoin.zcash.ui.screen.receive.Receive
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveAddressType
import co.electriccoin.zcash.ui.screen.scan.Scan
import co.electriccoin.zcash.ui.screen.scan.ScanFlow
import co.electriccoin.zcash.ui.screen.seed.backup.SeedBackup
import co.electriccoin.zcash.ui.screen.send.Send
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    getVersionInfoProvider: GetVersionInfoProvider,
    getSelectedWalletAccountUseCase: GetSelectedWalletAccountUseCase,
    private val navigationRouter: NavigationRouter,
    private val isRestoreSuccessDialogVisible: IsRestoreSuccessDialogVisibleUseCase,
    private val navigateToCoinbase: NavigateToCoinbaseUseCase
) : ViewModel() {
    private val isMessageVisible = MutableStateFlow(true)

    private val isRestoreDialogVisible: Flow<Boolean?> =
        isRestoreSuccessDialogVisible.observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    val restoreDialogState: StateFlow<HomeRestoreDialogState?> =
        isRestoreDialogVisible
            .map { isVisible ->
                HomeRestoreDialogState(
                    onClick = ::onRestoreDialogSeenClick
                ).takeIf { isVisible == true }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    val state: StateFlow<HomeState?> =
        combine(getSelectedWalletAccountUseCase.observe(), isMessageVisible) { selectedAccount, isMessageVisible ->
            createState(getVersionInfoProvider, selectedAccount, isMessageVisible)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    private fun createState(
        getVersionInfoProvider: GetVersionInfoProvider,
        selectedAccount: WalletAccount?,
        isMessageVisible: Boolean
    ) = HomeState(
        firstButton =
            BigIconButtonState(
                text = stringRes("Receive"),
                icon = R.drawable.ic_home_receive,
                onClick = ::onReceiveButtonClick,
            ),
        secondButton =
            BigIconButtonState(
                text = stringRes("Send"),
                icon = R.drawable.ic_home_send,
                onClick = ::onSendButtonClick,
            ),
        thirdButton =
            BigIconButtonState(
                text = stringRes("Scan"),
                icon = R.drawable.ic_home_scan,
                onClick = ::onScanButtonClick,
            ),
        fourthButton =
            when {
                getVersionInfoProvider().distributionDimension == DistributionDimension.FOSS ->
                    BigIconButtonState(
                        text = stringRes("Request"),
                        icon = R.drawable.ic_home_request,
                        onClick = ::onRequestClick,
                    )

                selectedAccount is KeystoneAccount ->
                    BigIconButtonState(
                        text = stringRes("Buy"),
                        icon = R.drawable.ic_home_buy,
                        onClick = ::onBuyClick,
                    )

                else ->
                    BigIconButtonState(
                        text = stringRes("More"),
                        icon = R.drawable.ic_home_more,
                        onClick = ::onMoreButtonClick,
                    )
            },
        message = createWalletBackupMessageState().takeIf { isMessageVisible }
    )

    private fun createWalletBackupMessageState(): WalletBackupMessageState {
        return WalletBackupMessageState(
            onClick = {
                navigationRouter.forward(SeedBackup)
            }
        )
    }

    private fun onRestoreDialogSeenClick() =
        viewModelScope.launch {
            isRestoreSuccessDialogVisible.setSeen()
        }

    private fun onMoreButtonClick() {
        navigationRouter.forward(DialogIntegrations)
    }

    private fun onSendButtonClick() {
        navigationRouter.forward(Send())
    }

    private fun onReceiveButtonClick() {
        navigationRouter.forward(Receive)
    }

    private fun onScanButtonClick() {
        navigationRouter.forward(Scan(ScanFlow.HOMEPAGE))
    }

    private fun onBuyClick() =
        viewModelScope.launch {
            navigateToCoinbase(replaceCurrentScreen = false)
        }

    private fun onRequestClick() {
        navigationRouter.forward("${NavigationTargets.REQUEST}/${ReceiveAddressType.Unified.ordinal}")
    }
}
