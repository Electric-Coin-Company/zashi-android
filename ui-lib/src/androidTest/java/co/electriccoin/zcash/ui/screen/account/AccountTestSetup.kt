package co.electriccoin.zcash.ui.screen.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.account.history.fixture.TransactionHistorySyncStateFixture
import co.electriccoin.zcash.ui.screen.account.state.TransactionHistorySyncState
import co.electriccoin.zcash.ui.screen.account.view.Account
import java.util.concurrent.atomic.AtomicInteger

class AccountTestSetup(
    private val composeTestRule: ComposeContentTestRule,
    private val walletSnapshot: WalletSnapshot,
) {
    // TODO [#1282]: Update AccountView Tests #1282
    // TODO [#1282]: https://github.com/Electric-Coin-Company/zashi-android/issues/1282

    val initialHistorySyncState: TransactionHistorySyncState = TransactionHistorySyncStateFixture.new()

    private val onSettingsCount = AtomicInteger(0)
    private val onReceiveCount = AtomicInteger(0)
    private val onSendCount = AtomicInteger(0)
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

    fun getOnSettingsCount(): Int {
        composeTestRule.waitForIdle()
        return onSettingsCount.get()
    }

    fun getOnReceiveCount(): Int {
        composeTestRule.waitForIdle()
        return onReceiveCount.get()
    }

    fun getOnSendCount(): Int {
        composeTestRule.waitForIdle()
        return onSendCount.get()
    }

    fun getWalletSnapshot(): WalletSnapshot {
        composeTestRule.waitForIdle()
        return walletSnapshot
    }

    @Composable
    @Suppress("TestFunctionName")
    fun DefaultContent() {
        Account(
            walletSnapshot = walletSnapshot,
            isKeepScreenOnWhileSyncing = false,
            goSettings = {
                onSettingsCount.incrementAndGet()
            },
            goBalances = {},
            transactionState = initialHistorySyncState,
            onItemClick = {
                onItemClickCount.incrementAndGet()
            },
            onTransactionIdClick = {
                onItemIdClickCount.incrementAndGet()
            }
        )
    }

    fun setDefaultContent() {
        composeTestRule.setContent {
            ZcashTheme {
                DefaultContent()
            }
        }
    }
}
