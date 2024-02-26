package co.electriccoin.zcash.ui.screen.balances.integration

import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.filters.MediumTest
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.PercentDecimal
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.balances.BalancesTag
import co.electriccoin.zcash.ui.screen.balances.BalancesTestSetup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test

class BalancesViewIntegrationTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun newTestSetup(walletSnapshot: WalletSnapshot) =
        BalancesTestSetup(
            composeTestRule,
            walletSnapshot,
            isShowFiatConversion = true
        )

    // This is just basic sanity check that we still have UI set up as expected after the state restore
    @Test
    @MediumTest
    fun wallet_snapshot_restoration() {
        val restorationTester = StateRestorationTester(composeTestRule)
        val walletSnapshot =
            WalletSnapshotFixture.new(
                status = Synchronizer.Status.SYNCING,
                progress = PercentDecimal(0.5f)
            )
        val testSetup = newTestSetup(walletSnapshot)

        restorationTester.setContent {
            testSetup.DefaultContent()
        }

        assertNotEquals(WalletSnapshotFixture.STATUS, testSetup.getWalletSnapshot().status)
        assertEquals(Synchronizer.Status.SYNCING, testSetup.getWalletSnapshot().status)

        assertNotEquals(WalletSnapshotFixture.PROGRESS, testSetup.getWalletSnapshot().progress)
        assertEquals(0.5f, testSetup.getWalletSnapshot().progress.decimal)

        restorationTester.emulateSavedInstanceStateRestore()

        assertNotEquals(WalletSnapshotFixture.STATUS, testSetup.getWalletSnapshot().status)
        assertEquals(Synchronizer.Status.SYNCING, testSetup.getWalletSnapshot().status)

        assertNotEquals(WalletSnapshotFixture.PROGRESS, testSetup.getWalletSnapshot().progress)
        assertEquals(0.5f, testSetup.getWalletSnapshot().progress.decimal)

        composeTestRule.onNodeWithTag(BalancesTag.STATUS).also {
            it.assertExists()
            it.assertWidthIsAtLeast(1.dp)
        }
    }
}
