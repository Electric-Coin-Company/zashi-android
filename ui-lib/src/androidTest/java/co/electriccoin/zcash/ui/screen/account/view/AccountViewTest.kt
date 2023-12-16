package co.electriccoin.zcash.ui.screen.account.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.design.component.CommonTag
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.account.AccountTag
import co.electriccoin.zcash.ui.screen.account.AccountTestSetup
import co.electriccoin.zcash.ui.screen.send.clickSettingsTopAppBarMenu
import co.electriccoin.zcash.ui.test.getStringResource
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class AccountViewTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun check_all_elementary_ui_elements_displayed() {
        newTestSetup()

        composeTestRule.onNodeWithTag(CommonTag.TOP_APP_BAR)
            .also {
                it.assertIsDisplayed()
            }

        composeTestRule.onNodeWithTag(AccountTag.STATUS_VIEWS).also {
            it.assertIsDisplayed()
        }

        composeTestRule.onNodeWithTag(AccountTag.FIAT_CONVERSION).also {
            it.assertIsDisplayed()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.account_button_history), ignoreCase = true).also {
            it.assertIsDisplayed()
        }
    }

    @Test
    @MediumTest
    fun hide_fiat_conversion() {
        newTestSetup(isShowFiatConversion = false)

        composeTestRule.onNodeWithTag(AccountTag.FIAT_CONVERSION).also {
            it.assertDoesNotExist()
        }
    }

    @Test
    @MediumTest
    fun click_history_button() {
        val testSetup = newTestSetup()

        Assert.assertEquals(0, testSetup.getOnHistoryCount())

        composeTestRule.clickHistory()

        Assert.assertEquals(1, testSetup.getOnHistoryCount())
    }

    @Test
    @MediumTest
    fun hamburger_settings_test() {
        val testSetup = newTestSetup()

        Assert.assertEquals(0, testSetup.getOnReceiveCount())

        composeTestRule.clickSettingsTopAppBarMenu()

        Assert.assertEquals(1, testSetup.getOnSettingsCount())
    }

    private fun newTestSetup(
        isShowFiatConversion: Boolean = true,
        walletSnapshot: WalletSnapshot = WalletSnapshotFixture.new()
    ) = AccountTestSetup(
        composeTestRule,
        walletSnapshot = walletSnapshot,
        isShowFiatConversion = isShowFiatConversion
    ).apply {
        setDefaultContent()
    }
}

private fun ComposeContentTestRule.clickHistory() {
    onNodeWithText(getStringResource(R.string.home_button_history), ignoreCase = true).also {
        it.performScrollTo()
        it.performClick()
    }
}
