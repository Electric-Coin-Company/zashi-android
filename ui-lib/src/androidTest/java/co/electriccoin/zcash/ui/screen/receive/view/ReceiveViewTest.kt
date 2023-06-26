package co.electriccoin.zcash.ui.screen.receive.view

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import cash.z.ecc.android.sdk.fixture.WalletAddressFixture
import cash.z.ecc.android.sdk.model.WalletAddress
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.test.getStringResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

/*
 * Note: It is difficult to test the QR code from automated tests.  There is a manual test case
 * for that currently.  A future enhancement could take a screenshot and try to analyze the
 * screenshot contents.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ReceiveViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun setup() = runTest {
        val walletAddress = WalletAddressFixture.unified()
        newTestSetup(walletAddress)

        // Enable substring for ellipsizing
        composeTestRule.onNodeWithText(walletAddress.address, substring = true).also {
            it.assertExists()
        }
    }

    @Test
    @MediumTest
    fun back() = runTest {
        val testSetup = newTestSetup(WalletAddressFixture.unified())

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.onNodeWithContentDescription(
            getStringResource(R.string.receive_back_content_description)
        ).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnBackCount())
    }

    @Test
    @MediumTest
    fun address_details() = runTest {
        val testSetup = newTestSetup(WalletAddressFixture.unified())

        assertEquals(0, testSetup.getOnAddressDetailsCount())

        composeTestRule.onNodeWithText(getStringResource(R.string.receive_see_address_details)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnAddressDetailsCount())
    }

    private fun newTestSetup(walletAddress: WalletAddress) = TestSetup(composeTestRule, walletAddress)

    private class TestSetup(private val composeTestRule: ComposeContentTestRule, walletAddress: WalletAddress) {

        private val onBackCount = AtomicInteger(0)
        private val onAddressDetailsCount = AtomicInteger(0)

        fun getOnBackCount(): Int {
            composeTestRule.waitForIdle()
            return onBackCount.get()
        }

        fun getOnAddressDetailsCount(): Int {
            composeTestRule.waitForIdle()
            return onAddressDetailsCount.get()
        }

        init {
            composeTestRule.setContent {
                ZcashTheme {
                    Receive(
                        walletAddress,
                        onBack = {
                            onBackCount.getAndIncrement()
                        },
                        onAddressDetails = {
                            onAddressDetailsCount.getAndIncrement()
                        }
                    )
                }
            }
        }
    }
}
