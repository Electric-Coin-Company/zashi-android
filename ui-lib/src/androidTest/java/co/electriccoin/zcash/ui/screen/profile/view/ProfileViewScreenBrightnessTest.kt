package co.electriccoin.zcash.ui.screen.profile.view

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.filters.MediumTest
import cash.z.ecc.sdk.fixture.WalletAddressFixture
import cash.z.ecc.sdk.model.WalletAddress
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.common.LocalScreenBrightness
import co.electriccoin.zcash.ui.common.ScreenBrightness
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewScreenBrightnessTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun testFullBrightness() = runTest {
        val testSetup = newTestSetup(WalletAddressFixture.unified())

        assertEquals(1, testSetup.getSecureBrightnessCount())
    }

    private fun newTestSetup(walletAddress: WalletAddress) = TestSetup(composeTestRule, walletAddress)

    private class TestSetup(private val composeTestRule: ComposeContentTestRule, walletAddress: WalletAddress) {
        private val screenBrightness = ScreenBrightness()

        fun getSecureBrightnessCount() = screenBrightness.referenceCount.value

        init {
            composeTestRule.setContent {
                CompositionLocalProvider(LocalScreenBrightness provides screenBrightness) {
                    ZcashTheme {
                        ZcashTheme {
                            Profile(
                                walletAddress,
                                onBack = { },
                                onAddressDetails = { },
                                onAddressBook = { },
                                onSettings = { },
                                onCoinholderVote = {},
                                onSupport = { },
                                onAbout = { }
                            )
                        }
                    }
                }
            }
        }
    }
}
