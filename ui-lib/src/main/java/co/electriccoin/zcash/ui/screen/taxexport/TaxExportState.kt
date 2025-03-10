package co.electriccoin.zcash.ui.screen.taxexport

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.StringResource

data class TaxExportState(
    val text: StringResource,
    val exportButton: ButtonState,
    val onBack: () -> Unit,
)
