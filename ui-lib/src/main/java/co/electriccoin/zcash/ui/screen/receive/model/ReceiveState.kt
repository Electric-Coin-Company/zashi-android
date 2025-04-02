package co.electriccoin.zcash.ui.screen.receive.model

import co.electriccoin.zcash.ui.design.util.StringResource

data class ReceiveState(
    val items: List<ReceiveAddressState>?,
    val isLoading: Boolean,
    val onBack: () -> Unit
)

data class ReceiveAddressState(
    val icon: Int,
    val title: StringResource,
    val subtitle: StringResource,
    val isExpanded: Boolean,
    val onClick: () -> Unit,
    val isShielded: Boolean,
    val onCopyClicked: () -> Unit,
    val onQrClicked: () -> Unit,
    val onRequestClicked: () -> Unit,
)
