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
    private val onBackCount = AtomicInteger(0)

    fun getOnBackCount(): Int {
        composeTestRule.waitForIdle()
        return onBackCount.get()
    }

    init {
        composeTestRule.setContent {
            ZcashTheme {
                History(
                    transactionState = initialHistorySyncState,
                    goBack = {
                        onBackCount.incrementAndGet()
                    }
                )
            }
        }
    }
}
