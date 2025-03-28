package co.electriccoin.zcash.ui.screen.integrations

import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemState
import co.electriccoin.zcash.ui.design.util.StringResource
import kotlinx.collections.immutable.ImmutableList

data class IntegrationsState(
    val disabledInfo: StringResource?,
    val onBack: () -> Unit,
    val items: ImmutableList<ZashiListItemState>,
    val onBottomSheetHidden: () -> Unit,
)
