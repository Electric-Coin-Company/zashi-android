package co.electriccoin.zcash.ui.screen.receive

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.util.StringResource

@Immutable
data class ReceiveState(
    val items: List<ReceiveAddressState>?,
    val isLoading: Boolean,
    val onBack: () -> Unit
)

@Immutable
data class ReceiveAddressState(
    val icon: Int,
    val title: StringResource,
    val subtitle: StringResource,
    val isExpanded: Boolean,
    val colorMode: ColorMode,
    val infoIconButton: IconButtonState,
    val onClick: () -> Unit,
    val isShielded: Boolean,
    val onCopyClicked: () -> Unit,
    val onQrClicked: () -> Unit,
    val onRequestClicked: () -> Unit,
) {
    enum class ColorMode {
        ZASHI,
        KEYSTONE,
        DEFAULT
    }
}
