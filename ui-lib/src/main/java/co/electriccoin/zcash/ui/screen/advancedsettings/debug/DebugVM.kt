package co.electriccoin.zcash.ui.screen.advancedsettings.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.EphemeralAddressRepository
import co.electriccoin.zcash.ui.common.usecase.CopyToClipboardUseCase
import co.electriccoin.zcash.ui.design.component.listitem.ListItemState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.advancedsettings.debug.text.DebugTextArgs
import co.electriccoin.zcash.ui.screen.hotfix.ephemeral.EphemeralHotfixArgs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DebugVM(
    private val copyToClipboardUseCase: CopyToClipboardUseCase,
    private val ephemeralAddressRepository: EphemeralAddressRepository,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    val state: StateFlow<DebugState> = MutableStateFlow(
        DebugState(
            onBack = ::onBack,
            items = listOf(
                ListItemState(
                    // bigIcon = imageRes(R.drawable.ic_zec_round_full),
                    // smallIcon = imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_zec_unshielded),
                    title = stringRes("Get Current Ephemeral Address"),
                    onClick = ::onGetEphemeralAddressClick
                ),
                ListItemState(
                    // bigIcon = imageRes(R.drawable.ic_zec_round_full),
                    // smallIcon = imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_zec_unshielded),
                    title = stringRes("Generate an Ephemeral Address"),
                    onClick = ::onGenerateEphemeralAddressClick
                ),
                ListItemState(
                    // bigIcon = imageRes(R.drawable.ic_zec_round_full),
                    // smallIcon = imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_zec_unshielded),
                    title = stringRes("Discover Funds"),
                    onClick = ::onDiscoverFundsClick
                ),
            )
        )
    ).asStateFlow()

    private fun onBack() = navigationRouter.back()

    private fun onGetEphemeralAddressClick() = viewModelScope.launch {
        val address = ephemeralAddressRepository.get()
        copyToClipboardUseCase(address?.address.toString())
        navigationRouter.forward(
            DebugTextArgs(
                title = "Current Ephemeral Address",
                text = address.toString()
            )
        )
    }

    private fun onGenerateEphemeralAddressClick() = viewModelScope.launch {
        val address = ephemeralAddressRepository.create()
        copyToClipboardUseCase(address.address)
        navigationRouter.forward(
            DebugTextArgs(
                title = "New Ephemeral Address",
                text = address.toString()
            )
        )
    }

    private fun onDiscoverFundsClick() = navigationRouter.forward(EphemeralHotfixArgs(null))
}