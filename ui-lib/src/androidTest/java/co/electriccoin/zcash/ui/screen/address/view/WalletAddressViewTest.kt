package co.electriccoin.zcash.ui.screen.address.view

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import cash.z.ecc.sdk.fixture.WalletAddressesFixture
import cash.z.ecc.sdk.model.WalletAddresses
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.test.getStringResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

@OptIn(ExperimentalCoroutinesApi::class)
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
        composeTestRule.onNodeWithText(getStringResource(R.string.wallet_address_shielded_sapling)).also {
            it.assertExists()
        }
        composeTestRule.onNodeWithText(getStringResource(R.string.wallet_address_transparent)).also {
            it.assertExists()
        }
        composeTestRule.onNodeWithText(getStringResource(R.string.wallet_address_viewing_key)).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithText(walletAddresses.unified.address).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithText(walletAddresses.shieldedSapling.address).also {
            it.assertDoesNotExist()
        }
        composeTestRule.onNodeWithText(walletAddresses.transparent.address).also {
            it.assertDoesNotExist()
        }
        composeTestRule.onNodeWithText(walletAddresses.viewingKey.encoding).also {
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
    fun shielded_sapling_expands() = runTest {
        val walletAddresses = WalletAddressesFixture.new()
        newTestSetup(walletAddresses)

        composeTestRule.onNodeWithText(walletAddresses.shieldedSapling.address).also {
            it.assertDoesNotExist()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.wallet_address_shielded_sapling)).also {
            it.assertExists()
            it.performClick()
        }

        composeTestRule.onNodeWithText(walletAddresses.shieldedSapling.address).also {
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
    fun viewing_expands() = runTest {
        val walletAddresses = WalletAddressesFixture.new()
        newTestSetup(walletAddresses)

        composeTestRule.onNodeWithText(walletAddresses.viewingKey.encoding).also {
            it.assertDoesNotExist()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.wallet_address_viewing_key)).also {
            it.assertExists()
            it.performClick()
        }

        composeTestRule.onNodeWithText(walletAddresses.viewingKey.encoding).also {
            it.assertExists()
        }
    }

    @Test
    @MediumTest
    fun back() = runTest {
        val testSetup = newTestSetup(WalletAddressesFixture.new())

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.onNodeWithContentDescription(getStringResource(R.string.wallet_address_back_content_description)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnBackCount())
    }

    private fun newTestSetup(initialState: WalletAddresses) = TestSetup(composeTestRule, initialState)

    private class TestSetup(private val composeTestRule: ComposeContentTestRule, initialState: WalletAddresses) {

        private val onBackCount = AtomicInteger(0)

        fun getOnBackCount(): Int {
            composeTestRule.waitForIdle()
            return onBackCount.get()
        }

        init {
            composeTestRule.setContent {
                ZcashTheme {
                    WalletAddresses(
                        initialState,
                        onBack = {
                            onBackCount.incrementAndGet()
                        }
                    )
                }
            }
        }
    }
}
