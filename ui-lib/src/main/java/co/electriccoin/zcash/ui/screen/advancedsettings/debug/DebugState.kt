package co.electriccoin.zcash.ui.screen.advancedsettings.debug

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.listitem.ListItemState

@Immutable
data class DebugState(
    val items: List<ListItemState>,
    val onBack: () -> Unit,
)
