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
                showWelcomeAnim = false, // It's fine to test the screen UI after the welcome animation
                isDebugMenuEnabled = false, // Debug only UI state does not need to be tested
                onImportWallet = { onImportWalletCallbackCount.incrementAndGet() },
                onCreateWallet = { onCreateWalletCallbackCount.incrementAndGet() },
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
