package co.electriccoin.zcash.ui.screen.receive.view

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.filters.MediumTest
import cash.z.ecc.android.sdk.fixture.WalletAddressesFixture
import cash.z.ecc.android.sdk.model.WalletAddresses
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.fixture.VersionInfoFixture
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class ReceiveViewScreenTimeoutTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun testTimeoutDefaultState() =
        runTest {
            val testSetup =
                newTestSetup(
                    WalletAddressesFixture.new(),
                    VersionInfoFixture.new()
                )

            assertEquals(0, testSetup.getScreenTimeoutCount())
        }

    @Test
    @MediumTest
    fun testTimeoutOnState() =
        runTest {
            // Using isDebuggable flag to have brightness toggle in the UI
            val testSetup =
                newTestSetup(
                    WalletAddressesFixture.new(),
                    VersionInfoFixture.new(isDebuggable = true)
                )

            assertEquals(0, testSetup.getScreenTimeoutCount())

            composeTestRule.clickAdjustBrightness()

            assertEquals(1, testSetup.getScreenTimeoutCount())
        }

    private fun newTestSetup(
        walletAddresses: WalletAddresses,
        versionInfo: VersionInfo
    ) = ReceiveViewTestSetup(composeTestRule, walletAddresses, versionInfo)
}
