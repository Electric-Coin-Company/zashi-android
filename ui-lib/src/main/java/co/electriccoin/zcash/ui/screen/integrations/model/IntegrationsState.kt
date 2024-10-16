package co.electriccoin.zcash.ui.screen.integrations.model

import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItemState
import co.electriccoin.zcash.ui.design.util.StringResource
import com.flexa.spend.Transaction

data class IntegrationsState(
    val version: StringResource,
    val coinbase: ZashiSettingsListItemState?,
    val flexa: ZashiSettingsListItemState?,
    val disabledInfo: StringResource?,
    val onBack: () -> Unit,
    val onFlexaSendCallback: (Result<Transaction>) -> Unit
)
