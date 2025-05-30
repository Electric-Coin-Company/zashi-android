package co.electriccoin.zcash.ui.screen.integrations

import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.component.listitem.ListItemState
import co.electriccoin.zcash.ui.design.util.StringResource
import kotlinx.collections.immutable.ImmutableList

data class IntegrationsState(
    val disabledInfo: StringResource?,
    val items: ImmutableList<ListItemState>,
    override val onBack: () -> Unit,
) : ModalBottomSheetState
