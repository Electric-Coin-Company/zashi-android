package co.electriccoin.zcash.ui.screen.support.view

import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.test.getStringResource
import co.electriccoin.zcash.ui.test.getStringResourceWithArgs
import org.junit.Rule
import org.junit.Test
import kotlin.test.Ignore

class SupportViewIntegrationTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun message_state_restoration() {
        val restorationTester = StateRestorationTester(composeTestRule)
        val testSetup = newTestSetup()

        restorationTester.setContent {
            ZcashTheme {
                testSetup.DefaultContent()
            }
        }

        composeTestRule.onNodeWithText("I can haz cheezburger?").also {
            it.assertDoesNotExist()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.support_hint)).also {
            it.performTextInput("I can haz cheezburger?")
        }

        composeTestRule.onNodeWithText("I can haz cheezburger?").also {
            it.assertExists()
        }

        restorationTester.emulateSavedInstanceStateRestore()

        composeTestRule.onNodeWithText("I can haz cheezburger?").also {
            it.assertExists()
        }
    }

    @Test
    @MediumTest
    @Ignore("Will be updated as part of #1275")
    fun dialog_state_restoration() {
        val restorationTester = StateRestorationTester(composeTestRule)
        val testSetup = newTestSetup()

        restorationTester.setContent {
            testSetup.DefaultContent()
        }

        composeTestRule.onNodeWithText("I can haz cheezburger?").also {
            it.assertDoesNotExist()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.support_send), ignoreCase = true).also {
            it.performClick()
        }

        restorationTester.emulateSavedInstanceStateRestore()

        val dialogContent =
            getStringResourceWithArgs(
                R.string.support_confirmation_explanation,
                getStringResource(R.string.app_name)
            )
        composeTestRule.onNodeWithText(dialogContent).also {
            it.assertExists()
        }
    }

    private fun newTestSetup() = SupportViewTestSetup(composeTestRule)
}
