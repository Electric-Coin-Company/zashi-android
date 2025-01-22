package co.electriccoin.zcash.ui.screen.transactionhistory.widget

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.screen.transactionhistory.TransactionState

@Immutable
sealed interface TransactionHistoryWidgetState {
    @Immutable
    data class Data(
        val header: TransactionHistoryWidgetHeaderState,
        val transactions: List<TransactionState>
    ) : TransactionHistoryWidgetState

    @Immutable
    data class Empty(
        val sendTransaction: ButtonState
    ) : TransactionHistoryWidgetState
}
