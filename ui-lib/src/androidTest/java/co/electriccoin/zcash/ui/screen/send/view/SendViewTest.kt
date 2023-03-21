package co.electriccoin.zcash.ui.screen.send.view

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import cash.z.ecc.android.sdk.ext.collectWith
import cash.z.ecc.android.sdk.fixture.WalletAddressFixture
import cash.z.ecc.android.sdk.model.Memo
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.sdk.fixture.ZecRequestFixture
import cash.z.ecc.sdk.fixture.ZecSendFixture
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.send.SendViewTestSetup
import co.electriccoin.zcash.ui.screen.send.assertOnConfirmation
import co.electriccoin.zcash.ui.screen.send.assertOnForm
import co.electriccoin.zcash.ui.screen.send.assertOnSendFailure
import co.electriccoin.zcash.ui.screen.send.assertOnSendSuccessful
import co.electriccoin.zcash.ui.screen.send.assertOnSending
import co.electriccoin.zcash.ui.screen.send.assertSendDisabled
import co.electriccoin.zcash.ui.screen.send.assertSendEnabled
import co.electriccoin.zcash.ui.screen.send.clickBack
import co.electriccoin.zcash.ui.screen.send.clickConfirmation
import co.electriccoin.zcash.ui.screen.send.clickCreateAndSend
import co.electriccoin.zcash.ui.screen.send.model.SendStage
import co.electriccoin.zcash.ui.screen.send.setAmount
import co.electriccoin.zcash.ui.screen.send.setMemo
import co.electriccoin.zcash.ui.screen.send.setValidAddress
import co.electriccoin.zcash.ui.screen.send.setValidAmount
import co.electriccoin.zcash.ui.screen.send.setValidMemo
import co.electriccoin.zcash.ui.test.getStringResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SendViewTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun newTestSetup(
        sendStage: SendStage = SendStage.Form,
        zecSend: ZecSend? = null
    ) = SendViewTestSetup(
        composeTestRule,
        sendStage,
        zecSend
    ).apply {
        setDefaultContent()
    }

    @Test
    @MediumTest
    fun create_button_disabled() {
        @Suppress("UNUSED_VARIABLE")
        val testSetup = newTestSetup()

        composeTestRule.onNodeWithText(getStringResource(R.string.send_create)).also {
            it.assertExists()
            it.assertIsNotEnabled()
        }
    }

    @Test
    @MediumTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun create_request_no_memo() = runTest {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnCreateCount())
        assertEquals(null, testSetup.getLastZecSend())

        composeTestRule.setValidAmount()
        composeTestRule.setValidAddress()
        composeTestRule.clickCreateAndSend()
        composeTestRule.assertOnConfirmation()
        composeTestRule.clickConfirmation()

        launch {
            testSetup.mutableActionExecuted.collectWith(this) {
                if (!it) return@collectWith

                assertEquals(1, testSetup.getOnCreateCount())

                launch {
                    testSetup.getLastZecSend().also {
                        assertNotNull(it)
                        assertEquals(WalletAddressFixture.unified(), it.destination)
                        assertEquals(Zatoshi(12345678900000), it.amount)
                        assertEquals(ZecRequestFixture.MESSAGE.value, it.memo.value)
                    }
                }
                this.cancel()
            }
        }
    }

    @Test
    @MediumTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun create_request_with_memo() = runTest {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnCreateCount())
        assertEquals(null, testSetup.getLastZecSend())

        composeTestRule.setValidAmount()
        composeTestRule.setValidAddress()
        composeTestRule.setValidMemo()

        composeTestRule.clickCreateAndSend()
        composeTestRule.assertOnConfirmation()
        composeTestRule.clickConfirmation()

        launch {
            testSetup.mutableActionExecuted.collectWith(this) {
                if (!it) return@collectWith

                assertEquals(1, testSetup.getOnCreateCount())

                launch {
                    testSetup.getLastZecSend().also {
                        assertNotNull(it)
                        assertEquals(WalletAddressFixture.unified(), it.destination)
                        assertEquals(Zatoshi(12345678900000), it.amount)
                        assertEquals(ZecRequestFixture.MESSAGE.value, it.memo.value)
                    }
                }
                this.cancel()
            }
        }
    }

    @Test
    @MediumTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun check_regex_functionality_valid_inputs() = runTest {
        val testSetup = newTestSetup()
        val separators = MonetarySeparators.current()

        assertEquals(0, testSetup.getOnCreateCount())
        assertEquals(null, testSetup.getLastZecSend())
        composeTestRule.assertSendDisabled()

        composeTestRule.setValidAmount()
        composeTestRule.setValidAddress()
        composeTestRule.setValidMemo()
        composeTestRule.assertSendEnabled()

        composeTestRule.setAmount("123")
        composeTestRule.assertSendEnabled()

        // e.g. 123,
        composeTestRule.setAmount("123${separators.grouping}")
        composeTestRule.assertSendEnabled()

        // e.g. 123.
        composeTestRule.setAmount("123${separators.decimal}")
        composeTestRule.assertSendEnabled()

        // e.g. 123,456.
        composeTestRule.setAmount("123${separators.grouping}456${separators.decimal}")
        composeTestRule.assertSendEnabled()

        // e.g. 123,456.789
        composeTestRule.setAmount("123${separators.grouping}456${separators.decimal}789")
        composeTestRule.assertSendEnabled()

        composeTestRule.clickCreateAndSend()
        composeTestRule.assertOnConfirmation()
        composeTestRule.clickConfirmation()

        launch {
            testSetup.mutableActionExecuted.collectWith(this) {
                if (!it) return@collectWith

                assertEquals(1, testSetup.getOnCreateCount())

                launch {
                    testSetup.getLastZecSend().also {
                        assertNotNull(it)
                        assertEquals(WalletAddressFixture.unified(), it.destination)
                        assertEquals(Zatoshi(12345678900000), it.amount)
                        assertEquals(ZecRequestFixture.MESSAGE.value, it.memo.value)
                    }
                }
                this.cancel()
            }
        }
    }

    @Test
    @MediumTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun check_regex_functionality_invalid_inputs() = runTest {
        val testSetup = newTestSetup()
        val separators = MonetarySeparators.current()

        assertEquals(0, testSetup.getOnCreateCount())
        assertEquals(null, testSetup.getLastZecSend())
        composeTestRule.assertSendDisabled()

        composeTestRule.setAmount("aaa")
        composeTestRule.assertSendDisabled()

        composeTestRule.setAmount("123aaa")
        composeTestRule.assertSendDisabled()

        // e.g. ,.
        composeTestRule.setAmount("${separators.grouping}${separators.decimal}")
        composeTestRule.assertSendDisabled()

        // e.g. 123,.
        composeTestRule.setAmount("123${separators.grouping}${separators.decimal}")
        composeTestRule.assertSendDisabled()

        // e.g. 1,2,3
        composeTestRule.setAmount("1${separators.grouping}2${separators.grouping}3")
        composeTestRule.assertSendDisabled()

        // e.g. 1.2.3
        composeTestRule.setAmount("1${separators.decimal}2${separators.decimal}3")
        composeTestRule.assertSendDisabled()

        assertEquals(0, testSetup.getOnCreateCount())
        assertEquals(null, testSetup.getLastZecSend())
        composeTestRule.assertSendDisabled()
    }

    @Test
    @MediumTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun max_memo_length() = runTest {
        val testSetup = newTestSetup()

        composeTestRule.setValidAmount()
        composeTestRule.setValidAddress()

        val input = buildString {
            while (Memo.isWithinMaxLength(toString())) {
                append("a")
            }
        }

        composeTestRule.setMemo(input)

        composeTestRule.clickCreateAndSend()
        composeTestRule.assertOnConfirmation()
        composeTestRule.clickConfirmation()

        launch {
            testSetup.mutableActionExecuted.collectWith(this) {
                if (!it) return@collectWith

                assertEquals(1, testSetup.getOnCreateCount())

                launch {
                    testSetup.getLastZecSend().also {
                        assertNotNull(it)
                        assertEquals(WalletAddressFixture.unified(), it.destination)
                        assertEquals(Zatoshi(12345600000), it.amount)
                        assertTrue(it.memo.value.isEmpty())
                    }
                }
                this.cancel()
            }
        }
    }

    @Test
    @MediumTest
    fun back_on_form() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.clickBack()

        assertEquals(1, testSetup.getOnBackCount())
    }

    @Test
    @MediumTest
    fun back_on_confirmation() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.setValidAmount()
        composeTestRule.setValidAddress()
        composeTestRule.clickCreateAndSend()
        composeTestRule.assertOnConfirmation()
        composeTestRule.clickBack()
        composeTestRule.assertOnForm()

        assertEquals(1, testSetup.getOnBackCount())
    }

    @Test
    @MediumTest
    fun back_on_sending_disabled_check() {
        newTestSetup(
            SendStage.Confirmation,
            runBlocking { ZecSendFixture.new() }
        )

        composeTestRule.assertOnConfirmation()
        composeTestRule.clickConfirmation()
        composeTestRule.assertOnSending()

        composeTestRule.onNodeWithContentDescription(getStringResource(R.string.send_back_content_description)).also {
            it.assertDoesNotExist()
        }
    }

    @Test
    @MediumTest
    fun back_on_send_successful() {
        val testSetup = newTestSetup(
            SendStage.SendSuccessful,
            runBlocking { ZecSendFixture.new() }
        )

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.assertOnSendSuccessful()
        composeTestRule.clickBack()

        assertEquals(1, testSetup.getOnBackCount())
    }

    @Test
    @MediumTest
    fun close_on_send_successful() {
        val testSetup = newTestSetup(
            SendStage.SendSuccessful,
            runBlocking { ZecSendFixture.new() }
        )

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.assertOnSendSuccessful()
        composeTestRule.onNodeWithText(getStringResource(R.string.send_successful_button)).also {
            it.assertExists()
            it.performClick()
        }

        assertEquals(1, testSetup.getOnBackCount())
    }

    @Test
    @MediumTest
    fun back_on_send_failure() {
        val testSetup = newTestSetup(
            SendStage.SendFailure,
            runBlocking { ZecSendFixture.new() }
        )

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.assertOnSendFailure()
        composeTestRule.clickBack()
        composeTestRule.assertOnForm()

        assertEquals(1, testSetup.getOnBackCount())
    }

    @Test
    @MediumTest
    fun close_on_send_failure() {
        val testSetup = newTestSetup(
            SendStage.SendFailure,
            runBlocking { ZecSendFixture.new() }
        )

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.assertOnSendFailure()
        composeTestRule.onNodeWithText(getStringResource(R.string.send_failure_button)).also {
            it.assertExists()
            it.performClick()
        }
        composeTestRule.assertOnForm()

        assertEquals(1, testSetup.getOnBackCount())
    }
}
