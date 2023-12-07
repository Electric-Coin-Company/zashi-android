package co.electriccoin.zcash.ui.screen.receive.view

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.filters.MediumTest
import cash.z.ecc.android.sdk.fixture.WalletAddressFixture
import cash.z.ecc.android.sdk.model.WalletAddress
import co.electriccoin.zcash.test.UiTestPrerequisites
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class ReceiveViewScreenTimeoutTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun testTimeoutDefaultState() = runTest {
        val testSetup = newTestSetup(WalletAddressFixture.unified())

        assertEquals(0, testSetup.getScreenTimeoutCount())
    }

    @Test
    @MediumTest
    fun testTimeoutOnState() = runTest {
        val testSetup = newTestSetup(WalletAddressFixture.unified())

        assertEquals(false, testSetup.getOnAdjustBrightness())
        assertEquals(0, testSetup.getScreenTimeoutCount())

        composeTestRule.clickAdjustBrightness()

        assertEquals(true, testSetup.getOnAdjustBrightness())
        assertEquals(1, testSetup.getScreenTimeoutCount())
    }

    private fun newTestSetup(walletAddress: WalletAddress) = ReceiveViewTestSetup(composeTestRule, walletAddress)
}
