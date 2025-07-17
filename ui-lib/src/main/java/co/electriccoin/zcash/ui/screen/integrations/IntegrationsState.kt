package co.electriccoin.zcash.ui.screen.integrations

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemState
import co.electriccoin.zcash.ui.design.util.StringResource
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class IntegrationsState(
    val disabledInfo: StringResource?,
    override val onBack: () -> Unit,
    val items: ImmutableList<ZashiListItemState>,
) : ModalBottomSheetState
