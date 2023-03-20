package co.electriccoin.zcash.ui.screen.restore.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build.VERSION_CODES
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.test.filters.MediumTest
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
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

    @Test
    @MediumTest
    // Functionality is compatible with Android 27+, but a bug in the Android framework prevents this
    // test from passing until API 28. See https://issuetracker.google.com/issues/141132133
    @SdkSuppress(minSdkVersion = VERSION_CODES.P)
    fun paste_too_many_words() {
        val testSetup = newTestSetup()

        copyToClipboard(
            getAppContext(),
            SeedPhraseFixture.SEED_PHRASE + " " + SeedPhraseFixture.SEED_PHRASE
        )

        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            .pressKeyCode(KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_MASK)

        assertEquals(testSetup.getUserInputWords().size, SeedPhrase.SEED_PHRASE_SIZE)

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
