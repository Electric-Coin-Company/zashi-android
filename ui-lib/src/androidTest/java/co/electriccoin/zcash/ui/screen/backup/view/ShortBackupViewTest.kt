package co.electriccoin.zcash.ui.screen.backup.view

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.test.getStringResource
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ShortBackupViewTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun newTestSetup(): ShortBackupTestSetup {
        return ShortBackupTestSetup(composeTestRule).apply {
            setDefaultContent()
        }
    }

    @Test
    @MediumTest
    fun copy_to_clipboard() {
        val testSetup = newTestSetup()

        composeTestRule.copyToClipboard()

        assertEquals(1, testSetup.getOnCopyToClipboardCount())
    }

    @Test
    @MediumTest
    fun click_finish() {
        val testSetup = newTestSetup()

        composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_short_button_finished)).also {
            it.performClick()
        }

        assertEquals(0, testSetup.getOnCopyToClipboardCount())
        assertEquals(1, testSetup.getOnCompleteCallbackCount())
    }
}

private fun ComposeContentTestRule.copyToClipboard() {
    // open menu
    onNodeWithContentDescription(
        getStringResource(R.string.new_wallet_toolbar_more_button_content_description)
    ).also { moreMenu ->
        moreMenu.performClick()
    }

    // click menu button
    onNodeWithText(
        getStringResource(R.string.new_wallet_short_copy)
    ).also { menuButton ->
        menuButton.performClick()
    }
}
