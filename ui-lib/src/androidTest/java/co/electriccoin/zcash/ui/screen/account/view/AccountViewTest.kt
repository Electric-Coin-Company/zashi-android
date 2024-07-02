package co.electriccoin.zcash.ui.screen.account.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.design.component.CommonTag
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.account.AccountTag
import co.electriccoin.zcash.ui.screen.account.AccountTestSetup
import co.electriccoin.zcash.ui.screen.send.clickHideBalances
import co.electriccoin.zcash.ui.screen.send.clickSettingsTopAppBarMenu
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

// TODO [#1194]: Cover Current balances UI widget with tests
// TODO [#1194]: https://github.com/Electric-Coin-Company/zashi-android/issues/1194

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

        composeTestRule.onNodeWithTag(AccountTag.BALANCE_VIEWS).also {
            it.assertIsDisplayed()
        }
    }

    @Test
    @MediumTest
    fun hamburger_settings_test() {
        val testSetup = newTestSetup()

        Assert.assertEquals(0, testSetup.getOnSettingsCount())

        composeTestRule.clickSettingsTopAppBarMenu()

        Assert.assertEquals(1, testSetup.getOnSettingsCount())
    }

    @Test
    @MediumTest
    fun hide_balances_btn_click_test() {
        val testSetup = newTestSetup()

        Assert.assertEquals(0, testSetup.getOnHideBalancesCount())

        composeTestRule.clickHideBalances()

        Assert.assertEquals(1, testSetup.getOnHideBalancesCount())
    }

    private fun newTestSetup(
        walletSnapshot: WalletSnapshot = WalletSnapshotFixture.new(),
        isHideBalances: Boolean = false
    ) = AccountTestSetup(
        composeTestRule,
        walletSnapshot = walletSnapshot,
    ).apply {
        setDefaultContent(isHideBalances)
    }
}
