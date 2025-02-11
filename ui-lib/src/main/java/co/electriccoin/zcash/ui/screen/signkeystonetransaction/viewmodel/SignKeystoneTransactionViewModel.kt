package co.electriccoin.zcash.ui.screen.signkeystonetransaction.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.BuildConfig
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.datasource.ShieldTransactionProposal
import co.electriccoin.zcash.ui.common.usecase.CancelProposalFlowUseCase
import co.electriccoin.zcash.ui.common.usecase.CreateKeystoneProposalPCZTEncoderUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.SharePCZTUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.addressbook.viewmodel.ADDRESS_MAX_LENGTH
import co.electriccoin.zcash.ui.screen.scankeystone.ScanKeystonePCZTRequest
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.state.SignKeystoneTransactionState
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.state.ZashiAccountInfoListItemState
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.view.SignKeystoneTransactionBottomSheetState
import com.sparrowwallet.hummingbird.UREncoder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class SignKeystoneTransactionViewModel(
    observeSelectedWalletAccount: ObserveSelectedWalletAccountUseCase,
    observeProposalUseCase: ObserveProposalUseCase,
    private val navigationRouter: NavigationRouter,
    private val createKeystoneProposalPCZTEncoder: CreateKeystoneProposalPCZTEncoderUseCase,
    private val cancelKeystoneProposalFlow: CancelProposalFlowUseCase,
    private val sharePCZT: SharePCZTUseCase
) : ViewModel() {
    private var encoder: UREncoder? = null

    private val isBottomSheetVisible = MutableStateFlow(false)

    private val currentQrPart = MutableStateFlow<String?>(null)

    val bottomSheetState =
        isBottomSheetVisible
            .map { isVisible ->
                if (isVisible) {
                    SignKeystoneTransactionBottomSheetState(
                        onBack = ::onCloseBottomSheetClick,
                        positiveButton =
                            ButtonState(
                                text = stringRes(R.string.sign_keystone_transaction_bottom_sheet_go_back),
                                onClick = ::onCloseBottomSheetClick
                            ),
                        negativeButton =
                            ButtonState(
                                text = stringRes(R.string.sign_keystone_transaction_bottom_sheet_reject),
                                onClick = ::onRejectBottomSheetClick
                            ),
                    )
                } else {
                    null
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    val state: StateFlow<SignKeystoneTransactionState?> =
        combine(
            observeProposalUseCase(),
            observeSelectedWalletAccount.require(),
            currentQrPart
        ) { proposal, wallet, qrData ->
            SignKeystoneTransactionState(
                accountInfo =
                    ZashiAccountInfoListItemState(
                        icon = R.drawable.ic_settings_info,
                        title = wallet.name,
                        subtitle =
                            if (proposal is ShieldTransactionProposal) {
                                stringRes("${wallet.transparent.address.address.take(ADDRESS_MAX_LENGTH)}...")
                            } else {
                                stringRes("${wallet.unified.address.address.take(ADDRESS_MAX_LENGTH)}...")
                            }
                    ),
                generateNextQrCode = { currentQrPart.update { encoder?.nextPart() } },
                qrData = qrData,
                positiveButton =
                    ButtonState(
                        text = stringRes(R.string.sign_keystone_transaction_positive),
                        onClick = ::onSignTransactionClick
                    ),
                negativeButton =
                    ButtonState(
                        text = stringRes(R.string.sign_keystone_transaction_negative),
                        onClick = ::onRejectClick
                    ),
                shareButton =
                    ButtonState(
                        text = stringRes("Share PCZT"),
                        onClick = ::onSharePCZTClick
                    ).takeIf { BuildConfig.DEBUG },
                onBack = ::onBack,
                onQrCodeClick = {
                    // TODO [#1731]: Allow QR codes colors switching
                    // TODO [#1731]: https://github.com/Electric-Coin-Company/zashi-android/issues/1731
                },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    init {
        viewModelScope.launch {
            try {
                encoder = createKeystoneProposalPCZTEncoder()
                currentQrPart.update { encoder?.nextPart() }
            } catch (_: Exception) {
                // do nothing
            }
        }
    }

    private fun onRejectBottomSheetClick() {
        viewModelScope.launch {
            isBottomSheetVisible.update { false }
            delay(350.milliseconds)
            cancelKeystoneProposalFlow()
        }
    }

    private fun onCloseBottomSheetClick() {
        isBottomSheetVisible.update { false }
    }

    private fun onSharePCZTClick() =
        viewModelScope.launch {
            sharePCZT()
        }

    private fun onBack() {
        cancelKeystoneProposalFlow()
    }

    private fun onRejectClick() {
        isBottomSheetVisible.update { true }
    }

    private fun onSignTransactionClick() {
        navigationRouter.forward(ScanKeystonePCZTRequest)
    }
}
