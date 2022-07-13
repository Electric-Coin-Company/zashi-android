package co.electriccoin.zcash.ui.screen.restore.view

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.filters.MediumTest
import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.sdk.fixture.SeedPhraseFixture
import cash.z.ecc.sdk.model.SeedPhrase
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.CommonTag
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.restore.RestoreTag
import co.electriccoin.zcash.ui.screen.restore.state.WordList
import co.electriccoin.zcash.ui.test.getAppContext
import co.electriccoin.zcash.ui.test.getStringResource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

class RestoreViewTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun keyboard_appears_on_launch() {
        newTestSetup(emptyList())

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(RestoreTag.SEED_WORD_TEXT_FIELD).also {
            it.assertIsFocused()
        }

        val inputMethodManager = getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        assertTrue(inputMethodManager.isAcceptingText)
    }

    @Test
    @MediumTest
    fun autocomplete_suggestions_appear() {
        newTestSetup(emptyList())

        composeTestRule.onNodeWithTag(RestoreTag.SEED_WORD_TEXT_FIELD).also {
            it.performTextInput("ab")

            // Make sure text isn't cleared
            it.assertTextContains("ab")
        }

        composeTestRule.onNode(hasText("abandon") and hasTestTag(RestoreTag.AUTOCOMPLETE_ITEM)).also {
            it.assertExists()
        }

        composeTestRule.onNode(hasText("able") and hasTestTag(RestoreTag.AUTOCOMPLETE_ITEM)).also {
            it.assertExists()
        }
    }

    @Test
    @MediumTest
    fun choose_autocomplete() {
        newTestSetup(emptyList())

        composeTestRule.onNodeWithTag(RestoreTag.SEED_WORD_TEXT_FIELD).also {
            it.performTextInput("ab")
        }

        composeTestRule.onNode(hasText("abandon") and hasTestTag(RestoreTag.AUTOCOMPLETE_ITEM)).also {
            it.performClick()
        }

        composeTestRule.onNodeWithTag(RestoreTag.AUTOCOMPLETE_LAYOUT).also {
            it.assertDoesNotExist()
        }

        composeTestRule.onNode(hasText("abandon") and hasTestTag(CommonTag.CHIP), useUnmergedTree = true).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithTag(RestoreTag.SEED_WORD_TEXT_FIELD).also {
            it.assertTextEquals("")
        }
    }

    @Test
    @MediumTest
    fun type_full_word() {
        newTestSetup(emptyList())

        composeTestRule.onNodeWithTag(RestoreTag.SEED_WORD_TEXT_FIELD).also {
            it.performTextInput("abandon")
        }

        composeTestRule.onNodeWithTag(RestoreTag.SEED_WORD_TEXT_FIELD).also {
            it.assertTextEquals("")
        }

        composeTestRule.onNodeWithTag(RestoreTag.AUTOCOMPLETE_LAYOUT).also {
            it.assertDoesNotExist()
        }

        composeTestRule.onNode(hasText("abandon") and hasTestTag(CommonTag.CHIP), useUnmergedTree = true).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithTag(RestoreTag.SEED_WORD_TEXT_FIELD).also {
            it.assertTextEquals("")
        }
    }

    @Test
    @MediumTest
    fun invalid_phrase_does_not_progress() {
        newTestSetup(generateSequence { "abandon" }.take(SeedPhrase.SEED_PHRASE_SIZE).toList())

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_complete_header)).also {
            it.assertDoesNotExist()
        }
    }

    @Test
    @MediumTest
    fun finish_appears_after_24_words() {
        newTestSetup(SeedPhraseFixture.new().split)

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_complete_header)).also {
            it.assertExists()
        }
    }

    @Test
    @MediumTest
    fun click_take_to_wallet() {
        val testSetup = newTestSetup(SeedPhraseFixture.new().split)

        assertEquals(0, testSetup.getOnFinishedCount())

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_button_see_wallet)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnFinishedCount())
    }

    @Test
    @MediumTest
    fun back() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.onNodeWithContentDescription(getStringResource(R.string.restore_back_content_description)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnBackCount())
    }

    @Test
    @MediumTest
    fun clear() {
        newTestSetup(listOf("abandon"))

        composeTestRule.onNode(hasText("abandon") and hasTestTag(CommonTag.CHIP), useUnmergedTree = true).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_button_clear)).also {
            it.performClick()
        }

        composeTestRule.onNode(hasText("abandon") and hasTestTag(CommonTag.CHIP), useUnmergedTree = true).also {
            it.assertDoesNotExist()
        }
    }

    private fun newTestSetup(initialState: List<String> = emptyList()) = TestSetup(composeTestRule, initialState)

    private class TestSetup(private val composeTestRule: ComposeContentTestRule, initialState: List<String>) {
        private val state = WordList(initialState)

        private val onBackCount = AtomicInteger(0)

        private val onFinishedCount = AtomicInteger(0)

        fun getUserInputWords(): List<String> {
            composeTestRule.waitForIdle()
            return state.current.value
        }

        fun getOnBackCount(): Int {
            composeTestRule.waitForIdle()
            return onBackCount.get()
        }

        fun getOnFinishedCount(): Int {
            composeTestRule.waitForIdle()
            return onFinishedCount.get()
        }

        init {
            composeTestRule.setContent {
                ZcashTheme {
                    RestoreWallet(
                        Mnemonics.getCachedWords(Locale.ENGLISH.language).toSortedSet(),
                        state,
                        onBack = {
                            onBackCount.incrementAndGet()
                        },
                        paste = { "" },
                        onFinished = {
                            onFinishedCount.incrementAndGet()
                        }
                    )
                }
            }
        }
    }
}
