package co.electriccoin.zcash.ui.screen.advancedsettings.model

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItemState
import kotlinx.collections.immutable.ImmutableList

data class AdvancedSettingsState(
    val onBack: () -> Unit,
    val items: ImmutableList<ZashiSettingsListItemState>,
    val deleteButton: ButtonState,
)
