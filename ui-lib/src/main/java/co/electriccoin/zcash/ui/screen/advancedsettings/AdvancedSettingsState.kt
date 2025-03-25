package co.electriccoin.zcash.ui.screen.advancedsettings

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemState
import kotlinx.collections.immutable.ImmutableList

data class AdvancedSettingsState(
    val onBack: () -> Unit,
    val items: ImmutableList<ZashiListItemState>,
    val deleteButton: ButtonState,
)
