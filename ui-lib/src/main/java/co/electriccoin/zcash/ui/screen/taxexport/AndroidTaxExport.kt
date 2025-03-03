package co.electriccoin.zcash.ui.screen.taxexport

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AndroidTaxExport() {
    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val walletSnapshot = walletViewModel.currentWalletSnapshot.collectAsStateWithLifecycle().value
    val viewModel = koinViewModel<TaxExportViewModel> { parametersOf(walletSnapshot?.isZashi) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val walletState by walletViewModel.walletStateInformation.collectAsStateWithLifecycle()

    TaxExportView(
        state = state,
        topAppBarSubTitleState = walletState,
    )
}
