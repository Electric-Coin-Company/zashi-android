package co.electriccoin.zcash.ui.screen.onboarding.integration

import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.onboarding.LongOnboardingTestSetup
import co.electriccoin.zcash.ui.screen.onboarding.model.OnboardingStage
import co.electriccoin.zcash.ui.test.getStringResource
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class OnboardingIntegrationTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun newTestSetup(initialStage: OnboardingStage) = LongOnboardingTestSetup(
        composeTestRule,
        initialStage
    )

    /**
     * The test semantics are built upon StateRestorationTester component. We simulate screen state
     * restoration with method emulateSavedInstanceStateRestore(), which needs to have setContent()
     * method called beforehand. Then, after state restores after emulateSavedInstanceStateRestore(),
     * setContent() callback is called again.
     */
    @Test
    @MediumTest
    fun current_stage_restoration() {
        val restorationTester = StateRestorationTester(composeTestRule)
        val testSetup = newTestSetup(OnboardingStage.UnifiedAddresses)

        restorationTester.setContent {
            testSetup.DefaultContent()
        }

        assertEquals(OnboardingStage.UnifiedAddresses, testSetup.getOnboardingStage())

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_next)).also {
            it.performClick()
        }

        assertEquals(OnboardingStage.More, testSetup.getOnboardingStage())

        restorationTester.emulateSavedInstanceStateRestore()

        assertEquals(OnboardingStage.More, testSetup.getOnboardingStage())
    }
}
