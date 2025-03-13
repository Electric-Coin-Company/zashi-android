package co.electriccoin.zcash.ui.screen.balances.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.design.component.CommonTag
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.balances.BalancesTestSetup
import co.electriccoin.zcash.ui.screen.send.clickSettingsTopAppBarMenu
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import kotlin.test.DefaultAsserter.assertEquals

// TODO [#1227]: Cover Balances UI and logic with tests
// TODO [#1227]: https://github.com/Electric-Coin-Company/zashi-android/issues/1227

class BalancesViewTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun newTestSetup(walletSnapshot: WalletSnapshot = WalletSnapshotFixture.new()) =
        BalancesTestSetup(
            composeTestRule,
            walletSnapshot = walletSnapshot,
        ).apply {
            setDefaultContent()
        }

    @Test
    @MediumTest
    fun check_all_elementary_ui_elements_displayed() {
        newTestSetup()

        composeTestRule
            .onNodeWithTag(CommonTag.TOP_APP_BAR)
            .also {
                it.assertIsDisplayed()
            }
    }

    @Test
    @MediumTest
    fun hamburger_settings_test() {
        val testSetup = newTestSetup()

        assertEquals("Failed in comparison", 0, testSetup.getOnSettingsCount())

        composeTestRule.clickSettingsTopAppBarMenu()

        Assert.assertEquals(1, testSetup.getOnSettingsCount())
    }
}
