package co.electriccoin.zcash.ui.screen.integrations

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.component.listitem.ListItemState
import co.electriccoin.zcash.ui.design.util.StringResource
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class IntegrationsState(
    val disabledInfo: StringResource?,
    val items: ImmutableList<ListItemState>,
    val showFooter: Boolean = true,
    override val onBack: () -> Unit,
) : ModalBottomSheetState
