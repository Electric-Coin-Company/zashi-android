package co.electriccoin.zcash.ui.screen.onboarding

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.onboarding.model.OnboardingStage
import co.electriccoin.zcash.ui.screen.onboarding.state.OnboardingState
import co.electriccoin.zcash.ui.screen.onboarding.view.Onboarding
import java.util.concurrent.atomic.AtomicInteger

class OnboardingTestSetup(
    private val composeTestRule: ComposeContentTestRule,
    initialStage: OnboardingStage
) {
    private val onboardingState = OnboardingState(initialStage)

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

    fun getOnboardingStage(): OnboardingStage {
        composeTestRule.waitForIdle()
        return onboardingState.current.value
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun getDefaultContent() {
        ZcashTheme {
            Onboarding(
                onboardingState,
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
            getDefaultContent()
        }
    }
}
