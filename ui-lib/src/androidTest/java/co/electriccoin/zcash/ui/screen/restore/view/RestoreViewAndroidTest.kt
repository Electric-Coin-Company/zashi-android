package co.electriccoin.zcash.ui.screen.restore.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build.VERSION_CODES
import android.view.inputmethod.InputMethodManager
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.withKeyDown
import androidx.test.filters.MediumTest
import androidx.test.filters.SdkSuppress
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.sdk.fixture.SeedPhraseFixture
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.CommonTag
import co.electriccoin.zcash.ui.screen.restore.RestoreTag
import co.electriccoin.zcash.ui.screen.restore.model.RestoreStage
import co.electriccoin.zcash.ui.test.getAppContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertFalse

// Non-multiplatform tests that require interacting with the Android system (e.g. clipboard, Context)
// These don't have persistent state, so they are still unit tests.
class RestoreViewAndroidTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun keyboard_appears_on_launch() {
        newTestSetup()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(RestoreTag.SEED_WORD_TEXT_FIELD).also {
            it.assertIsFocused()
        }

        val inputMethodManager = getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        assertTrue(inputMethodManager.isAcceptingText)
    }

    @OptIn(ExperimentalTestApi::class, ExperimentalComposeUiApi::class)
    @Test
    @MediumTest
    // Functionality should be compatible with Android 27+, but a bug in the Android framework causes a crash
    // on Android 27.  Further, copying to the clipboard seems to be broken in emulators until API 33 (even in
    // other apps like the Contacts app). We haven't been able to test this on physical devices yet, but
    // we're assuming that it works.
    @SdkSuppress(minSdkVersion = VERSION_CODES.TIRAMISU)
    fun paste_too_many_words() {
        val testSetup = newTestSetup()

        copyToClipboard(
            getAppContext(),
            SeedPhraseFixture.SEED_PHRASE + " " + SeedPhraseFixture.SEED_PHRASE
        )

        composeTestRule.onNodeWithTag(RestoreTag.SEED_WORD_TEXT_FIELD).also {
            it.performKeyInput {
                withKeyDown(Key.CtrlLeft) {
                    pressKey(Key.V)
                }
            }
        }

        assertEquals(SeedPhrase.SEED_PHRASE_SIZE, testSetup.getUserInputWords().size)

        composeTestRule.onNodeWithTag(RestoreTag.SEED_WORD_TEXT_FIELD).also {
            it.assertDoesNotExist()
        }

        composeTestRule.onNodeWithTag(RestoreTag.AUTOCOMPLETE_LAYOUT).also {
            it.assertDoesNotExist()
        }

        composeTestRule.onAllNodes(hasTestTag(CommonTag.CHIP), useUnmergedTree = true).also {
            it.assertCountEquals(SeedPhrase.SEED_PHRASE_SIZE)
        }
    }

    @Test
    @MediumTest
    fun keyboard_disappears_after_seed() {
        newTestSetup()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(RestoreTag.SEED_WORD_TEXT_FIELD).also {
            it.performTextInput(SeedPhraseFixture.SEED_PHRASE)
        }

        composeTestRule.waitForIdle()

        val inputMethodManager = getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        assertFalse(inputMethodManager.isAcceptingText)
    }

    private fun newTestSetup(
        initialStage: RestoreStage = RestoreStage.Seed,
        initialWordsList: List<String> = emptyList()
    ) = RestoreViewTest.TestSetup(composeTestRule, initialStage, initialWordsList)
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboardManager = context.getSystemService(ClipboardManager::class.java)
    val data = ClipData.newPlainText(
        context.getString(R.string.new_wallet_clipboard_tag),
        text
    )
    clipboardManager.setPrimaryClip(data)
}
