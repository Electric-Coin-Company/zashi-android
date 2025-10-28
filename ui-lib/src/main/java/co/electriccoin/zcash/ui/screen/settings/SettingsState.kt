package co.electriccoin.zcash.ui.screen.settings

import co.electriccoin.zcash.ui.design.component.listitem.ListItemState
import co.electriccoin.zcash.ui.design.util.StringResource
import kotlinx.collections.immutable.ImmutableList

data class SettingsState(
    val version: StringResource,
    val onBack: () -> Unit,
    val onVersionLongClick: () -> Unit,
    val items: ImmutableList<ListItemState>,
)
