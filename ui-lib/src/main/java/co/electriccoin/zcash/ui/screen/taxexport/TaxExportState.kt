package co.electriccoin.zcash.ui.screen.taxexport

import co.electriccoin.zcash.ui.design.component.ButtonState

data class TaxExportState(
    val exportButton: ButtonState,
    val onBack: () -> Unit,
)
