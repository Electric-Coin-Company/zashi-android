package co.electriccoin.zcash.ui.screen.home.integration

import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.filters.MediumTest
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.PercentDecimal
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.home.HomeTag
import co.electriccoin.zcash.ui.screen.home.HomeTestSetup
import co.electriccoin.zcash.ui.screen.home.model.WalletSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test

class HomeViewIntegrationTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun newTestSetup(walletSnapshot: WalletSnapshot) = HomeTestSetup(
        composeTestRule,
        walletSnapshot,
        isShowFiatConversion = false
    )

    // This is just basic sanity check that we still have UI set up as expected after the state restore
    @Test
    @MediumTest
    fun wallet_snapshot_restoration() {
        val restorationTester = StateRestorationTester(composeTestRule)
        val walletSnapshot = WalletSnapshotFixture.new(
            status = Synchronizer.Status.DOWNLOADING,
            progress = PercentDecimal(0.5f)
        )
        val testSetup = newTestSetup(walletSnapshot)

        restorationTester.setContent {
            testSetup.DefaultContent()
        }

        assertNotEquals(WalletSnapshotFixture.STATUS, testSetup.getWalletSnapshot().status)
        assertEquals(Synchronizer.Status.DOWNLOADING, testSetup.getWalletSnapshot().status)

        assertNotEquals(WalletSnapshotFixture.PROGRESS, testSetup.getWalletSnapshot().progress)
        assertEquals(0.5f, testSetup.getWalletSnapshot().progress.decimal)

        restorationTester.emulateSavedInstanceStateRestore()

        assertNotEquals(WalletSnapshotFixture.STATUS, testSetup.getWalletSnapshot().status)
        assertEquals(Synchronizer.Status.DOWNLOADING, testSetup.getWalletSnapshot().status)

        assertNotEquals(WalletSnapshotFixture.PROGRESS, testSetup.getWalletSnapshot().progress)
        assertEquals(0.5f, testSetup.getWalletSnapshot().progress.decimal)

        composeTestRule.onNodeWithTag(HomeTag.PROGRESS).also {
            it.assertIsDisplayed()
            it.assertHeightIsAtLeast(1.dp)
        }

        composeTestRule.onNodeWithTag(HomeTag.PROGRESS).also {
            it.assertIsDisplayed()
            it.assertHeightIsAtLeast(1.dp)
        }

        composeTestRule.onNodeWithTag(HomeTag.SINGLE_LINE_TEXT).also {
            it.assertIsDisplayed()
            it.assertWidthIsAtLeast(1.dp)
        }
    }
}
