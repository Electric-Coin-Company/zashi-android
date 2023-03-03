package co.electriccoin.zcash.ui.screen.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.onboarding.view.ShortOnboarding
import java.util.concurrent.atomic.AtomicInteger

class ShortOnboardingTestSetup(
    private val composeTestRule: ComposeContentTestRule,
) {
    private val onCreateWalletCallbackCount = AtomicInteger(0)
    private val onImportWalletCallbackCount = AtomicInteger(0)

    fun getOnCreateWalletCallbackCount(): Int {
        composeTestRule.waitForIdle()
        return onCreateWalletCallbackCount.get()
    }

    fun getOnImportWalletCallbackCount(): Int {
        composeTestRule.waitForIdle()
        return onImportWalletCallbackCount.get()
    }

    @Composable
    @Suppress("TestFunctionName")
    fun DefaultContent() {
        ZcashTheme {
            ShortOnboarding(
                isDebugMenuEnabled = false,
                onCreateWallet = { onCreateWalletCallbackCount.incrementAndGet() },
                onImportWallet = { onImportWalletCallbackCount.incrementAndGet() },
                // We aren't testing this because it is for debug builds only.
                onFixtureWallet = {}
            )
        }
    }

    fun setDefaultContent() {
        composeTestRule.setContent {
            DefaultContent()
        }
    }
}
