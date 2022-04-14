package co.electriccoin.zcash.ui.screen.onboarding.view

import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.onboarding.TestOnboardingActivity
import co.electriccoin.zcash.ui.screen.onboarding.model.OnboardingStage
import co.electriccoin.zcash.ui.test.getStringResource
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

// TODO [#382]: https://github.com/zcash/secant-android-wallet/issues/382
class OnboardingIntegrationTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestOnboardingActivity>()

    private fun newTestSetup(initialStage: OnboardingStage) = OnboardingTestSetup(composeTestRule, initialStage)

    /**
     * The test semantics are built upon StateRestorationTester component. We simulate screen state
     * restoration with method emulateSavedInstanceStateRestore(), which needs to have setContent()
     * method called beforehand. Then, after state restores after emulateSavedInstanceStateRestore(),
     * setContent() callback is called again. Thus we can null the testSetup variable, call state
     * restoration, and then make the assertion on it again.
     */
    @Test
    @MediumTest
    fun current_stage_restoration() {
        val restorationTester = StateRestorationTester(composeTestRule)
        val testSetup = newTestSetup(OnboardingStage.UnifiedAddresses)

        restorationTester.setContent {
            testSetup.getDefaultContent()
        }

        assertEquals(OnboardingStage.UnifiedAddresses, testSetup.getOnboardingStage())

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_next)).also {
            it.performClick()
        }

        assertEquals(OnboardingStage.More, testSetup.getOnboardingStage())

        restorationTester.emulateSavedInstanceStateRestore()

        assertEquals(OnboardingStage.More, testSetup.getOnboardingStage())
    }

    @Test
    @MediumTest
    fun current_stage_restoration_activity() {
        val testSetup = newTestSetup(OnboardingStage.ShieldedByDefault)
        testSetup.setDefaultContent()

        assertEquals(OnboardingStage.ShieldedByDefault, testSetup.getOnboardingStage())

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_next)).also {
            it.performClick()
        }

        assertEquals(OnboardingStage.UnifiedAddresses, testSetup.getOnboardingStage())

        composeTestRule.activityRule.scenario.onActivity {
            it.recreate()
        }

        assertEquals(OnboardingStage.UnifiedAddresses, testSetup.getOnboardingStage())
    }
}
