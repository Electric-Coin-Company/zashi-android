package co.electriccoin.zcash.ui.screen.account.history

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.account.model.TransactionUiState
import co.electriccoin.zcash.ui.screen.account.view.HistoryContainer
import java.util.concurrent.atomic.AtomicInteger

class HistoryTestSetup(
    private val composeTestRule: ComposeContentTestRule,
    initialHistoryUiState: TransactionUiState
) {
    private val onItemClickCount = AtomicInteger(0)
    private val onItemIdClickCount = AtomicInteger(0)

    fun getOnItemClickCount(): Int {
        composeTestRule.waitForIdle()
        return onItemClickCount.get()
    }

    fun getOnItemIdClickCount(): Int {
        composeTestRule.waitForIdle()
        return onItemIdClickCount.get()
    }

    init {
        composeTestRule.setContent {
            ZcashTheme {
                HistoryContainer(
                    transactionState = initialHistoryUiState,
                    onTransactionItemAction = {
                        onItemIdClickCount.incrementAndGet()
                    }
                )
            }
        }
    }
}
