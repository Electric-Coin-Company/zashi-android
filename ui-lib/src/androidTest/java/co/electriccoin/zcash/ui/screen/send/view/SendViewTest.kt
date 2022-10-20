package co.electriccoin.zcash.ui.screen.send.view

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.filters.MediumTest
import cash.z.ecc.android.sdk.ext.collectWith
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.ext.ui.model.MonetarySeparators
import cash.z.ecc.sdk.fixture.MemoFixture
import cash.z.ecc.sdk.fixture.WalletAddressFixture
import cash.z.ecc.sdk.fixture.ZatoshiFixture
import cash.z.ecc.sdk.fixture.ZecRequestFixture
import cash.z.ecc.sdk.model.Memo
import cash.z.ecc.sdk.model.ZecSend
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.test.getStringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SendViewTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun create_button_disabled() {
        @Suppress("UNUSED_VARIABLE")
        val testSetup = TestSetup(composeTestRule)

        composeTestRule.onNodeWithText(getStringResource(R.string.send_create)).also {
            it.assertExists()
            it.assertIsNotEnabled()
        }
    }

    @Test
    @MediumTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun create_request_no_memo() = runTest {
        val testSetup = TestSetup(composeTestRule)

        assertEquals(0, testSetup.getOnCreateCount())
        assertEquals(null, testSetup.getLastSend())

        composeTestRule.setValidAmount()
        composeTestRule.setValidAddress()
        composeTestRule.clickCreateAndSend()
        composeTestRule.assertOnConfirmation()
        clickConfirmation(testSetup.interactionSource)

        launch {
            testSetup.mutableActionExecuted.collectWith(this) {
                if (!it) return@collectWith

                assertEquals(1, testSetup.getOnCreateCount())

                launch {
                    testSetup.getLastSend().also {
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
        val testSetup = TestSetup(composeTestRule)

        assertEquals(0, testSetup.getOnCreateCount())
        assertEquals(null, testSetup.getLastSend())

        composeTestRule.setValidAmount()
        composeTestRule.setValidAddress()
        composeTestRule.setValidMemo()

        composeTestRule.clickCreateAndSend()
        composeTestRule.assertOnConfirmation()
        clickConfirmation(testSetup.interactionSource)

        launch {
            testSetup.mutableActionExecuted.collectWith(this) {
                if (!it) return@collectWith

                assertEquals(1, testSetup.getOnCreateCount())

                launch {
                    testSetup.getLastSend().also {
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
        val testSetup = TestSetup(composeTestRule)
        val separators = MonetarySeparators.current()

        assertEquals(0, testSetup.getOnCreateCount())
        assertEquals(null, testSetup.getLastSend())
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
        clickConfirmation(testSetup.interactionSource)

        launch {
            testSetup.mutableActionExecuted.collectWith(this) {
                if (!it) return@collectWith

                assertEquals(1, testSetup.getOnCreateCount())

                launch {
                    testSetup.getLastSend().also {
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
        val testSetup = TestSetup(composeTestRule)
        val separators = MonetarySeparators.current()

        assertEquals(0, testSetup.getOnCreateCount())
        assertEquals(null, testSetup.getLastSend())
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
        assertEquals(null, testSetup.getLastSend())
        composeTestRule.assertSendDisabled()
    }

    @Test
    @MediumTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun max_memo_length() = runTest {
        val testSetup = TestSetup(composeTestRule)

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
        clickConfirmation(testSetup.interactionSource)

        launch {
            testSetup.mutableActionExecuted.collectWith(this) {
                if (!it) return@collectWith

                assertEquals(1, testSetup.getOnCreateCount())

                launch {
                    testSetup.getLastSend().also {
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
        val testSetup = TestSetup(composeTestRule)

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.clickBack()

        assertEquals(1, testSetup.getOnBackCount())
    }

    @Test
    @MediumTest
    fun back_on_confirmation() {
        val testSetup = TestSetup(composeTestRule)

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.setValidAmount()
        composeTestRule.setValidAddress()
        composeTestRule.clickCreateAndSend()
        composeTestRule.assertOnConfirmation()
        composeTestRule.clickBack()
        composeTestRule.assertOnForm()

        assertEquals(0, testSetup.getOnBackCount())
    }

    private class TestSetup(private val composeTestRule: ComposeContentTestRule) {

        private val onBackCount = AtomicInteger(0)
        private val onCreateCount = AtomicInteger(0)
        val interactionSource = MutableInteractionSource()
        val mutableActionExecuted = MutableStateFlow(false)

        @Volatile
        private var onSendZecRequest: ZecSend? = null

        fun getOnBackCount(): Int {
            composeTestRule.waitForIdle()
            return onBackCount.get()
        }

        fun getOnCreateCount(): Int {
            composeTestRule.waitForIdle()
            return onCreateCount.get()
        }

        fun getLastSend(): ZecSend? {
            composeTestRule.waitForIdle()
            return onSendZecRequest
        }

        init {
            composeTestRule.setContent {
                ZcashTheme {
                    Send(
                        mySpendableBalance = ZatoshiFixture.new(),
                        interactionSource = interactionSource,
                        goBack = {
                            onBackCount.incrementAndGet()
                        },
                        onCreateAndSend = {
                            onCreateCount.incrementAndGet()
                            onSendZecRequest = it
                            mutableActionExecuted.update { true }
                        }
                    )
                }
            }
        }
    }
}

private fun ComposeContentTestRule.clickBack() {
    onNodeWithContentDescription(getStringResource(R.string.send_back_content_description)).also {
        it.performClick()
    }
}

private fun ComposeContentTestRule.setValidAmount() {
    onNodeWithText(getStringResource(R.string.send_amount)).also {
        val separators = MonetarySeparators.current()
        it.performTextClearance()
        it.performTextInput("123${separators.decimal}456")
    }
}

private fun ComposeContentTestRule.setAmount(amount: String) {
    onNodeWithText(getStringResource(R.string.send_amount)).also {
        it.performTextClearance()
        it.performTextInput(amount)
    }
}

private fun ComposeContentTestRule.setValidAddress() {
    onNodeWithText(getStringResource(R.string.send_to)).also {
        it.performTextClearance()
        it.performTextInput(WalletAddressFixture.UNIFIED_ADDRESS_STRING)
    }
}

private fun ComposeContentTestRule.setValidMemo() {
    onNodeWithText(getStringResource(R.string.send_memo)).also {
        it.performTextClearance()
        it.performTextInput(MemoFixture.MEMO_STRING)
    }
}

private fun ComposeContentTestRule.setMemo(memo: String) {
    onNodeWithText(getStringResource(R.string.send_memo)).also {
        it.performTextClearance()
        it.performTextInput(memo)
    }
}

private fun ComposeContentTestRule.clickCreateAndSend() {
    onNodeWithText(getStringResource(R.string.send_create)).also {
        it.performClick()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun TestScope.clickConfirmation(interactionSource: MutableInteractionSource) {
    launch(Dispatchers.Main) {
        interactionSource.emit(PressInteraction.Press(Offset.Zero))
    }
}

private fun ComposeContentTestRule.assertOnForm() {
    onNodeWithText(getStringResource(R.string.send_create)).also {
        it.assertExists()
    }
}

private fun ComposeContentTestRule.assertOnConfirmation() {
    onNodeWithText(getStringResource(R.string.send_confirm)).also {
        it.assertExists()
    }
}

private fun ComposeContentTestRule.assertSendEnabled() {
    onNodeWithText(getStringResource(R.string.send_create)).also {
        it.assertIsEnabled()
    }
}

private fun ComposeContentTestRule.assertSendDisabled() {
    onNodeWithText(getStringResource(R.string.send_create)).also {
        it.assertIsNotEnabled()
    }
}
