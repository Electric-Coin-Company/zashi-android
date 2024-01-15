package co.electriccoin.zcash.ui.screen.receive.view

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import cash.z.ecc.android.sdk.fixture.WalletAddressesFixture
import cash.z.ecc.android.sdk.model.WalletAddresses
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.test.getStringResource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

// TODO [#1184]: Improve ReceiveScreen UI tests
// TODO [#1184]: https://github.com/Electric-Coin-Company/zashi-android/issues/1184

/*
 * Note: It is difficult to test the QR code from automated tests.  There is a manual test case
 * for that currently.  A future enhancement could take a screenshot and try to analyze the
 * screenshot contents.
 */
class ReceiveViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun setup() =
        runTest {
            val walletAddresses = WalletAddressesFixture.new()
            newTestSetup(walletAddresses)

            // Enable substring for ellipsizing
            composeTestRule.onNodeWithText(walletAddresses.unified.address, substring = true).also {
                it.assertExists()
            }
        }

    @Test
    @MediumTest
    fun click_settings_test() =
        runTest {
            val testSetup = newTestSetup(WalletAddressesFixture.new())

            assertEquals(0, testSetup.getOnSettingsCount())

            composeTestRule.onNodeWithContentDescription(
                getStringResource(R.string.settings_menu_content_description)
            ).also {
                it.performClick()
            }

            assertEquals(1, testSetup.getOnSettingsCount())
        }

    private fun newTestSetup(walletAddresses: WalletAddresses) = ReceiveViewTestSetup(composeTestRule, walletAddresses)
}
