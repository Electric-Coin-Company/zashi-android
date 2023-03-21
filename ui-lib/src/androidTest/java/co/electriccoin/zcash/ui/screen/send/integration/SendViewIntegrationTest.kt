package co.electriccoin.zcash.ui.screen.send.integration

import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.sdk.fixture.ZecSendFixture
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.send.SendViewTestSetup
import co.electriccoin.zcash.ui.screen.send.model.SendStage
import co.electriccoin.zcash.ui.test.getStringResource
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SendViewIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun newTestSetup(
        sendStage: SendStage = SendStage.Form,
        zecSend: ZecSend? = null
    ) = SendViewTestSetup(
        composeTestRule,
        sendStage,
        zecSend
    )

    @Test
    @MediumTest
    fun send_screens_values_state_restoration() {
        val restorationTester = StateRestorationTester(composeTestRule)

        val startSendStage = SendStage.Confirmation
        val startZecSend = runBlocking {
            ZecSendFixture.new(address = "Address to cause failure")
        }

        val testSetup = newTestSetup(
            startSendStage,
            startZecSend
        )

        restorationTester.setContent {
            testSetup.DefaultContent()
        }

        assertEquals(testSetup.getLastSendStage(), startSendStage)
        assertEquals(testSetup.getLastZecSend(), startZecSend)

        composeTestRule.onNodeWithText(getStringResource(R.string.send_confirmation_button)).also {
            it.performClick()
        }

        assertEquals(testSetup.getLastZecSend(), startZecSend)
        assertNotEquals(testSetup.getLastSendStage(), startSendStage)
        assertEquals(testSetup.getLastSendStage(), SendStage.Sending)

        restorationTester.emulateSavedInstanceStateRestore()

        assertEquals(testSetup.getLastZecSend(), startZecSend)
        assertEquals(testSetup.getLastSendStage(), SendStage.Sending)
    }
}
