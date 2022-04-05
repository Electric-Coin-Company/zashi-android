package co.electriccoin.zcash.ui.screen.request.view

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.filters.MediumTest
import cash.z.ecc.sdk.ext.ui.model.MonetarySeparators
import cash.z.ecc.sdk.fixture.WalletAddressFixture
import cash.z.ecc.sdk.fixture.ZecRequestFixture
import cash.z.ecc.sdk.model.Zatoshi
import cash.z.ecc.sdk.model.ZecRequest
import cash.z.ecc.sdk.model.ZecRequestMessage
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.test.getStringResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RequestViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun create_button_disabled() {
        @Suppress("UNUSED_VARIABLE")
        val testSetup = TestSetup(composeTestRule)

        composeTestRule.assertSendDisabled()
    }

    @Test
    @MediumTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun create_request_no_message() = runTest {
        val testSetup = TestSetup(composeTestRule)

        assertEquals(0, testSetup.getOnCreateCount())
        assertEquals(null, testSetup.getLastCreateZecRequest())

        composeTestRule.setValidAmount()

        composeTestRule.clickCreateAndSend()

        assertEquals(1, testSetup.getOnCreateCount())

        testSetup.getLastCreateZecRequest().also {
            assertNotNull(it)
            assertEquals(WalletAddressFixture.unified(), it.address)
            assertEquals(Zatoshi(12345600000), it.amount)
            assertTrue(it.message.value.isEmpty())
        }
    }

    @Test
    @MediumTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun create_request_with_message() = runTest {
        val testSetup = TestSetup(composeTestRule)

        assertEquals(0, testSetup.getOnCreateCount())
        assertEquals(null, testSetup.getLastCreateZecRequest())

        composeTestRule.setValidAmount()

        composeTestRule.setValidMessage()

        composeTestRule.clickCreateAndSend()

        assertEquals(1, testSetup.getOnCreateCount())

        testSetup.getLastCreateZecRequest().also {
            assertNotNull(it)
            assertEquals(WalletAddressFixture.unified(), it.address)
            assertEquals(Zatoshi(12345600000), it.amount)
            assertEquals(ZecRequestFixture.MESSAGE.value, it.message.value)
        }
    }

    @Test
    @MediumTest
    fun check_regex_functionality_valid_inputs() {
        val testSetup = TestSetup(composeTestRule)
        val separators = MonetarySeparators.current()

        assertEquals(0, testSetup.getOnCreateCount())
        assertEquals(null, testSetup.getLastCreateZecRequest())

        composeTestRule.setAmount("123")
        composeTestRule.clickCreateAndSend()
        assertEquals(1, testSetup.getOnCreateCount())

        // e.g. 123,456
        composeTestRule.setAmount("123${separators.grouping}456")
        composeTestRule.clickCreateAndSend()
        assertEquals(2, testSetup.getOnCreateCount())

        // e.g. 123.
        composeTestRule.setAmount("123${separators.decimal}")
        composeTestRule.clickCreateAndSend()
        assertEquals(3, testSetup.getOnCreateCount())

        // e.g. 123,456.
        composeTestRule.setAmount("123${separators.grouping}456${separators.decimal}")
        composeTestRule.clickCreateAndSend()
        assertEquals(4, testSetup.getOnCreateCount())

        // e.g. 123,456.789
        composeTestRule.setAmount("123${separators.grouping}456${separators.decimal}789")
        composeTestRule.clickCreateAndSend()
        assertEquals(5, testSetup.getOnCreateCount())
    }

    @Test
    @MediumTest
    fun check_regex_functionality_invalid_inputs() {
        val testSetup = TestSetup(composeTestRule)
        val separators = MonetarySeparators.current()

        assertEquals(0, testSetup.getOnCreateCount())
        assertEquals(null, testSetup.getLastCreateZecRequest())

        composeTestRule.setAmount("aaa")
        composeTestRule.clickCreateAndSend()
        assertEquals(0, testSetup.getOnCreateCount())

        composeTestRule.setAmount("123aaa")
        composeTestRule.clickCreateAndSend()
        assertEquals(0, testSetup.getOnCreateCount())

        // e.g. ,.
        composeTestRule.setAmount("${separators.grouping}${separators.decimal}")
        composeTestRule.clickCreateAndSend()
        assertEquals(0, testSetup.getOnCreateCount())

        // e.g. 123,.
        composeTestRule.setAmount("123${separators.grouping}${separators.decimal}")
        composeTestRule.clickCreateAndSend()
        assertEquals(0, testSetup.getOnCreateCount())

        // e.g. 1,2,3
        composeTestRule.setAmount("1${separators.grouping}2${separators.grouping}3")
        composeTestRule.clickCreateAndSend()
        assertEquals(0, testSetup.getOnCreateCount())

        // e.g. 1.2.3
        composeTestRule.setAmount("1${separators.decimal}2${separators.decimal}3")
        composeTestRule.clickCreateAndSend()
        assertEquals(0, testSetup.getOnCreateCount())
    }

    @Test
    @MediumTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun max_message_length() = runTest {
        val testSetup = TestSetup(composeTestRule)

        composeTestRule.setValidAmount()

        composeTestRule.setMessage(
            buildString {
                repeat(ZecRequestMessage.MAX_MESSAGE_LENGTH + 1) { number ->
                    append("$number")
                }
            }
        )

        composeTestRule.clickCreateAndSend()

        assertEquals(1, testSetup.getOnCreateCount())

        testSetup.getLastCreateZecRequest().also {
            assertNotNull(it)
            assertEquals(WalletAddressFixture.unified(), it.address)
            assertEquals(Zatoshi(12345600000), it.amount)
            assertTrue(it.message.value.isEmpty())
        }
    }

    @Test
    @MediumTest
    fun back() {
        val testSetup = TestSetup(composeTestRule)

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.clickBack()

        assertEquals(1, testSetup.getOnBackCount())
    }

    private class TestSetup(private val composeTestRule: ComposeContentTestRule) {

        private val onBackCount = AtomicInteger(0)
        private val onCreateCount = AtomicInteger(0)
        @Volatile
        private var onCreateZecRequest: ZecRequest? = null

        fun getOnBackCount(): Int {
            composeTestRule.waitForIdle()
            return onBackCount.get()
        }

        fun getOnCreateCount(): Int {
            composeTestRule.waitForIdle()
            return onCreateCount.get()
        }

        fun getLastCreateZecRequest(): ZecRequest? {
            composeTestRule.waitForIdle()
            return onCreateZecRequest
        }

        init {
            composeTestRule.setContent {
                ZcashTheme {
                    Request(
                        myAddress = runBlocking { WalletAddressFixture.unified() },
                        goBack = {
                            onBackCount.incrementAndGet()
                        },
                        onCreateAndSend = {
                            onCreateCount.incrementAndGet()
                            onCreateZecRequest = it
                        }
                    )
                }
            }
        }
    }
}

private fun ComposeContentTestRule.clickBack() {
    onNodeWithContentDescription(getStringResource(R.string.request_back_content_description)).also {
        it.performClick()
    }
}

private fun ComposeContentTestRule.setValidAmount() {
    onNodeWithText(getStringResource(R.string.request_amount)).also {
        val separators = MonetarySeparators.current()
        it.performTextClearance()
        it.performTextInput("123${separators.decimal}456")
    }
}

private fun ComposeContentTestRule.setAmount(amount: String) {
    onNodeWithText(getStringResource(R.string.request_amount)).also {
        it.performTextClearance()
        it.performTextInput(amount)
    }
}

private fun ComposeContentTestRule.setValidMessage() {
    onNodeWithText(getStringResource(R.string.request_message)).also {
        it.performTextClearance()
        it.performTextInput(ZecRequestFixture.MESSAGE.value)
    }
}

private fun ComposeContentTestRule.setMessage(message: String) {
    onNodeWithText(getStringResource(R.string.request_message)).also {
        it.performTextClearance()
        it.performTextInput(message)
    }
}

private fun ComposeContentTestRule.clickCreateAndSend() {
    onNodeWithText(getStringResource(R.string.request_create)).also {
        it.performClick()
    }
}

private fun ComposeContentTestRule.assertSendDisabled() {
    onNodeWithText(getStringResource(R.string.request_create)).also {
        it.assertIsNotEnabled()
    }
}
