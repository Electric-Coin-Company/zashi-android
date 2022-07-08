package co.electriccoin.zcash.ui.screen.home.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.home.HomeTag
import co.electriccoin.zcash.ui.test.getStringResource
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeViewTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun check_all_elementary_ui_elements_displayed() {
        newTestSetup()

        composeTestRule.onNodeWithContentDescription(getStringResource(R.string.home_scan_content_description)).also {
            it.assertIsDisplayed()
        }

        composeTestRule.onNodeWithContentDescription(getStringResource(R.string.home_profile_content_description)).also {
            it.assertIsDisplayed()
        }

        composeTestRule.onNodeWithTag(HomeTag.STATUS).also {
            it.assertIsDisplayed()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.home_button_send)).also {
            it.assertIsDisplayed()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.home_button_request)).also {
            it.assertIsDisplayed()
        }
    }

    @Test
    @MediumTest
    fun click_scan_button() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnScanCount())

        composeTestRule.clickScan()

        assertEquals(1, testSetup.getOnScanCount())
    }

    @Test
    @MediumTest
    fun click_profile_button() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnProfileCount())

        composeTestRule.clickProfile()

        assertEquals(1, testSetup.getOnProfileCount())
    }

    @Test
    @MediumTest
    fun click_send_button() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnSendCount())

        composeTestRule.clickSend()

        assertEquals(1, testSetup.getOnSendCount())
    }

    @Test
    @MediumTest
    fun click_request_button() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnRequestCount())

        composeTestRule.clickRequest()

        assertEquals(1, testSetup.getOnRequestCount())
    }

    private fun newTestSetup() = HomeTestSetup(composeTestRule).apply {
        setDefaultContent()
    }
}

fun ComposeContentTestRule.clickScan() {
    onNodeWithContentDescription(getStringResource(R.string.home_scan_content_description)).also {
        it.performClick()
    }
}

private fun ComposeContentTestRule.clickProfile() {
    onNodeWithContentDescription(getStringResource(R.string.home_profile_content_description)).also {
        it.performClick()
    }
}

private fun ComposeContentTestRule.clickSend() {
    onNodeWithText(getStringResource(R.string.home_button_send)).also {
        it.performScrollTo()
        it.performClick()
    }
}

private fun ComposeContentTestRule.clickRequest() {
    onNodeWithText(getStringResource(R.string.home_button_request)).also {
        it.performScrollTo()
        it.performClick()
    }
}
