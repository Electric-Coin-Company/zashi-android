package co.electriccoin.zcash.ui.screen.profile.view

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.filters.MediumTest
import cash.z.ecc.android.sdk.fixture.WalletAddressFixture
import cash.z.ecc.android.sdk.model.WalletAddress
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.profile.util.ProfileConfiguration
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
class ProfileViewTest {
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

        composeTestRule.onNodeWithContentDescription(getStringResource(R.string.profile_back_content_description)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnBackCount())
    }

    @Test
    @MediumTest
    fun address_details() = runTest {
        val testSetup = newTestSetup(WalletAddressFixture.unified())

        assertEquals(0, testSetup.getOnAddressDetailsCount())

        composeTestRule.onNodeWithText(getStringResource(R.string.profile_see_address_details)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnAddressDetailsCount())
    }

    @Test
    @MediumTest
    fun address_book() = runTest {
        if (ProfileConfiguration.IS_ADDRESS_BOOK_ENABLED) {
            val testSetup = newTestSetup(WalletAddressFixture.unified())

            assertEquals(0, testSetup.getOnAddressBookCount())

            composeTestRule.onNodeWithText(getStringResource(R.string.profile_address_book)).also {
                it.performClick()
            }

            assertEquals(1, testSetup.getOnAddressBookCount())
        }
    }

    @Test
    @MediumTest
    fun settings() = runTest {
        val testSetup = newTestSetup(WalletAddressFixture.unified())

        assertEquals(0, testSetup.getOnSettingsCount())

        composeTestRule.onNodeWithText(getStringResource(R.string.profile_settings)).also {
            it.performScrollTo()
            it.performClick()
        }

        assertEquals(1, testSetup.getOnSettingsCount())
    }

    @Test
    @MediumTest
    fun coinholder_vote() = runTest {
        if (ProfileConfiguration.IS_COINHOLDER_VOTE_ENABLED) {
            val testSetup = newTestSetup(WalletAddressFixture.unified())

            assertEquals(0, testSetup.getOnCoinholderVoteCount())

            composeTestRule.onNodeWithText(getStringResource(R.string.profile_coinholder_vote)).also {
                it.performScrollTo()
                it.performClick()
            }

            assertEquals(1, testSetup.getOnCoinholderVoteCount())
        }
    }

    @Test
    @MediumTest
    fun support() = runTest {
        val testSetup = newTestSetup(WalletAddressFixture.unified())

        assertEquals(0, testSetup.getOnSupportCount())

        composeTestRule.onNodeWithText(getStringResource(R.string.profile_support)).also {
            it.performScrollTo()
            it.assertExists()
            it.performClick()
        }

        assertEquals(1, testSetup.getOnSupportCount())
    }

    @Test
    @MediumTest
    fun about() = runTest {
        val testSetup = newTestSetup(WalletAddressFixture.unified())

        assertEquals(0, testSetup.getOnAboutCount())

        composeTestRule.onNodeWithText(getStringResource(R.string.profile_about)).also {
            it.performScrollTo()
            it.assertExists()
            it.performClick()
        }

        assertEquals(1, testSetup.getOnAboutCount())
    }

    private fun newTestSetup(walletAddress: WalletAddress) = TestSetup(composeTestRule, walletAddress)

    private class TestSetup(private val composeTestRule: ComposeContentTestRule, walletAddress: WalletAddress) {

        private val onBackCount = AtomicInteger(0)
        private val onAddressDetailsCount = AtomicInteger(0)
        private val onAddressBookCount = AtomicInteger(0)
        private val onSettingsCount = AtomicInteger(0)
        private val onCoinholderVoteCount = AtomicInteger(0)
        private val onSupportCount = AtomicInteger(0)
        private val onAboutCount = AtomicInteger(0)

        fun getOnBackCount(): Int {
            composeTestRule.waitForIdle()
            return onBackCount.get()
        }

        fun getOnAddressDetailsCount(): Int {
            composeTestRule.waitForIdle()
            return onAddressDetailsCount.get()
        }

        fun getOnAddressBookCount(): Int {
            composeTestRule.waitForIdle()
            return onAddressBookCount.get()
        }

        fun getOnSettingsCount(): Int {
            composeTestRule.waitForIdle()
            return onSettingsCount.get()
        }

        fun getOnCoinholderVoteCount(): Int {
            composeTestRule.waitForIdle()
            return onCoinholderVoteCount.get()
        }

        fun getOnSupportCount(): Int {
            composeTestRule.waitForIdle()
            return onSupportCount.get()
        }

        fun getOnAboutCount(): Int {
            composeTestRule.waitForIdle()
            return onAboutCount.get()
        }

        init {
            composeTestRule.setContent {
                ZcashTheme {
                    Profile(
                        walletAddress,
                        onBack = {
                            onBackCount.getAndIncrement()
                        },
                        onAddressDetails = {
                            onAddressDetailsCount.getAndIncrement()
                        },
                        onAddressBook = {
                            onAddressBookCount.getAndIncrement()
                        },
                        onSettings = {
                            onSettingsCount.getAndIncrement()
                        },
                        onCoinholderVote = {
                            onCoinholderVoteCount.getAndIncrement()
                        },
                        onSupport = {
                            onSupportCount.getAndIncrement()
                        },
                        onAbout = {
                            onAboutCount.getAndIncrement()
                        }
                    )
                }
            }
        }
    }
}
