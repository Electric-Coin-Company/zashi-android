package co.electriccoin.zcash.ui.screen.send.view

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.filters.MediumTest
import cash.z.ecc.sdk.fixture.MemoFixture
import cash.z.ecc.sdk.fixture.WalletAddressFixture
import cash.z.ecc.sdk.fixture.ZatoshiFixture
import cash.z.ecc.sdk.fixture.ZecRequestFixture
import cash.z.ecc.sdk.model.Memo
import cash.z.ecc.sdk.model.MonetarySeparators
import cash.z.ecc.sdk.model.Zatoshi
import cash.z.ecc.sdk.model.ZecSend
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.test.getStringResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SendViewTest {
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
        composeTestRule.clickConfirmation()

        assertEquals(1, testSetup.getOnCreateCount())

        testSetup.getLastSend().also {
            assertNotNull(it)
            assertEquals(WalletAddressFixture.unified(), it.destination)
            assertEquals(Zatoshi(12300000), it.amount)
            assertTrue(it.memo.value.isEmpty())
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

        composeTestRule.onNodeWithText(getStringResource(R.string.send_memo)).also {
            it.performTextInput(MemoFixture.MEMO_STRING)
        }

        composeTestRule.clickCreateAndSend()
        composeTestRule.assertOnConfirmation()
        composeTestRule.clickConfirmation()

        assertEquals(1, testSetup.getOnCreateCount())

        testSetup.getLastSend().also {
            assertNotNull(it)
            assertEquals(WalletAddressFixture.unified(), it.destination)
            assertEquals(Zatoshi(12300000), it.amount)
            assertEquals(ZecRequestFixture.MESSAGE.value, it.memo.value)
        }
    }

    @Test
    @MediumTest
    @Ignore("https://github.com/zcash/secant-android-wallet/issues/218")
    fun create_request_illegal_amount() {
        val testSetup = TestSetup(composeTestRule)

        assertEquals(0, testSetup.getOnCreateCount())
        assertEquals(null, testSetup.getLastSend())

        composeTestRule.onNodeWithText(getStringResource(R.string.send_amount)).also {
            val separators = MonetarySeparators.current()

            it.performTextInput("{${separators.decimal}}1{${separators.decimal}}2{${separators.decimal}}3{${separators.decimal}}4")
        }

        composeTestRule.setValidAddress()

        composeTestRule.clickCreateAndSend()

        assertEquals(0, testSetup.getOnCreateCount())
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

        composeTestRule.onNodeWithText(getStringResource(R.string.send_memo)).also {
            it.performTextInput(input)
        }

        composeTestRule.clickCreateAndSend()
        composeTestRule.assertOnConfirmation()
        composeTestRule.clickConfirmation()

        assertEquals(1, testSetup.getOnCreateCount())

        testSetup.getLastSend().also {
            assertNotNull(it)
            assertEquals(WalletAddressFixture.unified(), it.destination)
            assertEquals(Zatoshi(12300000), it.amount)
            assertTrue(it.memo.value.isEmpty())
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
                        goBack = {
                            onBackCount.incrementAndGet()
                        },
                        onCreateAndSend = {
                            onCreateCount.incrementAndGet()
                            onSendZecRequest = it
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

        it.performTextInput("{${separators.decimal}}123")
    }
}

private fun ComposeContentTestRule.setValidAddress() {
    onNodeWithText(getStringResource(R.string.send_to)).also {
        it.performTextInput(WalletAddressFixture.UNIFIED_ADDRESS_STRING)
    }
}

private fun ComposeContentTestRule.clickCreateAndSend() {
    onNodeWithText(getStringResource(R.string.send_create)).also {
        it.performClick()
    }
}

private fun ComposeContentTestRule.clickConfirmation() {
    onNodeWithText(getStringResource(R.string.send_confirm)).also {
        it.performClick()
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
