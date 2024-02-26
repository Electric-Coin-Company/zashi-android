package co.electriccoin.zcash.ui.screen.send.integration

import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.filters.MediumTest
import cash.z.ecc.android.sdk.fixture.WalletBalanceFixture
import cash.z.ecc.android.sdk.fixture.WalletFixture
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.fixture.ZecSendFixture
import co.electriccoin.zcash.ui.fixture.MockSynchronizer
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.send.WrapSend
import co.electriccoin.zcash.ui.screen.send.assertOnConfirmation
import co.electriccoin.zcash.ui.screen.send.assertOnForm
import co.electriccoin.zcash.ui.screen.send.clickBack
import co.electriccoin.zcash.ui.screen.send.clickCreateAndSend
import co.electriccoin.zcash.ui.screen.send.setAddress
import co.electriccoin.zcash.ui.screen.send.setAmount
import co.electriccoin.zcash.ui.screen.send.setMemo
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import java.util.Locale

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
    private val walletSnapshot =
        WalletSnapshotFixture.new(
            saplingBalance =
                WalletBalanceFixture.new(
                    available = Zatoshi(Zatoshi.MAX_INCLUSIVE.div(100))
                )
        )

    // TODO [#1171]: Remove default MonetarySeparators locale
    // TODO [#1171]: https://github.com/Electric-Coin-Company/zashi-android/issues/1171
    private val monetarySeparators = MonetarySeparators.current(Locale.US)

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
                focusManager = LocalFocusManager.current,
                synchronizer = synchronizer,
                walletSnapshot = walletSnapshot,
                spendingKey = spendingKey,
                goToQrScanner = {},
                goBack = {},
                goBalances = {},
                hasCameraFeature = true,
                goSettings = {},
                monetarySeparators = monetarySeparators
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
