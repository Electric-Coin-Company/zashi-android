package co.electriccoin.zcash.ui.screen.integrations.model

import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItemState
import co.electriccoin.zcash.ui.design.util.StringResource
import kotlinx.collections.immutable.ImmutableList

data class IntegrationsState(
    val disabledInfo: StringResource?,
    val onBack: () -> Unit,
    val items: ImmutableList<ZashiSettingsListItemState>,
)
