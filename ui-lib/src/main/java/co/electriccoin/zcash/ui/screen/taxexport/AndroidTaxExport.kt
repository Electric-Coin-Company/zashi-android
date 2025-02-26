package co.electriccoin.zcash.ui.screen.taxexport

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AndroidTaxExport() {
    val viewModel = koinViewModel<TaxExportViewModel>()
    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val walletState by walletViewModel.walletStateInformation.collectAsStateWithLifecycle()

    TaxExportView(
        state = state,
        topAppBarSubTitleState = walletState,
    )
}
