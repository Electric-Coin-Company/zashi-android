package co.electriccoin.zcash.ui.screen.onboarding.view

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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

class LongOnboardingViewTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun newTestSetup(initialStage: OnboardingStage): LongOnboardingTestSetup {
        return LongOnboardingTestSetup(composeTestRule, initialStage).apply {
            setDefaultContent()
        }
    }

    // Sanity check the TestSetup
    @Test
    @MediumTest
    fun verify_test_setup_stage_1() {
        val testSetup = newTestSetup(initialStage = OnboardingStage.ShieldedByDefault)

        assertEquals(OnboardingStage.ShieldedByDefault, testSetup.getOnboardingStage())
        assertEquals(0, testSetup.getOnImportWalletCallbackCount())
        assertEquals(0, testSetup.getOnCreateWalletCallbackCount())
    }

    @Test
    @MediumTest
    fun verify_test_setup_stage_4() {
        val testSetup = newTestSetup(initialStage = OnboardingStage.Wallet)

        assertEquals(OnboardingStage.Wallet, testSetup.getOnboardingStage())
        assertEquals(0, testSetup.getOnImportWalletCallbackCount())
        assertEquals(0, testSetup.getOnCreateWalletCallbackCount())
    }

    @Test
    @MediumTest
    fun stage_1_layout() {
        newTestSetup(initialStage = OnboardingStage.ShieldedByDefault)

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_next)).also {
            it.assertExists()
            it.assertHasClickAction()
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
        newTestSetup(initialStage = OnboardingStage.UnifiedAddresses)

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_skip)).also {
            it.assertExists()
            it.assertHasClickAction()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_next)).also {
            it.assertExists()
            it.assertHasClickAction()
        }

        composeTestRule.onNodeWithContentDescription(getStringResource(R.string.onboarding_back)).also {
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
        newTestSetup(initialStage = OnboardingStage.More)

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_skip)).also {
            it.assertExists()
            it.assertHasClickAction()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_next)).also {
            it.assertExists()
            it.assertHasClickAction()
        }

        composeTestRule.onNodeWithContentDescription(getStringResource(R.string.onboarding_back)).also {
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
        newTestSetup(initialStage = OnboardingStage.Wallet)

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_skip)).also {
            it.assertDoesNotExist()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_next)).also {
            it.assertDoesNotExist()
        }

        composeTestRule.onNodeWithContentDescription(getStringResource(R.string.onboarding_back)).also {
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
        val testSetup = newTestSetup(initialStage = OnboardingStage.ShieldedByDefault)

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_skip)).also {
            it.performClick()
        }

        assertEquals(OnboardingStage.Wallet, testSetup.getOnboardingStage())
    }

    @Test
    @MediumTest
    fun last_stage_click_create_wallet() {
        val testSetup = newTestSetup(initialStage = OnboardingStage.Wallet)

        val newWalletButton = composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_4_create_new_wallet))
        newWalletButton.performClick()

        assertEquals(1, testSetup.getOnCreateWalletCallbackCount())
        assertEquals(0, testSetup.getOnImportWalletCallbackCount())
    }

    @Test
    @MediumTest
    fun last_stage_click_import_wallet() {
        val testSetup = newTestSetup(initialStage = OnboardingStage.Wallet)

        val newWalletButton = composeTestRule.onNodeWithText(
            getStringResource(R.string.onboarding_4_import_existing_wallet)
        )
        newWalletButton.performClick()

        assertEquals(1, testSetup.getOnImportWalletCallbackCount())
        assertEquals(0, testSetup.getOnCreateWalletCallbackCount())
    }

    @Test
    @MediumTest
    fun multi_stage_progression() {
        val testSetup = newTestSetup(initialStage = OnboardingStage.ShieldedByDefault)

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
}
