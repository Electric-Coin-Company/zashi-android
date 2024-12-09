package co.electriccoin.zcash.ui.screen.signkeystonetransaction.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.common.usecase.ObserveClearSendUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.addressbook.viewmodel.ADDRESS_MAX_LENGTH
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.state.SignKeystoneTransactionState
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.state.ZashiAccountInfoListItemState
import co.electriccoin.zcash.ui.screen.transactionprogress.KeystoneTransactionProgress
import com.keystone.module.SolSignRequest
import com.keystone.sdk.KeystoneSDK
import com.keystone.sdk.KeystoneSolanaSDK
import com.sparrowwallet.hummingbird.UREncoder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class SignKeystoneTransactionViewModel(
    private val navigationRouter: NavigationRouter,
    observeSelectedWalletAccount: ObserveSelectedWalletAccountUseCase,
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val observeClearSend: ObserveClearSendUseCase
) : ViewModel() {
    private val qr = genSolanaQRCode()

    private val currentQrPart = MutableStateFlow(qr.nextPart())

    val state: StateFlow<SignKeystoneTransactionState?> = combine(observeSelectedWalletAccount(), currentQrPart) {
        wallet, qrData ->
        SignKeystoneTransactionState(
            accountInfo = ZashiAccountInfoListItemState(
                icon = R.drawable.ic_settings_info,
                title = wallet.name,
                subtitle = stringRes("${wallet.unifiedAddress.address.take(ADDRESS_MAX_LENGTH)}...")
            ),
            generateNextQrCode = { currentQrPart.update { qr.nextPart() } },
            qrData = qrData,
            positiveButton = ButtonState(
                text = stringRes(R.string.sign_keystone_transaction_positive),
                onClick = ::onSignTransactionClick
            ),
            negativeButton = ButtonState(
                text = stringRes(R.string.sign_keystone_transaction_negative),
                onClick = ::onRejectClick
            ),
            onBack = ::onBack
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT), null)

    private fun onBack() {
        keystoneProposalRepository.clear()
        observeClearSend.requestClear()
        navigationRouter.backToRoot()
    }

    private fun onRejectClick() {
        keystoneProposalRepository.clear()
        observeClearSend.requestClear()
        navigationRouter.backToRoot()
    }

    private fun onSignTransactionClick() {
        // TODO keystone
        keystoneProposalRepository.signAndCompleteProposalTemp()
        navigationRouter.forward(KeystoneTransactionProgress)
    }

    @Suppress("MaxLineLength", "MagicNumber")
    private fun genSolanaQRCode(): UREncoder {
        val requestId = "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d"
        val signData =
            "01000103c8d842a2f17fd7aab608ce2ea535a6e958dffa20caf669b347b911c4171965530f957620b228bae2b94c82ddd4c093983a67365555b737ec7ddc1117e61c72e0000000000000000000000000000000000000000000000000000000000000000010295cc2f1f39f3604718496ea00676d6a72ec66ad09d926e3ece34f565f18d201020200010c0200000000e1f50500000000"
        val path = "m/44'/501'/0'/0'"
        val xfp = "707EED6C"
        val address = ""
        val origin = "solflare"
        val signType = KeystoneSolanaSDK.SignType.Message
        val sdk = KeystoneSDK()
        KeystoneSDK.maxFragmentLen = 100
        return sdk.sol.generateSignRequest(
            SolSignRequest(
                requestId = requestId,
                signData = signData,
                path = path,
                xfp = xfp,
                address = address,
                origin = origin,
                signType = signType
            )
        )
    }
}