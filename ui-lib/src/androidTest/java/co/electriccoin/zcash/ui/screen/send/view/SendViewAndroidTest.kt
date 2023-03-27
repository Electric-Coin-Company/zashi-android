package co.electriccoin.zcash.ui.screen.send.view

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.Espresso
import androidx.test.filters.MediumTest
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.sdk.fixture.ZecSendFixture
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.screen.send.SendViewTestSetup
import co.electriccoin.zcash.ui.screen.send.assertOnConfirmation
import co.electriccoin.zcash.ui.screen.send.assertOnForm
import co.electriccoin.zcash.ui.screen.send.assertOnSendFailure
import co.electriccoin.zcash.ui.screen.send.assertOnSendSuccessful
import co.electriccoin.zcash.ui.screen.send.assertOnSending
import co.electriccoin.zcash.ui.screen.send.clickConfirmation
import co.electriccoin.zcash.ui.screen.send.model.SendStage
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

// Non-multiplatform tests that require interacting with the Android system (e.g. system back navigation)
// These don't have persistent state, so they are still unit tests.
class SendViewAndroidTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun newTestSetup(
        sendStage: SendStage = SendStage.Form,
        zecSend: ZecSend? = null
    ) = SendViewTestSetup(
        composeTestRule,
        sendStage,
        zecSend,
        null,
        true
    ).apply {
        setDefaultContent()
    }

    @Test
    @MediumTest
    fun back_on_sending_with_system_navigation_disabled_check() {
        val testSetup = newTestSetup(
            SendStage.Confirmation,
            runBlocking { ZecSendFixture.new() }
        )

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.assertOnConfirmation()
        composeTestRule.clickConfirmation()
        composeTestRule.assertOnSending()

        Espresso.pressBack()

        composeTestRule.assertOnSending()

        assertEquals(1, testSetup.getOnBackCount())
    }

    @Test
    @MediumTest
    fun back_on_send_successful_with_system_navigation() {
        val testSetup = newTestSetup(
            SendStage.SendSuccessful,
            runBlocking { ZecSendFixture.new() }
        )

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.assertOnSendSuccessful()

        Espresso.pressBack()

        assertEquals(1, testSetup.getOnBackCount())
    }

    @Test
    @MediumTest
    fun back_on_send_failure_with_system_navigation() {
        val testSetup = newTestSetup(
            SendStage.SendFailure,
            runBlocking { ZecSendFixture.new() }
        )

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.assertOnSendFailure()

        Espresso.pressBack()

        composeTestRule.assertOnForm()

        assertEquals(1, testSetup.getOnBackCount())
    }
}
