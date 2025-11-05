package co.electriccoin.zcash.ui.screen.advancedsettings

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.listitem.ListItemState

@Immutable
data class AdvancedSettingsState(
    val onBack: () -> Unit,
    val items: List<ListItemState>,
    val deleteButton: ButtonState,
)
