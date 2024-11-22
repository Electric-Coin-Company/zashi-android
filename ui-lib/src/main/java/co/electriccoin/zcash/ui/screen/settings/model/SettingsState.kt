package co.electriccoin.zcash.ui.screen.settings.model

import co.electriccoin.zcash.ui.design.component.ZashiListItemState
import co.electriccoin.zcash.ui.design.util.StringResource
import kotlinx.collections.immutable.ImmutableList

data class SettingsState(
    val version: StringResource,
    val onBack: () -> Unit,
    val debugMenu: SettingsTroubleshootingState?,
    val items: ImmutableList<ZashiListItemState>,
)
