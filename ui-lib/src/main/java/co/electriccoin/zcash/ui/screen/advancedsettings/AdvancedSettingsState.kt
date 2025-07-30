package co.electriccoin.zcash.ui.screen.advancedsettings

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.listitem.ListItemState
import kotlinx.collections.immutable.ImmutableList

data class AdvancedSettingsState(
    val onBack: () -> Unit,
    val items: ImmutableList<ListItemState>,
    val deleteButton: ButtonState,
)
