package co.electriccoin.zcash.ui.screen.qrcode.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.CopyToClipboardUseCase
import co.electriccoin.zcash.ui.common.usecase.GetAddressesUseCase
import co.electriccoin.zcash.ui.screen.qrcode.ext.fromReceiveAddressType
import co.electriccoin.zcash.ui.screen.qrcode.model.QrCodeState
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveAddressType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QrCodeViewModel(
    private val addressTypeOrdinal: Int,
    private val application: Application,
    getAddresses: GetAddressesUseCase,
    private val copyToClipboard: CopyToClipboardUseCase,
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    internal val state = getAddresses().mapLatest { addresses ->
        QrCodeState.Prepared(
            walletAddress = addresses.fromReceiveAddressType(ReceiveAddressType.fromOrdinal(addressTypeOrdinal)),
            onAddressCopy = { address -> onAddressCopyClick(address) },
            onQrCodeShare = ::onQrCodeShareClick,
            onBack = ::onBack,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
        initialValue = QrCodeState.Loading
    )

    val backNavigationCommand = MutableSharedFlow<Unit>()

    private fun onBack() =
        viewModelScope.launch {
            backNavigationCommand.emit(Unit)
        }

    private fun onQrCodeShareClick(address: String) =
        Toast.makeText(application.applicationContext, "Not implemented yet", Toast.LENGTH_SHORT).show()

    private fun onAddressCopyClick(address: String) =
        copyToClipboard(
            context = application.applicationContext,
            tag = application.getString(R.string.receive_clipboard_tag),
            value = address
        )
}
