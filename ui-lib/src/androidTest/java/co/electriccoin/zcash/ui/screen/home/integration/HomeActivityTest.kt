package co.electriccoin.zcash.ui.screen.home.integration

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.filters.MediumTest
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.PercentDecimal
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.common.UiTestingActivity
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.home.HomeTag
import co.electriccoin.zcash.ui.screen.home.HomeTestSetup
import co.electriccoin.zcash.ui.screen.home.model.WalletSnapshot
import org.junit.Rule
import org.junit.Test

class HomeActivityTest : UiTestPrerequisites() {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<UiTestingActivity>()

    private fun newTestSetup(walletSnapshot: WalletSnapshot) = HomeTestSetup(
        composeTestRule,
        walletSnapshot,
        isShowFiatConversion = false,
        isCircularProgressBar = false
    )

    @Test
    @MediumTest
    fun open_close_drawer_menu_test() {
        val walletSnapshot = WalletSnapshotFixture.new(
            status = Synchronizer.Status.SYNCING,
            progress = PercentDecimal(0.5f)
        )
        val testSetup = newTestSetup(walletSnapshot)
        testSetup.setDefaultContent()

        composeTestRule.onNodeWithTag(HomeTag.DRAWER_MENU).also {
            it.assertIsNotDisplayed()
        }
        composeTestRule.onNodeWithTag(HomeTag.DRAWER_MENU_OPEN_BUTTON).also {
            it.assertIsDisplayed()
            it.performClick()
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(HomeTag.DRAWER_MENU).also {
            it.assertIsDisplayed()
        }

        Espresso.pressBack()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(HomeTag.DRAWER_MENU).also {
            it.assertIsNotDisplayed()
        }
    }
}
