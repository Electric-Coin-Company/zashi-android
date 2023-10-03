package co.electriccoin.zcash.ui.screen.support.view

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.test.getStringResource
import co.electriccoin.zcash.ui.test.getStringResourceWithArgs
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SupportViewTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    companion object {
        internal val DEFAULT_MESSAGE = "I can haz cheezburger?"
    }

    @Test
    @MediumTest
    fun back() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.clickBack()

        assertEquals(1, testSetup.getOnBackCount())
    }

    @Test
    @MediumTest
    fun send_shows_dialog() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnSendCount())
        assertEquals(null, testSetup.getSendMessage())

        composeTestRule.typeMessage()
        composeTestRule.clickSend()

        assertEquals(0, testSetup.getOnSendCount())

        val dialogContent = getStringResourceWithArgs(
            R.string.support_confirmation_explanation,
            getStringResource(R.string.app_name)
        )
        composeTestRule.onNodeWithText(dialogContent).also {
            it.assertExists()
        }
    }

    @Test
    @MediumTest
    fun dialog_confirm_sends() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnSendCount())
        assertEquals(null, testSetup.getSendMessage())

        composeTestRule.typeMessage()
        composeTestRule.clickSend()

        composeTestRule.onNodeWithText(getStringResource(R.string.support_confirmation_dialog_ok)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnSendCount())
        assertEquals(DEFAULT_MESSAGE, testSetup.getSendMessage())
    }

    @Test
    @MediumTest
    fun dialog_cancel() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnSendCount())
        assertEquals(null, testSetup.getSendMessage())

        composeTestRule.typeMessage()
        composeTestRule.clickSend()

        composeTestRule.onNodeWithText(getStringResource(R.string.support_confirmation_dialog_cancel)).also {
            it.performClick()
        }

        val dialogContent = getStringResourceWithArgs(
            R.string.support_confirmation_explanation,
            getStringResource(R.string.app_name)
        )
        composeTestRule.onNodeWithText(dialogContent).also {
            it.assertDoesNotExist()
        }

        assertEquals(0, testSetup.getOnSendCount())
        assertEquals(0, testSetup.getOnBackCount())
    }

    private fun newTestSetup() = SupportViewTestSetup(composeTestRule).apply {
        setDefaultContent()
    }
}

private fun ComposeContentTestRule.clickBack() {
    onNodeWithContentDescription(getStringResource(R.string.support_back_content_description)).also {
        it.performClick()
    }
}

private fun ComposeContentTestRule.clickSend() {
    onNodeWithText(getStringResource(R.string.support_send), ignoreCase = true).also {
        it.performClick()
    }
}

private fun ComposeContentTestRule.typeMessage() {
    onNodeWithText(getStringResource(R.string.support_hint)).also {
        it.performTextInput(SupportViewTest.DEFAULT_MESSAGE)
    }
}
