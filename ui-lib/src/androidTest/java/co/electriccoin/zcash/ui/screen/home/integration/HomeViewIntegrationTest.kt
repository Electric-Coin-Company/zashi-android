package co.electriccoin.zcash.ui.screen.home.integration

import androidx.compose.ui.test.junit4.createComposeRule
import co.electriccoin.zcash.test.UiTestPrerequisites
import org.junit.Rule

class HomeViewIntegrationTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    // TODO [#1125]: Home screen navigation: Add integration test
    // TODO [#1125]: https://github.com/Electric-Coin-Company/zashi-android/issues/1125

    /*
    private fun newTestSetup(walletSnapshot: WalletSnapshot) =
        HomeTestSetup(
            composeTestRule,
            walletSnapshot,
            isShowFiatConversion = false
        )

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

        composeTestRule.onNodeWithTag(AccountTag.SINGLE_LINE_TEXT).also {
            it.assertIsDisplayed()
            it.assertWidthIsAtLeast(1.dp)
        }
    }
     */
}
