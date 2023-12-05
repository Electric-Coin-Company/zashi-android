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
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.home.HomeTag
import co.electriccoin.zcash.ui.screen.home.HomeTestSetup
import co.electriccoin.zcash.ui.screen.home.model.WalletSnapshot
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

        composeTestRule.onNodeWithContentDescription(getStringResource(R.string.home_menu_content_description)).also {
            it.assertIsDisplayed()
        }

        composeTestRule.onNodeWithTag(HomeTag.STATUS_VIEWS).also {
            it.assertIsDisplayed()
        }

        composeTestRule.onNodeWithTag(HomeTag.FIAT_CONVERSION).also {
            it.assertIsDisplayed()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.home_button_send), ignoreCase = true).also {
            it.assertIsDisplayed()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.home_button_receive), ignoreCase = true).also {
            it.assertIsDisplayed()
        }
    }

    @Test
    @MediumTest
    fun hide_fiat_conversion() {
        newTestSetup(isShowFiatConversion = false)

        composeTestRule.onNodeWithTag(HomeTag.FIAT_CONVERSION).also {
            it.assertDoesNotExist()
        }
    }

    @Test
    @MediumTest
    fun click_receive_button() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnReceiveCount())

        composeTestRule.clickReceive()

        assertEquals(1, testSetup.getOnReceiveCount())
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
    fun click_history_button() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnHistoryCount())

        composeTestRule.clickHistory()

        assertEquals(1, testSetup.getOnHistoryCount())
    }

    @Test
    @MediumTest
    fun hamburger_settings() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnReceiveCount())

        composeTestRule.openSettings()

        assertEquals(1, testSetup.getOnSettingsCount())
    }

    private fun newTestSetup(
        isShowFiatConversion: Boolean = true,
        walletSnapshot: WalletSnapshot = WalletSnapshotFixture.new()
    ) = HomeTestSetup(
        composeTestRule,
        walletSnapshot = walletSnapshot,
        isShowFiatConversion = isShowFiatConversion
    ).apply {
        setDefaultContent()
    }
}

private fun ComposeContentTestRule.openSettings() {
    onNodeWithContentDescription(getStringResource(R.string.home_menu_content_description)).also {
        it.performClick()
    }
}

private fun ComposeContentTestRule.clickReceive() {
    onNodeWithText(getStringResource(R.string.home_button_receive), ignoreCase = true).also {
        it.performClick()
    }
}

private fun ComposeContentTestRule.clickSend() {
    onNodeWithText(getStringResource(R.string.home_button_send), ignoreCase = true).also {
        it.performScrollTo()
        it.performClick()
    }
}

private fun ComposeContentTestRule.clickHistory() {
    onNodeWithText(getStringResource(R.string.home_button_history), ignoreCase = true).also {
        it.performScrollTo()
        it.performClick()
    }
}
