package co.electriccoin.zcash.ui.screen.account

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.fixture.BalanceStateFixture
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.fixture.ZashiMainTopAppBarStateFixture
import co.electriccoin.zcash.ui.screen.account.view.Account
import co.electriccoin.zcash.ui.screen.transactionhistory.widget.TransactionHistoryWidgetStateFixture
import java.util.concurrent.atomic.AtomicInteger

class AccountTestSetup(
    private val composeTestRule: ComposeContentTestRule,
    private val walletSnapshot: WalletSnapshot,
) {
    // TODO [#1282]: Update AccountView Tests #1282
    // TODO [#1282]: https://github.com/Electric-Coin-Company/zashi-android/issues/1282

    private val onSettingsCount = AtomicInteger(0)
    private val onHideBalancesCount = AtomicInteger(0)

    fun getOnSettingsCount(): Int {
        composeTestRule.waitForIdle()
        return onSettingsCount.get()
    }

    fun getOnHideBalancesCount(): Int {
        composeTestRule.waitForIdle()
        return onHideBalancesCount.get()
    }

    fun getWalletSnapshot(): WalletSnapshot {
        composeTestRule.waitForIdle()
        return walletSnapshot
    }

    @Composable
    @Suppress("TestFunctionName")
    fun DefaultContent(isHideBalances: Boolean) {
        Account(
            balanceState = BalanceStateFixture.new(),
            goBalances = {},
            hideStatusDialog = {},
            isHideBalances = isHideBalances,
            onContactSupport = {},
            showStatusDialog = null,
            snackbarHostState = SnackbarHostState(),
            zashiMainTopAppBarState =
                ZashiMainTopAppBarStateFixture.new(
                    settingsButton =
                        IconButtonState(
                            icon = R.drawable.ic_app_bar_settings,
                            contentDescription =
                                stringRes(co.electriccoin.zcash.ui.R.string.settings_menu_content_description),
                        ) {
                            onSettingsCount.incrementAndGet()
                        },
                    balanceVisibilityButton =
                        IconButtonState(
                            icon = R.drawable.ic_app_bar_balances_hide,
                            contentDescription =
                                stringRes(
                                    co.electriccoin.zcash.ui.R.string.hide_balances_content_description
                                ),
                        ) {
                            onHideBalancesCount.incrementAndGet()
                        },
                ),
            transactionHistoryWidgetState = TransactionHistoryWidgetStateFixture.new(),
            isWalletRestoringState = WalletRestoringState.NONE,
            walletSnapshot = WalletSnapshotFixture.new(),
            onStatusClick = {},
        )
    }

    fun setDefaultContent(isHideBalances: Boolean = false) {
        composeTestRule.setContent {
            ZcashTheme {
                DefaultContent(isHideBalances)
            }
        }
    }
}
