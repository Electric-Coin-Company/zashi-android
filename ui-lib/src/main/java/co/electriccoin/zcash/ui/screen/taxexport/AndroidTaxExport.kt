package co.electriccoin.zcash.ui.screen.taxexport

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun AndroidTaxExport() {
    val viewModel = koinViewModel<TaxExportViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    BackHandler { state.onBack() }
    TaxExportView(
        state = state,
    )
}
