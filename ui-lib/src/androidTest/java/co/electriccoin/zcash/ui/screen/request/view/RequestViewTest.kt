package co.electriccoin.zcash.ui.screen.request.view

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.filters.MediumTest
import cash.z.ecc.sdk.fixture.WalletAddressFixture
import cash.z.ecc.sdk.fixture.ZecRequestFixture
import cash.z.ecc.sdk.model.MonetarySeparators
import cash.z.ecc.sdk.model.Zatoshi
import cash.z.ecc.sdk.model.ZecRequest
import cash.z.ecc.sdk.model.ZecRequestMessage
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.test.getStringResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Ignore
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

        composeTestRule.onNodeWithText(getStringResource(R.string.request_create)).also {
            it.assertIsNotEnabled()
        }
    }

    @Test
    @MediumTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun create_request_no_message() = runTest {
        val testSetup = TestSetup(composeTestRule)

        assertEquals(0, testSetup.getOnCreateCount())
        assertEquals(null, testSetup.getLastCreateZecRequest())

        composeTestRule.onNodeWithText(getStringResource(R.string.request_amount)).also {
            val separators = MonetarySeparators.current()

            it.performTextInput("{${separators.decimal}}123")
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.request_create)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnCreateCount())

        testSetup.getLastCreateZecRequest().also {
            assertNotNull(it)
            assertEquals(WalletAddressFixture.unified(), it.address)
            assertEquals(Zatoshi(12300000), it.amount)
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

        composeTestRule.onNodeWithText(getStringResource(R.string.request_amount)).also {
            val separators = MonetarySeparators.current()

            it.performTextInput("{${separators.decimal}}123")
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.request_message)).also {
            it.performTextInput(ZecRequestFixture.MESSAGE.value)
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.request_create)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnCreateCount())

        testSetup.getLastCreateZecRequest().also {
            assertNotNull(it)
            assertEquals(WalletAddressFixture.unified(), it.address)
            assertEquals(Zatoshi(12300000), it.amount)
            assertEquals(ZecRequestFixture.MESSAGE.value, it.message.value)
        }
    }

    @Test
    @MediumTest
    @Ignore("https://github.com/zcash/secant-android-wallet/issues/218")
    fun create_request_illegal_input() {
        val testSetup = TestSetup(composeTestRule)

        assertEquals(0, testSetup.getOnCreateCount())
        assertEquals(null, testSetup.getLastCreateZecRequest())

        composeTestRule.onNodeWithText(getStringResource(R.string.request_amount)).also {
            val separators = MonetarySeparators.current()

            it.performTextInput("{${separators.decimal}}1{${separators.decimal}}2{${separators.decimal}}3{${separators.decimal}}4")
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.request_create)).also {
            it.performClick()
        }

        assertEquals(0, testSetup.getOnCreateCount())
    }

    @Test
    @MediumTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun max_message_length() = runTest {
        val testSetup = TestSetup(composeTestRule)

        composeTestRule.onNodeWithText(getStringResource(R.string.request_amount)).also {
            val separators = MonetarySeparators.current()

            it.performTextInput("{${separators.decimal}}123")
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.request_message)).also {
            val input = buildString {
                repeat(ZecRequestMessage.MAX_MESSAGE_LENGTH + 1) { _ ->
                    append("$it")
                }
            }

            it.performTextInput(input)
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.request_create)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnCreateCount())

        testSetup.getLastCreateZecRequest().also {
            assertNotNull(it)
            assertEquals(WalletAddressFixture.unified(), it.address)
            assertEquals(Zatoshi(12300000), it.amount)
            assertTrue(it.message.value.isEmpty())
        }
    }

    @Test
    @MediumTest
    fun back() {
        val testSetup = TestSetup(composeTestRule)

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.onNodeWithContentDescription(getStringResource(R.string.request_back_content_description)).also {
            it.performClick()
        }

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
