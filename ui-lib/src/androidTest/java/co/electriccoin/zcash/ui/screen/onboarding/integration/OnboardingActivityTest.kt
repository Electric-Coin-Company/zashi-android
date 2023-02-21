package co.electriccoin.zcash.ui.screen.onboarding.integration

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.UiTestingActivity
import co.electriccoin.zcash.ui.screen.onboarding.OnboardingTestSetup
import co.electriccoin.zcash.ui.screen.onboarding.model.OnboardingStage
import co.electriccoin.zcash.ui.test.getStringResource
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class OnboardingActivityTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<UiTestingActivity>()

    private fun newTestSetup() = OnboardingTestSetup(
        composeTestRule,
        isFullOnboardingEnabled = true,
        OnboardingStage.ShieldedByDefault
    )

    @Test
    @MediumTest
    fun current_stage_restoration_activity() {
        val testSetup = newTestSetup()
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
