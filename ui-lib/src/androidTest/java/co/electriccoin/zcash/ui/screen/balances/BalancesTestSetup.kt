package co.electriccoin.zcash.ui.screen.balances

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.balances.model.ShieldState
import co.electriccoin.zcash.ui.screen.balances.view.Balances
import java.util.concurrent.atomic.AtomicInteger

class BalancesTestSetup(
    private val composeTestRule: ComposeContentTestRule,
    private val walletSnapshot: WalletSnapshot,
    private val isShowFiatConversion: Boolean
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
            onSettings = {
                onSettingsCount.incrementAndGet()
            },
            isFiatConversionEnabled = isShowFiatConversion,
            isKeepScreenOnWhileSyncing = false,
            isUpdateAvailable = false,
            onShielding = {},
            shieldState = ShieldState.Available,
            walletSnapshot = walletSnapshot,
            isShowingErrorDialog = false,
            setShowErrorDialog = {}
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
