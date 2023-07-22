package co.electriccoin.zcash.ui.screen.address.view

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import cash.z.ecc.android.sdk.fixture.WalletAddressesFixture
import cash.z.ecc.android.sdk.model.WalletAddresses
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.address.WalletAddressesTag
import co.electriccoin.zcash.ui.test.getStringResource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class WalletAddressViewTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun initial_screen_setup() = runTest {
        val walletAddresses = WalletAddressesFixture.new()
        newTestSetup(walletAddresses)

        composeTestRule.onNodeWithText(getStringResource(R.string.wallet_address_unified)).also {
            it.assertExists()
        }
        composeTestRule.onNodeWithText(getStringResource(R.string.wallet_address_sapling)).also {
            it.assertExists()
        }
        composeTestRule.onNodeWithText(getStringResource(R.string.wallet_address_transparent)).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithText(walletAddresses.unified.address).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithText(walletAddresses.sapling.address).also {
            it.assertDoesNotExist()
        }
        composeTestRule.onNodeWithText(walletAddresses.transparent.address).also {
            it.assertDoesNotExist()
        }
    }

    @Test
    @MediumTest
    fun unified_collapses() = runTest {
        val walletAddresses = WalletAddressesFixture.new()
        newTestSetup(walletAddresses)

        composeTestRule.onNodeWithText(walletAddresses.unified.address).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.wallet_address_unified)).also {
            it.assertExists()
            it.performClick()
        }

        composeTestRule.onNodeWithText(walletAddresses.unified.address).also {
            it.assertDoesNotExist()
        }
    }

    @Test
    @MediumTest
    fun sapling_expands() = runTest {
        val walletAddresses = WalletAddressesFixture.new()
        newTestSetup(walletAddresses)

        composeTestRule.onNodeWithText(walletAddresses.sapling.address).also {
            it.assertDoesNotExist()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.wallet_address_sapling)).also {
            it.assertExists()
            it.performClick()
        }

        composeTestRule.onNodeWithText(walletAddresses.sapling.address).also {
            it.assertExists()
        }
    }

    @Test
    @MediumTest
    fun transparent_expands() = runTest {
        val walletAddresses = WalletAddressesFixture.new()
        newTestSetup(walletAddresses)

        composeTestRule.onNodeWithText(walletAddresses.transparent.address).also {
            it.assertDoesNotExist()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.wallet_address_transparent)).also {
            it.assertExists()
            it.performClick()
        }

        composeTestRule.onNodeWithText(walletAddresses.transparent.address).also {
            it.assertExists()
        }
    }

    @Test
    @MediumTest
    fun back_clicked() = runTest {
        val testSetup = newTestSetup(WalletAddressesFixture.new())

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.onNodeWithContentDescription(
            getStringResource(R.string.wallet_address_back_content_description)
        ).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnBackCount())
    }

    @Test
    @MediumTest
    fun copy_to_clipboard_clicked() = runTest {
        val testSetup = newTestSetup(WalletAddressesFixture.new())

        assertEquals(0, testSetup.getOnCopyToClipboardCount())

        composeTestRule.onNodeWithTag(
            WalletAddressesTag.WALLET_ADDRESS
        ).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnCopyToClipboardCount())
    }

    private fun newTestSetup(initialState: WalletAddresses) = TestSetup(composeTestRule, initialState)

    private class TestSetup(private val composeTestRule: ComposeContentTestRule, initialState: WalletAddresses) {

        private val onBackCount = AtomicInteger(0)
        private val onCopyToClipboardCount = AtomicInteger(0)

        fun getOnBackCount(): Int {
            composeTestRule.waitForIdle()
            return onBackCount.get()
        }

        fun getOnCopyToClipboardCount(): Int {
            composeTestRule.waitForIdle()
            return onCopyToClipboardCount.get()
        }

        init {
            composeTestRule.setContent {
                ZcashTheme {
                    WalletAddresses(
                        walletAddresses = initialState,
                        onCopyToClipboard = {
                            onCopyToClipboardCount.incrementAndGet()
                        },
                        onBack = {
                            onBackCount.incrementAndGet()
                        }
                    )
                }
            }
        }
    }
}
