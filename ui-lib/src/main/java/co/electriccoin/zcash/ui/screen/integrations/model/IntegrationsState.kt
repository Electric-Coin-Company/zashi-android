package co.electriccoin.zcash.ui.screen.integrations.model

import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItemState
import co.electriccoin.zcash.ui.design.util.StringResource

data class IntegrationsState(
    val version: StringResource,
    val coinbase: ZashiSettingsListItemState?,
    val disabledInfo: StringResource?,
    val onBack: () -> Unit,
)
