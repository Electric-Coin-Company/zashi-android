package co.electriccoin.zcash.ui.screen.account

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.BalanceStateFixture
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.account.history.fixture.TransactionHistoryUiStateFixture
import co.electriccoin.zcash.ui.screen.account.model.TransactionUiState
import co.electriccoin.zcash.ui.screen.account.view.Account
import java.util.concurrent.atomic.AtomicInteger

class AccountTestSetup(
    private val composeTestRule: ComposeContentTestRule,
    private val walletSnapshot: WalletSnapshot,
) {
    // TODO [#1282]: Update AccountView Tests #1282
    // TODO [#1282]: https://github.com/Electric-Coin-Company/zashi-android/issues/1282

    val initialTransactionState: TransactionUiState = TransactionHistoryUiStateFixture.new()

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
            balanceState = BalanceStateFixture.new(),
            goSettings = {
                onSettingsCount.incrementAndGet()
            },
            goBalances = {},
            transactionsUiState = initialTransactionState,
            onTransactionItemAction = {
                onItemClickCount.incrementAndGet()
            },
            hideStatusDialog = {},
            showStatusDialog = null,
            onStatusClick = {},
            snackbarHostState = SnackbarHostState(),
            walletRestoringState = WalletRestoringState.NONE,
            walletSnapshot = WalletSnapshotFixture.new()
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
