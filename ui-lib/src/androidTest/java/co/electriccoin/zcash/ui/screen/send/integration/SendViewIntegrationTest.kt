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
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.fixture.BalanceStateFixture
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
import kotlin.test.Ignore

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

    private val monetarySeparators = MonetarySeparators.current(Locale.getDefault())

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
                balanceState = BalanceStateFixture.new(),
                sendArguments = null,
                synchronizer = synchronizer,
                walletSnapshot = walletSnapshot,
                spendingKey = spendingKey,
                focusManager = LocalFocusManager.current,
                goToQrScanner = {},
                goBack = {},
                goBalances = {},
                goSettings = {},
                goSendConfirmation = {},
                hasCameraFeature = true,
                monetarySeparators = monetarySeparators,
                walletRestoringState = WalletRestoringState.NONE,
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
