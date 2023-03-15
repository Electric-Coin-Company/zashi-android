package co.electriccoin.zcash.ui.screen.restore.view

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
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
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.fixture.SeedPhraseFixture
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.CommonTag
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.restore.RestoreTag
import co.electriccoin.zcash.ui.screen.restore.model.RestoreStage
import co.electriccoin.zcash.ui.screen.restore.state.RestoreState
import co.electriccoin.zcash.ui.screen.restore.state.WordList
import co.electriccoin.zcash.ui.test.getStringResource
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertNull

class RestoreViewTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun seed_autocomplete_suggestions_appear() {
        newTestSetup()

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
    fun seed_choose_autocomplete() {
        newTestSetup()

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
    fun seed_type_full_word() {
        newTestSetup()

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
    fun seed_invalid_phrase_does_not_progress() {
        newTestSetup(initialWordsList = generateSequence { "abandon" }.take(SeedPhrase.SEED_PHRASE_SIZE).toList())

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_seed_button_restore)).also {
            it.assertIsNotEnabled()
        }
    }

    @Test
    @MediumTest
    fun seed_finish_appears_after_24_words() {
        newTestSetup(initialWordsList = SeedPhraseFixture.new().split)

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_seed_button_restore)).also {
            it.assertExists()
        }
    }

    @Test
    @MediumTest
    fun seed_clear() {
        newTestSetup(initialWordsList = listOf("abandon"))

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

    @Test
    @MediumTest
    fun height_skip() {
        val testSetup = newTestSetup(initialStage = RestoreStage.Birthday, initialWordsList = SeedPhraseFixture.new().split)

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_birthday_button_skip)).also {
            it.performClick()
        }

        assertEquals(testSetup.getRestoreHeight(), null)
        assertEquals(testSetup.getStage(), RestoreStage.Complete)
    }

    @Test
    @MediumTest
    fun height_set_valid() {
        val testSetup = newTestSetup(
            initialStage = RestoreStage.Birthday,
            initialWordsList = SeedPhraseFixture.new().split
        )

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_birthday_button_restore)).also {
            it.assertIsNotEnabled()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_birthday_hint)).also {
            it.performTextInput(ZcashNetwork.Mainnet.saplingActivationHeight.value.toString())
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_birthday_button_restore)).also {
            it.assertIsEnabled()
            it.performClick()
        }

        assertEquals(testSetup.getRestoreHeight(), ZcashNetwork.Mainnet.saplingActivationHeight)
        assertEquals(testSetup.getStage(), RestoreStage.Complete)
    }

    @Test
    @MediumTest
    fun height_set_valid_but_skip() {
        val testSetup = newTestSetup(
            initialStage = RestoreStage.Birthday,
            initialWordsList = SeedPhraseFixture.new().split
        )

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_birthday_button_restore)).also {
            it.assertIsNotEnabled()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_birthday_hint)).also {
            it.performTextInput(ZcashNetwork.Mainnet.saplingActivationHeight.value.toString())
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_birthday_button_skip)).also {
            it.performClick()
        }

        assertNull(testSetup.getRestoreHeight())
        assertEquals(testSetup.getStage(), RestoreStage.Complete)
    }

    @Test
    @MediumTest
    fun height_set_invalid_too_small() {
        val testSetup = newTestSetup(
            initialStage = RestoreStage.Birthday,
            initialWordsList = SeedPhraseFixture.new().split
        )

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_birthday_button_restore)).also {
            it.assertIsNotEnabled()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_birthday_hint)).also {
            it.performTextInput((ZcashNetwork.Mainnet.saplingActivationHeight.value - 1L).toString())
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_birthday_button_restore)).also {
            it.assertIsNotEnabled()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_birthday_button_skip)).also {
            it.performClick()
        }

        assertNull(testSetup.getRestoreHeight())
        assertEquals(testSetup.getStage(), RestoreStage.Complete)
    }

    @Test
    @MediumTest
    fun height_set_invalid_non_digit() {
        val testSetup = newTestSetup(
            initialStage = RestoreStage.Birthday,
            initialWordsList = SeedPhraseFixture.new().split
        )

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_birthday_button_restore)).also {
            it.assertIsNotEnabled()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_birthday_hint)).also {
            it.performTextInput("1.2")
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_birthday_button_restore)).also {
            it.assertIsNotEnabled()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_birthday_button_skip)).also {
            it.performClick()
        }

        assertNull(testSetup.getRestoreHeight())
        assertEquals(testSetup.getStage(), RestoreStage.Complete)
    }

    @Test
    @MediumTest
    fun complete_click_take_to_wallet() {
        val testSetup = newTestSetup(
            initialStage = RestoreStage.Complete,
            initialWordsList = SeedPhraseFixture.new().split
        )

        assertEquals(0, testSetup.getOnFinishedCount())

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_button_see_wallet)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnFinishedCount())
    }

    @Test
    @MediumTest
    fun back_from_seed() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.onNodeWithContentDescription(getStringResource(R.string.restore_back_content_description)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnBackCount())
    }

    @MediumTest
    fun back_from_birthday() {
        val testSetup = newTestSetup(
            initialStage = RestoreStage.Birthday,
            initialWordsList = SeedPhraseFixture.new().split
        )

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.onNodeWithContentDescription(getStringResource(R.string.restore_back_content_description)).also {
            it.performClick()
        }

        assertEquals(testSetup.getStage(), RestoreStage.Seed)
        assertEquals(0, testSetup.getOnBackCount())
    }

    @MediumTest
    fun back_from_complete() {
        val testSetup = newTestSetup(
            initialStage = RestoreStage.Complete,
            initialWordsList = SeedPhraseFixture.new().split
        )

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.onNodeWithContentDescription(getStringResource(R.string.restore_back_content_description)).also {
            it.performClick()
        }

        assertEquals(testSetup.getStage(), RestoreStage.Birthday)
        assertEquals(0, testSetup.getOnBackCount())
    }

    private fun newTestSetup(
        initialStage: RestoreStage = RestoreStage.Seed,
        initialWordsList: List<String> = emptyList()
    ) = TestSetup(composeTestRule, initialStage, initialWordsList)

    internal class TestSetup(
        private val composeTestRule: ComposeContentTestRule,
        initialStage: RestoreStage,
        initialWordsList: List<String>
    ) {
        private val state = RestoreState(initialStage)

        private val wordList = WordList(initialWordsList)

        private val onBackCount = AtomicInteger(0)

        private val onFinishedCount = AtomicInteger(0)

        private val restoreHeight = MutableStateFlow<BlockHeight?>(null)

        fun getUserInputWords(): List<String> {
            composeTestRule.waitForIdle()
            return wordList.current.value
        }

        fun getStage(): RestoreStage {
            composeTestRule.waitForIdle()
            return state.current.value
        }

        fun getRestoreHeight(): BlockHeight? {
            composeTestRule.waitForIdle()
            return restoreHeight.value
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
                        ZcashNetwork.Mainnet,
                        state,
                        Mnemonics.getCachedWords(Locale.ENGLISH.language).toPersistentSet(),
                        wordList,
                        restoreHeight = restoreHeight.collectAsState().value,
                        setRestoreHeight = {
                            restoreHeight.value = it
                        },
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
