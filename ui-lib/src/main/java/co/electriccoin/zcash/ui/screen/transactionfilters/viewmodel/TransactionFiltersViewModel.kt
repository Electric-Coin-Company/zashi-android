package co.electriccoin.zcash.ui.screen.transactionfilters.viewmodel

import androidx.lifecycle.ViewModel
import co.electriccoin.zcash.ui.screen.transactionfilters.model.TransactionFiltersState
import kotlinx.coroutines.flow.StateFlow

internal class TransactionFiltersViewModel : ViewModel() {
    val state: StateFlow<TransactionFiltersState> = TODO()
}