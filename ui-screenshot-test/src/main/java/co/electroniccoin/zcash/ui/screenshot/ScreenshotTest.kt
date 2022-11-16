package co.electroniccoin.zcash.ui.screenshot

import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.screenshot.captureToBitmap
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import androidx.test.filters.SdkSuppress
import cash.z.ecc.sdk.ext.ui.model.MonetarySeparators
import cash.z.ecc.sdk.fixture.SeedPhraseFixture
import cash.z.ecc.sdk.fixture.WalletAddressFixture
import co.electriccoin.zcash.spackle.FirebaseTestLabUtil
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.NavigationTargets
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ConfigurationOverride
import co.electriccoin.zcash.ui.design.component.UiMode
import co.electriccoin.zcash.ui.screen.backup.BackupTag
import co.electriccoin.zcash.ui.screen.home.viewmodel.SecretState
import co.electriccoin.zcash.ui.screen.restore.RestoreTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Rule
import org.junit.Test

/*
 * This screenshot implementation does not change the system-wide configuration, but rather
 * injects a Context with a modified Configuration to change the uiMode and Locale.
 *
 * This works by:
 *   1. Main Activity wraps the Composable with an Override
 *   2. Main Activity exposes a Flow where a ConfigurationOverride can be set
 *   3. We use an altered resContext in the tests instead of Application Context in order to load
 *      the right resources for comparison.
 *
 * Benefits of this implementation are that we do not modify system-wide values and don't require
 * additional permissions to run these tests.
 *
 * Limitations of this implementation are that any views outside of Compose will not be updated, which
 * can include the on-screen keyboard, system dialogs (like permissions), or other UI elements.
 *
 * An alternative implementation would be to use AppCompatActivity as the parent class for MainActivity,
 * then rely on the AppCompat APIs for changing uiMode and Locale.  This doesn't bring much benefit over
 * our approach (it still has the problem with system dialogs and the keyboard), and it requires that
 * we pull in the appcompat library.
 */
class ScreenshotTest : UiTestPrerequisites() {

    companion object {
        fun takeScreenshot(tag: String, screenshotName: String) {
            onView(isRoot())
                .captureToBitmap()
                .writeToTestStorage("$screenshotName - $tag")
        }
    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule(MainActivity::class.java)

    private fun navigateTo(route: String) = runBlocking {
        withContext(Dispatchers.Main) {
            composeTestRule.activity.navControllerForTesting.navigate(route)
        }
    }

    private fun runWith(uiMode: UiMode, locale: String, action: (Context, String) -> Unit) {
        val configurationOverride = ConfigurationOverride(uiMode, LocaleList.forLanguageTags(locale))
        composeTestRule.activity.configurationOverrideFlow.value = configurationOverride

        val applicationContext = ApplicationProvider.getApplicationContext<Context>()
        val configuration = configurationOverride.newConfiguration(applicationContext.resources.configuration)
        val resContext = applicationContext.createConfigurationContext(configuration)

        action(resContext, "$uiMode-$locale")
    }

    @Test
    @MediumTest
    fun take_screenshots_for_restore_wallet_light_en_XA() {
        runWith(UiMode.Light, "en-XA") { context, tag ->
            take_screenshots_for_restore_wallet(context, tag)
        }
    }

    @Test
    @MediumTest
    fun take_screenshots_for_restore_wallet_light_ar_XB() {
        runWith(UiMode.Light, "ar-XB") { context, tag ->
            take_screenshots_for_restore_wallet(context, tag)
        }
    }

    @Test
    @MediumTest
    fun take_screenshots_for_restore_wallet_light_en_US() {
        runWith(UiMode.Light, "en-US") { context, tag ->
            take_screenshots_for_restore_wallet(context, tag)
        }
    }

    // Dark mode was introduced in Android Q
    @Test
    @MediumTest
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.Q)
    fun take_screenshots_for_restore_wallet_dark_en_US() {
        runWith(UiMode.Dark, "en-US") { context, tag ->
            take_screenshots_for_restore_wallet(context, tag)
        }
    }

    private fun take_screenshots_for_restore_wallet(resContext: Context, tag: String) {
        // TODO [#286]: Screenshot tests fail on Firebase Test Lab
        // TODO [#286]: https://github.com/zcash/secant-android-wallet/issues/286
        if (FirebaseTestLabUtil.isFirebaseTestLab(ApplicationProvider.getApplicationContext())) {
            return
        }

        composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.secretState.value is SecretState.None }

        composeTestRule.onNodeWithText(resContext.getString(R.string.onboarding_1_header)).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithText(resContext.getString(R.string.onboarding_skip)).also {
            it.assertExists()
            it.performClick()
        }

        composeTestRule.onNodeWithText(resContext.getString(R.string.onboarding_4_import_existing_wallet)).also {
            it.assertExists()
            it.performClick()
        }

        composeTestRule.onNodeWithText(resContext.getString(R.string.restore_header)).also {
            it.assertExists()
        }

        takeScreenshot(tag, "Import 1")

        val seedPhraseSplitLength = SeedPhraseFixture.new().split.size
        SeedPhraseFixture.new().split.forEachIndexed { index, string ->
            composeTestRule.onNodeWithTag(RestoreTag.SEED_WORD_TEXT_FIELD).also {
                it.performTextInput(string)

                // Take a screenshot half-way through filling in the seed phrase
                if (index == seedPhraseSplitLength / 2) {
                    takeScreenshot(tag, "Import 2")
                }
            }
        }

        composeTestRule.onNodeWithText(resContext.getString(R.string.restore_complete_header)).also {
            it.assertExists()
        }

        takeScreenshot(tag, "Import 3")
    }

    @Test
    @LargeTest
    fun take_screenshots_for_new_wallet_and_rest_of_app_light_en_XA() {
        runWith(UiMode.Light, "en-XA") { context, tag ->
            take_screenshots_for_new_wallet_and_rest_of_app(context, tag)
        }
    }

    @Test
    @LargeTest
    fun take_screenshots_for_new_wallet_and_rest_of_app_light_ar_XB() {
        runWith(UiMode.Light, "ar-XB") { context, tag ->
            take_screenshots_for_new_wallet_and_rest_of_app(context, tag)
        }
    }

    @Test
    @LargeTest
    fun take_screenshots_for_new_wallet_and_rest_of_app_light_en_US() {
        runWith(UiMode.Light, "en-US") { context, tag ->
            take_screenshots_for_new_wallet_and_rest_of_app(context, tag)
        }
    }

    // Dark mode was introduced in Android Q
    @Test
    @LargeTest
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.Q)
    fun take_screenshots_for_new_wallet_and_rest_of_app_dark_en_US() {
        runWith(UiMode.Dark, "en-US") { context, tag ->
            take_screenshots_for_new_wallet_and_rest_of_app(context, tag)
        }
    }

    private fun take_screenshots_for_new_wallet_and_rest_of_app(resContext: Context, tag: String) {
        // TODO [#286]: Screenshot tests fail on Firebase Test Lab
        // TODO [#286]: https://github.com/zcash/secant-android-wallet/issues/286
        if (FirebaseTestLabUtil.isFirebaseTestLab(ApplicationProvider.getApplicationContext())) {
            return
        }

        onboardingScreenshots(resContext, tag, composeTestRule)
        backupScreenshots(resContext, tag, composeTestRule)
        homeScreenshots(resContext, tag, composeTestRule)

        // Profile screen
        // navigateTo(MainActivity.NAV_PROFILE)
        composeTestRule.onNode(hasContentDescription(resContext.getString(R.string.home_profile_content_description))).also {
            it.assertExists()
            it.performClick()
        }
        profileScreenshots(resContext, tag, composeTestRule)

        // Settings is a subscreen of profile
        composeTestRule.onNode(hasText(resContext.getString(R.string.profile_settings))).also {
            it.performScrollTo()
            it.assertExists()
            it.performClick()
        }
        settingsScreenshots(resContext, tag, composeTestRule)

        // Back to profile
        composeTestRule.onNode(hasContentDescription(resContext.getString(R.string.settings_back_content_description))).also {
            it.assertExists()
            it.performClick()
        }

        // Address Details is a subscreen of profile
        composeTestRule.onNode(hasText(resContext.getString(R.string.profile_see_address_details))).also {
            it.performScrollTo()
            it.assertExists()
            it.performClick()
        }
        addressDetailsScreenshots(resContext, tag, composeTestRule)

        // Back to profile
        composeTestRule.onNode(hasContentDescription(resContext.getString(R.string.wallet_address_back_content_description))).also {
            it.assertExists()
            it.performClick()
        }

        // Contact Support is a subscreen of profile
        composeTestRule.onNode(hasText(resContext.getString(R.string.profile_support))).also {
            it.performScrollTo()
            it.assertExists()
            it.performClick()
        }
        supportScreenshots(resContext, tag, composeTestRule)

        // Back to profile
        composeTestRule.onNode(hasContentDescription(resContext.getString(R.string.support_back_content_description))).also {
            it.assertExists()
            it.performClick()
        }

        composeTestRule.onNode(hasText(resContext.getString(R.string.profile_title))).also {
            it.assertExists()
        }

        // About is a subscreen of profile
        composeTestRule.onNode(hasText(resContext.getString(R.string.profile_about))).also {
            it.performScrollTo()
            it.assertExists()
            it.performClick()
        }
        aboutScreenshots(resContext, tag, composeTestRule)

        // Back to profile
        composeTestRule.onNode(hasContentDescription(resContext.getString(R.string.about_back_content_description))).also {
            it.assertExists()
            it.performClick()
        }

        // Back to home
        composeTestRule.onNode(hasContentDescription(resContext.getString(R.string.settings_back_content_description))).also {
            it.assertExists()
            it.performClick()
        }

        composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.secretState.value is SecretState.Ready }
        composeTestRule.onNode(hasText(resContext.getString(R.string.home_button_request))).also {
            it.assertExists()
            it.performClick()
        }
        composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.walletSnapshot.value != null }
        requestZecScreenshots(resContext, tag, composeTestRule)

        navigateTo(NavigationTargets.HOME)
        composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.secretState.value is SecretState.Ready }

        composeTestRule.onNode(hasText(resContext.getString(R.string.home_button_send))).also {
            it.assertExists()
            it.performScrollTo()
            it.performClick()
        }
        composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.synchronizer.value != null }
        composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.spendingKey.value != null }
        composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.walletSnapshot.value != null }
        sendZecScreenshots(resContext, tag, composeTestRule)

        navigateTo(NavigationTargets.HOME)
    }
}

private fun onboardingScreenshots(resContext: Context, tag: String, composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) {
    composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.secretState.value is SecretState.None }

    composeTestRule.onNodeWithText(resContext.getString(R.string.onboarding_1_header)).also {
        it.assertExists()
    }
    ScreenshotTest.takeScreenshot(tag, "Onboarding 1")

    composeTestRule.onNodeWithText(resContext.getString(R.string.onboarding_next)).also {
        it.performClick()
    }

    composeTestRule.onNodeWithText(resContext.getString(R.string.onboarding_2_header)).also {
        it.assertExists()
        ScreenshotTest.takeScreenshot(tag, "Onboarding 2")
    }
    composeTestRule.onNodeWithText(resContext.getString(R.string.onboarding_next)).also {
        it.performClick()
    }

    composeTestRule.onNodeWithText(resContext.getString(R.string.onboarding_3_header)).also {
        it.assertExists()
        ScreenshotTest.takeScreenshot(tag, "Onboarding 3")
    }
    composeTestRule.onNodeWithText(resContext.getString(R.string.onboarding_next)).also {
        it.performClick()
    }

    composeTestRule.onNodeWithText(resContext.getString(R.string.onboarding_4_header)).also {
        it.assertExists()
        ScreenshotTest.takeScreenshot(tag, "Onboarding 4")
    }

    composeTestRule.onNodeWithText(resContext.getString(R.string.onboarding_4_create_new_wallet)).also {
        it.performClick()
    }
}

private fun backupScreenshots(resContext: Context, tag: String, composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) {
    composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.secretState.value is SecretState.NeedsBackup }

    composeTestRule.onNodeWithText(resContext.getString(R.string.new_wallet_1_header)).also {
        it.assertExists()
    }
    ScreenshotTest.takeScreenshot(tag, "Backup 1")

    composeTestRule.onNodeWithText(resContext.getString(R.string.new_wallet_1_button)).also {
        it.performClick()
    }

    composeTestRule.onNodeWithText(resContext.getString(R.string.new_wallet_2_header)).also {
        it.assertExists()
    }
    ScreenshotTest.takeScreenshot(tag, "Backup 2")

    composeTestRule.onNodeWithText(resContext.getString(R.string.new_wallet_2_button)).also {
        it.performClick()
    }

    composeTestRule.onNodeWithText(resContext.getString(R.string.new_wallet_3_header)).also {
        it.assertExists()
    }
    ScreenshotTest.takeScreenshot(tag, "Backup 3")

    composeTestRule.onNodeWithText(resContext.getString(R.string.new_wallet_3_button_finished)).also {
        it.performClick()
    }

    composeTestRule.onNodeWithText(resContext.getString(R.string.new_wallet_4_header)).also {
        it.assertExists()
    }
    ScreenshotTest.takeScreenshot(tag, "Backup 4")

    // Fail test first
    composeTestRule.onAllNodesWithTag(BackupTag.DROPDOWN_CHIP).also {
        it[0].performScrollTo()
        it[0].performClick()
        composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[0].performClick()

        it[1].performScrollTo()
        it[1].performClick()
        composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[1].performClick()

        it[2].performScrollTo()
        it[2].performClick()
        composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[2].performClick()

        it[3].performScrollTo()
        it[3].performClick()
        composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[3].performClick()
    }
    composeTestRule.onNodeWithText(resContext.getString(R.string.new_wallet_4_header_ouch)).also {
        it.assertExists()
        ScreenshotTest.takeScreenshot(tag, "Backup Fail")
    }

    composeTestRule.onNode(hasText(resContext.getString(R.string.new_wallet_4_button_retry))).also {
        it.performClick()
    }

    composeTestRule.onNodeWithText(resContext.getString(R.string.new_wallet_3_header)).also {
        it.assertExists()
    }
    composeTestRule.onNodeWithText(resContext.getString(R.string.new_wallet_3_button_finished)).also {
        it.performClick()
    }

    composeTestRule.onNodeWithText(resContext.getString(R.string.new_wallet_4_header)).also {
        it.assertExists()
    }

    composeTestRule.onAllNodesWithTag(BackupTag.DROPDOWN_CHIP).also {
        it.assertCountEquals(4)

        it[0].performScrollTo()
        it[0].performClick()
        composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[1].performClick()

        it[1].performScrollTo()
        it[1].performClick()
        composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[0].performClick()

        it[2].performScrollTo()
        it[2].performClick()
        composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[3].performClick()

        it[3].performScrollTo()
        it[3].performClick()
        composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[2].performClick()
    }

    composeTestRule.onNode(hasText(resContext.getString(R.string.new_wallet_5_body))).also {
        it.assertExists()
        ScreenshotTest.takeScreenshot(tag, "Backup 5")
    }

    composeTestRule.onNode(hasText(resContext.getString(R.string.new_wallet_5_button_finished))).also {
        it.assertExists()
        it.performClick()
    }
}

private fun homeScreenshots(resContext: Context, tag: String, composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) {
    composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.secretState.value is SecretState.Ready }
    composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.walletSnapshot.value != null }

    composeTestRule.onNode(hasText(resContext.getString(R.string.home_button_send))).also {
        it.assertExists()
        ScreenshotTest.takeScreenshot(tag, "Home 1")
    }
}

private fun profileScreenshots(resContext: Context, tag: String, composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) {
    composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.addresses.value != null }

    composeTestRule.onNode(hasText(resContext.getString(R.string.profile_title))).also {
        it.assertExists()
    }

    ScreenshotTest.takeScreenshot(tag, "Profile 1")
}

private fun settingsScreenshots(resContext: Context, tag: String, composeTestRule: ComposeTestRule) {
    composeTestRule.onNode(hasText(resContext.getString(R.string.settings_header))).also {
        it.assertExists()
    }

    ScreenshotTest.takeScreenshot(tag, "Settings 1")
}

private fun addressDetailsScreenshots(resContext: Context, tag: String, composeTestRule: ComposeTestRule) {
    composeTestRule.onNode(hasText(resContext.getString(R.string.wallet_address_title))).also {
        it.assertExists()
    }

    ScreenshotTest.takeScreenshot(tag, "Addresses 1")
}

private fun requestZecScreenshots(resContext: Context, tag: String, composeTestRule: ComposeTestRule) {
    composeTestRule.onNode(hasText(resContext.getString(R.string.request_title))).also {
        it.assertExists()
    }

    ScreenshotTest.takeScreenshot(tag, "Request 1")
}

private fun sendZecScreenshots(resContext: Context, tag: String, composeTestRule: ComposeTestRule) {
    composeTestRule.onNode(hasText(resContext.getString(R.string.send_title))).also {
        it.assertExists()
    }

    ScreenshotTest.takeScreenshot(tag, "Send 1")

    composeTestRule.onNodeWithText(resContext.getString(R.string.send_amount)).also {
        val separators = MonetarySeparators.current()

        it.performTextInput("{${separators.decimal}}123")
    }

    composeTestRule.onNodeWithText(resContext.getString(R.string.send_to)).also {
        it.performTextInput(WalletAddressFixture.UNIFIED_ADDRESS_STRING)
    }

    composeTestRule.onNodeWithText(resContext.getString(R.string.send_create)).also {
        it.performClick()
    }

    composeTestRule.waitForIdle()

    ScreenshotTest.takeScreenshot(tag, "Send 2")
}

private fun supportScreenshots(resContext: Context, tag: String, composeTestRule: ComposeTestRule) {
    composeTestRule.onNode(hasText(resContext.getString(R.string.support_header))).also {
        it.assertExists()
    }

    ScreenshotTest.takeScreenshot(tag, "Support 1")
}

private fun aboutScreenshots(resContext: Context, tag: String, composeTestRule: ComposeTestRule) {
    composeTestRule.onNode(hasText(resContext.getString(R.string.about_title))).also {
        it.assertExists()
    }

    ScreenshotTest.takeScreenshot(tag, "About 1")
}
