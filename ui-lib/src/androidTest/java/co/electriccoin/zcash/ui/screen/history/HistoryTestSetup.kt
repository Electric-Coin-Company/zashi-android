package co.electriccoin.zcash.ui.screen.history

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.history.state.TransactionHistorySyncState
import co.electriccoin.zcash.ui.screen.history.view.History
import java.util.concurrent.atomic.AtomicInteger

class HistoryTestSetup(
    private val composeTestRule: ComposeContentTestRule,
    initialHistorySyncState: TransactionHistorySyncState
) {
    private val onBackClickCount = AtomicInteger(0)
    private val onItemClickCount = AtomicInteger(0)
    private val onItemIdClickCount = AtomicInteger(0)

    fun getOnBackClickCount(): Int {
        composeTestRule.waitForIdle()
        return onBackClickCount.get()
    }

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
                History(
                    transactionState = initialHistorySyncState,
                    onBack = {
                        onBackClickCount.incrementAndGet()
                    },
                    onItemClick = {
                        onItemClickCount.incrementAndGet()
                    },
                    onTransactionIdClick = {
                        onItemIdClickCount.incrementAndGet()
                    }
                )
            }
        }
    }
}
