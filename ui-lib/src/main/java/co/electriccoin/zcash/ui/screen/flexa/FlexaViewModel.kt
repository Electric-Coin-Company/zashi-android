package co.electriccoin.zcash.ui.screen.flexa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.common.usecase.CreateFlexaTransactionUseCase
import com.flexa.spend.Transaction
import kotlinx.coroutines.launch

class FlexaViewModel(
    private val createFlexaTransaction: CreateFlexaTransactionUseCase
) : ViewModel() {
    fun createTransaction(transaction: Result<Transaction>) =
        viewModelScope.launch {
            createFlexaTransaction(transaction)
        }
}
