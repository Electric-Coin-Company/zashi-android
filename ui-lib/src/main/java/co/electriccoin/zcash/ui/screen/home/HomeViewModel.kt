package co.electriccoin.zcash.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.Zatoshi
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
import co.electriccoin.zcash.ui.screen.exchangerate.optin.ExchangeRateOptIn
import co.electriccoin.zcash.ui.screen.home.balance.TransparentBalanceInfo
import co.electriccoin.zcash.ui.screen.home.messages.EnableCurrencyConversionMessageState
import co.electriccoin.zcash.ui.screen.home.messages.HomeMessageState
import co.electriccoin.zcash.ui.screen.home.messages.TransparentBalanceDetectedMessageState
import co.electriccoin.zcash.ui.screen.home.messages.WalletBackupMessageState
import co.electriccoin.zcash.ui.screen.home.messages.WalletDisconnectedMessageState
import co.electriccoin.zcash.ui.screen.home.messages.WalletErrorMessageState
import co.electriccoin.zcash.ui.screen.home.messages.WalletRestoringMessageState
import co.electriccoin.zcash.ui.screen.home.messages.WalletSyncingMessageState
import co.electriccoin.zcash.ui.screen.home.messages.WalletUpdatingMessageState
import co.electriccoin.zcash.ui.screen.integrations.DialogIntegrations
import co.electriccoin.zcash.ui.screen.receive.Receive
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveAddressType
import co.electriccoin.zcash.ui.screen.scan.Scan
import co.electriccoin.zcash.ui.screen.scan.ScanFlow
import co.electriccoin.zcash.ui.screen.seed.backup.SeedBackup
import co.electriccoin.zcash.ui.screen.send.Send
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class HomeViewModel(
    getVersionInfoProvider: GetVersionInfoProvider,
    getSelectedWalletAccountUseCase: GetSelectedWalletAccountUseCase,
    private val navigationRouter: NavigationRouter,
    private val isRestoreSuccessDialogVisible: IsRestoreSuccessDialogVisibleUseCase,
    private val navigateToCoinbase: NavigateToCoinbaseUseCase
) : ViewModel() {
    @Suppress("MagicNumber")
    private val messageState =
        flow {
            val states =
                listOf(
                    WalletErrorMessageState(
                        onClick = {}
                    ),
                    WalletDisconnectedMessageState(onClick = {
                        navigationRouter.forward(WalletDisconnectedInfo)
                    }),
                    WalletRestoringMessageState(progress = 0, onClick = {
                        navigationRouter.forward(WalletRestoringInfo)
                    }),
                    WalletRestoringMessageState(progress = 100, onClick = {
                        navigationRouter.forward(WalletRestoringInfo)
                    }),
                    WalletSyncingMessageState(progress = 0, onClick = {
                        navigationRouter.forward(WalletSyncingInfo)
                    }),
                    WalletSyncingMessageState(progress = 100, onClick = {
                        navigationRouter.forward(WalletSyncingInfo)
                    }),
                    WalletUpdatingMessageState(onClick = {
                        navigationRouter.forward(WalletUpdatingInfo)
                    }),
                    WalletBackupMessageState(
                        onClick = {
                            navigationRouter.forward(SeedBackupInfo)
                        },
                        onButtonClick = {
                            navigationRouter.forward(SeedBackup(false))
                        }
                    ),
                    TransparentBalanceDetectedMessageState(
                        subtitle = stringRes(zatoshi = Zatoshi(1000)),
                        onClick = {
                            navigationRouter.forward(TransparentBalanceInfo)
                        },
                        onButtonClick = {
                            // navigationRouter.forward(TransparentBalanceInfo)
                        },
                    ),
                    EnableCurrencyConversionMessageState(
                        onClick = {
                            navigationRouter.forward(ExchangeRateOptIn)
                        },
                        onButtonClick = {
                            navigationRouter.forward(ExchangeRateOptIn)
                        },
                    )
                )

            var index = 0

            while (true) {
                emit(states[index])
                delay(3.seconds)
                if (index == states.lastIndex) {
                    emit(null)
                    delay(10.seconds)
                    index = 0
                } else {
                    index += 1
                }
            }
        }

    private val isRestoreDialogVisible: Flow<Boolean?> =
        isRestoreSuccessDialogVisible
            .observe()
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
