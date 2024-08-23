package co.electriccoin.zcash.ui.screen.send.integration

import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.filters.MediumTest
import cash.z.ecc.sdk.fixture.ZecSendFixture
import co.electriccoin.zcash.ui.screen.send.WrapSend
import co.electriccoin.zcash.ui.screen.send.assertOnConfirmation
import co.electriccoin.zcash.ui.screen.send.assertOnForm
import co.electriccoin.zcash.ui.screen.send.clickCreateAndSend
import co.electriccoin.zcash.ui.screen.send.setAddress
import co.electriccoin.zcash.ui.screen.send.setAmount
import co.electriccoin.zcash.ui.screen.send.setMemo
import org.junit.Rule
import org.junit.Test
import kotlin.test.Ignore

class SendViewIntegrationTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // TODO [#1260]: Cover Send screens UI with tests
    // TODO [#1260]: https://github.com/Electric-Coin-Company/zashi-android/issues/1260
    @Test
    @MediumTest
    @Ignore("Disabled as the entire Send flow will be reworked and the test align after it")
    fun send_screens_values_state_restoration() {
        val restorationTester = StateRestorationTester(composeTestRule)

        val expectedAmount = ZecSendFixture.AMOUNT.value
        val expectedAddress = ZecSendFixture.ADDRESS
        val expectedMemo = ZecSendFixture.MEMO.value

        restorationTester.setContent {
            WrapSend(
                sendArguments = null,
                goToQrScanner = {},
                goBack = {},
                goBalances = {},
                goSettings = {},
                goSendConfirmation = {},
            )
        }

        // Fill form
        composeTestRule.assertOnForm()
        composeTestRule.setAddress(expectedAddress)
        composeTestRule.setAmount(expectedAmount.toString())
        composeTestRule.setMemo(expectedMemo)

        // Move to confirmation
        composeTestRule.clickCreateAndSend()
        composeTestRule.assertOnConfirmation()

        restorationTester.emulateSavedInstanceStateRestore()

        // Check if stage recreated correctly
        composeTestRule.assertOnConfirmation()

        // Move back to form
        composeTestRule.assertOnForm()

        composeTestRule.onNodeWithText(ZecSendFixture.ADDRESS).also {
            it.assertExists()
        }
        composeTestRule.onNodeWithText(ZecSendFixture.AMOUNT.value.toString()).also {
            it.assertExists()
        }
        composeTestRule.onNodeWithText(ZecSendFixture.MEMO.value).also {
            it.assertExists()
        }
    }
}
