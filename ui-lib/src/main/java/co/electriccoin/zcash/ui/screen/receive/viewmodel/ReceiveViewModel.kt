package co.electriccoin.zcash.ui.screen.receive.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationTargets
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.usecase.CopyToClipboardUseCase
import co.electriccoin.zcash.ui.common.usecase.GetAddressesUseCase
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveAddressType
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReceiveViewModel(
    private val application: Application,
    getVersionInfo: GetVersionInfoProvider,
    getAddresses: GetAddressesUseCase,
    private val copyToClipboard: CopyToClipboardUseCase,
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    internal val state =
        getAddresses().mapLatest { addresses ->
            ReceiveState.Prepared(
                walletAddresses = addresses,
                isTestnet = getVersionInfo().isTestnet,
                onAddressCopy = { address ->
                    copyToClipboard(
                        context = application.applicationContext,
                        tag = application.getString(R.string.receive_clipboard_tag),
                        value = address
                    )
                },
                onQrCode = { addressType -> onQrCodeClick(addressType) },
                onRequest = { addressType -> onRequestClick(addressType) },
                onSettings = ::onSettingsClick,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = ReceiveState.Loading
        )

    val navigationCommand = MutableSharedFlow<String>()

    @Suppress("UNUSED_PARAMETER")
    private fun onRequestClick(addressType: ReceiveAddressType) =
        Toast.makeText(application.applicationContext, "Not implemented yet", Toast.LENGTH_SHORT).show()

    private fun onQrCodeClick(addressType: ReceiveAddressType) =
        viewModelScope.launch {
            navigationCommand.emit("${NavigationTargets.QR_CODE}/${addressType.ordinal}")
        }

    private fun onSettingsClick() =
        viewModelScope.launch {
            navigationCommand.emit(NavigationTargets.SETTINGS)
        }
}
