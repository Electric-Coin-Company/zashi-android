package co.electriccoin.zcash.ui.screen.account.integration

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.account.AccountTag
import co.electriccoin.zcash.ui.screen.account.AccountTestSetup
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AccountViewIntegrationTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun newTestSetup(walletSnapshot: WalletSnapshot) =
        AccountTestSetup(
            composeTestRule,
            walletSnapshot,
        )

    // This is just basic sanity check that we still have UI set up as expected after the state restore
    @Test
    @MediumTest
    fun wallet_snapshot_restoration() {
        val restorationTester = StateRestorationTester(composeTestRule)
        val walletSnapshot =
            WalletSnapshotFixture.new(
                saplingBalance = WalletSnapshotFixture.SAPLING_BALANCE,
                orchardBalance = WalletSnapshotFixture.ORCHARD_BALANCE,
                transparentBalance = WalletSnapshotFixture.TRANSPARENT_BALANCE
            )
        val testSetup = newTestSetup(walletSnapshot)

        restorationTester.setContent {
            ZcashTheme {
                testSetup.DefaultContent(isHideBalances = false)
            }
        }

        assertEquals(WalletSnapshotFixture.SAPLING_BALANCE, testSetup.getWalletSnapshot().saplingBalance)
        assertEquals(WalletSnapshotFixture.ORCHARD_BALANCE, testSetup.getWalletSnapshot().orchardBalance)
        assertEquals(WalletSnapshotFixture.TRANSPARENT_BALANCE, testSetup.getWalletSnapshot().transparentBalance)

        restorationTester.emulateSavedInstanceStateRestore()

        assertEquals(WalletSnapshotFixture.SAPLING_BALANCE, testSetup.getWalletSnapshot().saplingBalance)
        assertEquals(WalletSnapshotFixture.ORCHARD_BALANCE, testSetup.getWalletSnapshot().orchardBalance)
        assertEquals(WalletSnapshotFixture.TRANSPARENT_BALANCE, testSetup.getWalletSnapshot().transparentBalance)

        composeTestRule.onNodeWithTag(AccountTag.BALANCE_VIEWS).also {
            it.assertIsDisplayed()
            it.assertWidthIsAtLeast(1.dp)
        }
    }
}
