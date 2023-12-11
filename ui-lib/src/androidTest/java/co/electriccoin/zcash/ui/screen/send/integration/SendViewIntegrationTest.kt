package co.electriccoin.zcash.ui.screen.send.integration

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.filters.MediumTest
import cash.z.ecc.android.sdk.fixture.WalletFixture
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.fixture.ZatoshiFixture
import cash.z.ecc.sdk.fixture.ZecSendFixture
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.fixture.MockSynchronizer
import co.electriccoin.zcash.ui.screen.send.WrapSend
import co.electriccoin.zcash.ui.screen.send.assertOnConfirmation
import co.electriccoin.zcash.ui.screen.send.assertOnForm
import co.electriccoin.zcash.ui.screen.send.clickBack
import co.electriccoin.zcash.ui.screen.send.clickCreateAndSend
import co.electriccoin.zcash.ui.screen.send.setAddress
import co.electriccoin.zcash.ui.screen.send.setAmount
import co.electriccoin.zcash.ui.screen.send.setMemo
import co.electriccoin.zcash.ui.test.getStringResource
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

class SendViewIntegrationTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val wallet = WalletFixture.Alice
    private val network = ZcashNetwork.Testnet
    private val spendingKey =
        runBlocking {
            WalletFixture.Alice.getUnifiedSpendingKey(
                seed = wallet.seedPhrase,
                network = network
            )
        }
    private val synchronizer = MockSynchronizer.new()
    private val balance = ZatoshiFixture.new()

    @Test
    @MediumTest
    fun send_screens_values_state_restoration() {
        val restorationTester = StateRestorationTester(composeTestRule)

        val expectedAmount = ZecSendFixture.AMOUNT.value
        val expectedAddress = ZecSendFixture.ADDRESS
        val expectedMemo = ZecSendFixture.MEMO.value

        restorationTester.setContent {
            WrapSend(
                sendArgumentsWrapper = null,
                synchronizer = synchronizer,
                spendableBalance = balance,
                spendingKey = spendingKey,
                goToQrScanner = {},
                goBack = {},
                hasCameraFeature = true
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
        composeTestRule.clickBack()
        composeTestRule.assertOnForm()

        // And check recreated form values too. Note also that we don't check the amount field value, as it's changed
        // by validation mechanisms

        // We use that the assertTextEquals searches in SemanticsProperties.EditableText too, although to be able to
        // compare its editable value to an exact match we need to pass all its texts
        composeTestRule.onNodeWithText(getStringResource(R.string.send_to)).also {
            it.assertTextEquals(
                getStringResource(R.string.send_to),
                ZecSendFixture.ADDRESS,
                includeEditableText = true
            )
        }
        composeTestRule.onNodeWithText(getStringResource(R.string.send_memo)).also {
            it.assertTextEquals(
                getStringResource(R.string.send_memo),
                ZecSendFixture.MEMO.value,
                includeEditableText = true
            )
        }
    }
}
