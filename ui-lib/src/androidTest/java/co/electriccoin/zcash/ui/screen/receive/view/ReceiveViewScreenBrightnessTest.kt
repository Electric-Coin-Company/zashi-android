package co.electriccoin.zcash.ui.screen.receive.view

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.filters.MediumTest
import cash.z.ecc.android.sdk.fixture.WalletAddressesFixture
import cash.z.ecc.android.sdk.model.WalletAddresses
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.common.compose.ScreenBrightnessState
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.fixture.VersionInfoFixture
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class ReceiveViewScreenBrightnessTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun testBrightnessDefaultState() =
        runTest {
            // Using isDebuggable flag to have brightness toggle in the UI
            val testSetup =
                newTestSetup(
                    WalletAddressesFixture.new(),
                    VersionInfoFixture.new(isDebuggable = true)
                )

            assertEquals(ScreenBrightnessState.NORMAL, testSetup.getScreenBrightness())
        }

    @Test
    @MediumTest
    fun testBrightnessOnState() =
        runTest {
            // Using isDebuggable flag to have brightness toggle in the UI
            val testSetup =
                newTestSetup(
                    WalletAddressesFixture.new(),
                    VersionInfoFixture.new(isDebuggable = true)
                )

            assertEquals(ScreenBrightnessState.NORMAL, testSetup.getOnAdjustBrightness())

            composeTestRule.clickAdjustBrightness()

            assertEquals(ScreenBrightnessState.FULL, testSetup.getOnAdjustBrightness())
        }

    private fun newTestSetup(
        walletAddresses: WalletAddresses,
        versionInfo: VersionInfo
    ) = ReceiveViewTestSetup(composeTestRule, walletAddresses, versionInfo)
}
