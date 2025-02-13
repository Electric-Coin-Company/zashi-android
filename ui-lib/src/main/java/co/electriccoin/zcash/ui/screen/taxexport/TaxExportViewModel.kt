package co.electriccoin.zcash.ui.screen.taxexport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.ExportTaxUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaxExportViewModel(
    private val exportTax: ExportTaxUseCase,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    val state: StateFlow<TaxExportState> =
        MutableStateFlow(
            TaxExportState(
                exportButton =
                    ButtonState(
                        text = stringRes(R.string.tax_export_export_button),
                        onClick = ::onExportClick
                    ),
                onBack = ::onBack
            )
        )

    private fun onExportClick() =
        viewModelScope.launch {
            exportTax()
        }

    private fun onBack() {
        navigationRouter.back()
    }
}
