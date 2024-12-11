package co.electriccoin.zcash.ui.screen.signkeystonetransaction.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.CancelKeystoneProposalFlowUseCase
import co.electriccoin.zcash.ui.common.usecase.CreateKeystoneProposalPCZTEncoderUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.addressbook.viewmodel.ADDRESS_MAX_LENGTH
import co.electriccoin.zcash.ui.screen.scankeystone.ScanKeystonePCZTRequest
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.state.SignKeystoneTransactionState
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.state.ZashiAccountInfoListItemState
import com.sparrowwallet.hummingbird.UREncoder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignKeystoneTransactionViewModel(
    observeSelectedWalletAccount: ObserveSelectedWalletAccountUseCase,
    private val navigationRouter: NavigationRouter,
    private val createKeystoneProposalPCZTEncoder: CreateKeystoneProposalPCZTEncoderUseCase,
    private val cancelKeystoneProposalFlow: CancelKeystoneProposalFlowUseCase
) : ViewModel() {

    private var encoder: UREncoder? = null

    private val currentQrPart = MutableStateFlow<String?>(null)

    val state: StateFlow<SignKeystoneTransactionState?> =
        combine(observeSelectedWalletAccount.require(), currentQrPart) { wallet, qrData ->
            SignKeystoneTransactionState(
                accountInfo =
                ZashiAccountInfoListItemState(
                    icon = R.drawable.ic_settings_info,
                    title = wallet.name,
                    subtitle = stringRes("${wallet.unified.address.address.take(ADDRESS_MAX_LENGTH)}...")
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
                onBack = ::onBack
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT), null)

    init {
        viewModelScope.launch {
            encoder = createKeystoneProposalPCZTEncoder()
            currentQrPart.update { encoder?.nextPart() }
        }
    }

    private fun onBack() {
        cancelKeystoneProposalFlow()
    }

    private fun onRejectClick() {
        cancelKeystoneProposalFlow()
    }

    private fun onSignTransactionClick() {
        navigationRouter.forward(ScanKeystonePCZTRequest)
    }
}
