package co.electriccoin.zcash.ui.screen.onboarding.view

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.onboarding.model.OnboardingStage
import co.electriccoin.zcash.ui.screen.onboarding.state.OnboardingState
import co.electriccoin.zcash.ui.test.getStringResource
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class OnboardingViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Sanity check the TestSetup
    @Test
    @MediumTest
    fun verify_test_setup_stage_1() {
        val testSetup = newTestSetup(OnboardingStage.ShieldedByDefault)

        assertEquals(OnboardingStage.ShieldedByDefault, testSetup.getOnboardingStage())
        assertEquals(0, testSetup.getOnImportWalletCallbackCount())
        assertEquals(0, testSetup.getOnCreateWalletCallbackCount())
    }

    @Test
    @MediumTest
    fun verify_test_setup_stage_4() {
        val testSetup = newTestSetup(OnboardingStage.Wallet)

        assertEquals(OnboardingStage.Wallet, testSetup.getOnboardingStage())
        assertEquals(0, testSetup.getOnImportWalletCallbackCount())
        assertEquals(0, testSetup.getOnCreateWalletCallbackCount())
    }

    @Test
    @MediumTest
    fun stage_1_layout() {
        newTestSetup(OnboardingStage.ShieldedByDefault)

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_skip)).also {
            it.assertExists()
            it.assertHasClickAction()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_next)).also {
            it.assertExists()
            it.assertHasClickAction()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_back)).also {
            it.assertDoesNotExist()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_1_header)).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_1_body)).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_4_create_new_wallet)).also {
            it.assertDoesNotExist()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_4_import_existing_wallet)).also {
            it.assertDoesNotExist()
        }
    }

    @Test
    @MediumTest
    fun stage_2_layout() {
        newTestSetup(OnboardingStage.UnifiedAddresses)

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_skip)).also {
            it.assertExists()
            it.assertHasClickAction()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_next)).also {
            it.assertExists()
            it.assertHasClickAction()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_back)).also {
            it.assertExists()
            it.assertHasClickAction()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_2_header)).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_2_body)).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_4_create_new_wallet)).also {
            it.assertDoesNotExist()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_4_import_existing_wallet)).also {
            it.assertDoesNotExist()
        }
    }

    @Test
    @MediumTest
    fun stage_3_layout() {
        newTestSetup(OnboardingStage.More)

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_skip)).also {
            it.assertExists()
            it.assertHasClickAction()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_next)).also {
            it.assertExists()
            it.assertHasClickAction()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_back)).also {
            it.assertExists()
            it.assertHasClickAction()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_3_header)).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_3_body)).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_4_create_new_wallet)).also {
            it.assertDoesNotExist()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_4_import_existing_wallet)).also {
            it.assertDoesNotExist()
        }
    }

    @Test
    @MediumTest
    fun stage_4_layout() {
        newTestSetup(OnboardingStage.Wallet)

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_skip)).also {
            it.assertDoesNotExist()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_next)).also {
            it.assertDoesNotExist()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_back)).also {
            it.assertExists()
            it.assertIsEnabled()
            it.assertHasClickAction()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_4_create_new_wallet)).also {
            it.assertExists()
            it.assertIsEnabled()
            it.assertHasClickAction()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_4_import_existing_wallet)).also {
            it.assertExists()
            it.assertIsEnabled()
            it.assertHasClickAction()
        }
    }

    @Test
    @MediumTest
    fun stage_1_skip() {
        val testSetup = newTestSetup(OnboardingStage.ShieldedByDefault)

        val skipButton = composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_skip))
        skipButton.performClick()

        assertEquals(OnboardingStage.Wallet, testSetup.getOnboardingStage())
    }

    @Test
    @MediumTest
    fun last_stage_click_create_wallet() {
        val testSetup = newTestSetup(OnboardingStage.Wallet)

        val newWalletButton = composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_4_create_new_wallet))
        newWalletButton.performClick()

        assertEquals(1, testSetup.getOnCreateWalletCallbackCount())
        assertEquals(0, testSetup.getOnImportWalletCallbackCount())
    }

    @Test
    @MediumTest
    fun last_stage_click_import_wallet() {
        val testSetup = newTestSetup(OnboardingStage.Wallet)

        val newWalletButton = composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_4_import_existing_wallet))
        newWalletButton.performClick()

        assertEquals(1, testSetup.getOnImportWalletCallbackCount())
        assertEquals(0, testSetup.getOnCreateWalletCallbackCount())
    }

    @Test
    @MediumTest
    fun multi_stage_progression() {
        val testSetup = newTestSetup(OnboardingStage.ShieldedByDefault)

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_next)).also {
            it.performClick()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_next)).also {
            it.performClick()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_next)).also {
            it.performClick()
        }

        assertEquals(OnboardingStage.Wallet, testSetup.getOnboardingStage())
    }

    @Test
    @MediumTest
    fun stage_restoration() {
        val testSetup = newTestSetup(OnboardingStage.ShieldedByDefault)

        assertEquals(OnboardingStage.ShieldedByDefault, testSetup.getOnboardingStage())

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_next)).also {
            it.performClick()
        }

        ActivityScenario.launch(MainActivity::class.java).also { scenario ->

            assertEquals(OnboardingStage.UnifiedAddresses, testSetup.getOnboardingStage())

            scenario.moveToState(Lifecycle.State.RESUMED)
            assert(scenario.state == Lifecycle.State.RESUMED)

            // recreates activity synchronously, it is ensured that the Activity state goes back to
            // the same state as its previous state
            scenario.recreate()

            assert(scenario.state == Lifecycle.State.RESUMED)
            assertEquals(OnboardingStage.UnifiedAddresses, testSetup.getOnboardingStage())
        }
    }

    private fun newTestSetup(initialStage: OnboardingStage) = TestSetup(composeTestRule, initialStage)

    private class TestSetup(private val composeTestRule: ComposeContentTestRule, initialStage: OnboardingStage) {
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

        init {
            composeTestRule.setContent {
                ZcashTheme {
                    Onboarding(
                        onboardingState,
                        onCreateWallet = { onCreateWalletCallbackCount.incrementAndGet() },
                        onImportWallet = { onImportWalletCallbackCount.incrementAndGet() }
                    )
                }
            }
        }
    }
}
