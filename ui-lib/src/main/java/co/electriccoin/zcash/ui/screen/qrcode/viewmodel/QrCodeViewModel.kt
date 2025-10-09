package co.electriccoin.zcash.ui.screen.qrcode.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.usecase.CopyToClipboardUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.ShareQRUseCase
import co.electriccoin.zcash.ui.screen.qrcode.ext.fromReceiveAddressType
import co.electriccoin.zcash.ui.screen.qrcode.model.QrCodeState
import co.electriccoin.zcash.ui.screen.qrcode.model.QrCodeType
import co.electriccoin.zcash.ui.screen.receive.ReceiveAddressType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QrCodeViewModel(
    observeSelectedWalletAccount: ObserveSelectedWalletAccountUseCase,
    private val addressTypeOrdinal: Int,
    private val application: Application,
    private val copyToClipboard: CopyToClipboardUseCase,
    private val navigationRouter: NavigationRouter,
    private val shareQR: ShareQRUseCase,
    private val context: Context,
) : ViewModel() {
    internal val state =
        observeSelectedWalletAccount
            .require()
            .map { account ->
                val walletAddress = account.fromReceiveAddressType(ReceiveAddressType.fromOrdinal(addressTypeOrdinal))

                if (walletAddress == null) {
                    QrCodeState.Loading
                } else {
                    QrCodeState.Prepared(
                        walletAddress = walletAddress,
                        onAddressCopy = { address -> onAddressCopyClick(address) },
                        onQrCodeShare = {
                            viewModelScope.launch {
                                shareQR(
                                    qrData = it,
                                    shareText = context.getString(R.string.qr_code_share_chooser_text),
                                    sharePickerText = context.getString(R.string.qr_code_share_chooser_title),
                                    filenamePrefix = "zcash_address_qr_",
                                    centerIcon =
                                        when (account) {
                                            is ZashiAccount -> R.drawable.logo_zec_fill_stroke
                                            is KeystoneAccount ->
                                                co.electriccoin.zcash.ui.design.R.drawable.ic_item_keystone_qr
                                        },
                                )
                            }
                        },
                        onBack = ::onBack,
                        qrCodeType =
                            when (account) {
                                is KeystoneAccount -> QrCodeType.KEYSTONE
                                is ZashiAccount -> QrCodeType.ZASHI
                            }
                    )
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = QrCodeState.Loading
            )

    private fun onBack() = navigationRouter.back()

    private fun onAddressCopyClick(address: String) =
        copyToClipboard(
            tag = application.getString(R.string.qr_code_clipboard_tag),
            value = address
        )
}
