package co.electriccoin.zcash.ui.screen.securitywarning.view

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.test.getStringResource
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class SecurityWarningViewTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun default_ui_state_test() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnBackCount())
        assertEquals(false, testSetup.getOnAcknowledged())
        assertEquals(0, testSetup.getOnConfirmCount())

        composeTestRule.onNodeWithTag(SecurityScreenTag.ACKNOWLEDGE_CHECKBOX_TAG).also {
            it.assertExists()
            it.assertIsDisplayed()
            it.assertHasClickAction()
            it.assertIsEnabled()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.security_warning_confirm), ignoreCase = true).also {
            it.assertExists()
            it.assertIsDisplayed()
            it.assertHasClickAction()
            it.assertIsNotEnabled()
        }

        composeTestRule.onNodeWithTag(SecurityScreenTag.WARNING_TEXT_TAG).also {
            it.assertExists()
            it.assertIsDisplayed()
        }
    }

    @Test
    @MediumTest
    fun back_test() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.clickBack()

        assertEquals(1, testSetup.getOnBackCount())
    }

    @Test
    @MediumTest
    fun click_disabled_confirm_button_test() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnConfirmCount())
        assertEquals(false, testSetup.getOnAcknowledged())

        composeTestRule.clickConfirm()

        assertEquals(0, testSetup.getOnConfirmCount())
        assertEquals(false, testSetup.getOnAcknowledged())
    }

    @Test
    @MediumTest
    fun click_enabled_confirm_button_test() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnConfirmCount())
        assertEquals(false, testSetup.getOnAcknowledged())

        composeTestRule.clickAcknowledge()

        assertEquals(0, testSetup.getOnConfirmCount())
        assertEquals(true, testSetup.getOnAcknowledged())

        composeTestRule.clickConfirm()

        assertEquals(1, testSetup.getOnConfirmCount())
        assertEquals(true, testSetup.getOnAcknowledged())
    }

    private fun newTestSetup() =
        SecurityWarningViewTestSetup(composeTestRule).apply {
            setDefaultContent()
        }
}

private fun ComposeContentTestRule.clickBack() {
    onNodeWithContentDescription(getStringResource(R.string.support_back_content_description)).also {
        it.performClick()
    }
}

private fun ComposeContentTestRule.clickConfirm() {
    onNodeWithText(getStringResource(R.string.security_warning_confirm), ignoreCase = true).also {
        it.performClick()
    }
}

private fun ComposeContentTestRule.clickAcknowledge() {
    onNodeWithText(getStringResource(R.string.security_warning_acknowledge)).also {
        it.performClick()
    }
}
