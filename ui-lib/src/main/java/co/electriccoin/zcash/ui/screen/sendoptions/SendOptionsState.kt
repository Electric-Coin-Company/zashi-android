package co.electriccoin.zcash.ui.screen.sendoptions

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.component.listitem.ListItemState
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class SendOptionsState(
    override val onBack: () -> Unit,
    val items: ImmutableList<ListItemState>,
) : ModalBottomSheetState
