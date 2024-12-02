package co.electriccoin.zcash.ui.screen.balances

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.BalanceStateFixture
import co.electriccoin.zcash.ui.fixture.ZashiMainTopAppBarStateFixture
import co.electriccoin.zcash.ui.screen.balances.model.ShieldState
import co.electriccoin.zcash.ui.screen.balances.view.Balances
import java.util.concurrent.atomic.AtomicInteger

class BalancesTestSetup(
    private val composeTestRule: ComposeContentTestRule,
    private val walletSnapshot: WalletSnapshot,
) {
    private val onSettingsCount = AtomicInteger(0)

    fun getOnSettingsCount(): Int {
        composeTestRule.waitForIdle()
        return onSettingsCount.get()
    }

    fun getWalletSnapshot(): WalletSnapshot {
        composeTestRule.waitForIdle()
        return walletSnapshot
    }

    @Composable
    @Suppress("TestFunctionName")
    fun DefaultContent() {
        Balances(
            balanceState = BalanceStateFixture.new(),
            hideStatusDialog = {},
            isHideBalances = false,
            showStatusDialog = null,
            onStatusClick = {},
            snackbarHostState = SnackbarHostState(),
            isUpdateAvailable = false,
            isShowingErrorDialog = false,
            setShowErrorDialog = {},
            onContactSupport = {},
            onShielding = {},
            shieldState = ShieldState.Available,
            walletSnapshot = walletSnapshot,
            walletRestoringState = WalletRestoringState.NONE,
            zashiMainTopAppBarState =
                ZashiMainTopAppBarStateFixture.new(
                    onSettingsClick = {
                        onSettingsCount.incrementAndGet()
                    }
                )
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
