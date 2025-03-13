@file:Suppress("TooManyFunctions")

package co.electroniccoin.zcash.ui.screenshot

import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.activity.viewModels
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.screenshot.captureToBitmap
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.filters.SdkSuppress
import cash.z.ecc.android.sdk.fixture.WalletAddressFixture
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.sdk.fixture.MemoFixture
import cash.z.ecc.sdk.fixture.SeedPhraseFixture
import cash.z.ecc.sdk.type.ZcashCurrency
import co.electriccoin.zcash.spackle.FirebaseTestLabUtil
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.NavigationTargets
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.viewmodel.SecretState
import co.electriccoin.zcash.ui.design.component.ConfigurationOverride
import co.electriccoin.zcash.ui.design.component.UiMode
import co.electriccoin.zcash.ui.screen.account.AccountTag
import co.electriccoin.zcash.ui.screen.authentication.view.AnimationConstants.WELCOME_ANIM_TEST_TAG
import co.electriccoin.zcash.ui.screen.home.HomeTag
import co.electriccoin.zcash.ui.screen.restore.RestoreTag
import co.electriccoin.zcash.ui.screen.restore.viewmodel.RestoreViewModel
import co.electriccoin.zcash.ui.screen.securitywarning.view.SecurityScreenTag.ACKNOWLEDGE_CHECKBOX_TAG
import co.electriccoin.zcash.ui.screen.send.SendTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

private const val DEFAULT_TIMEOUT_MILLISECONDS = 10_000L

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
        fun takeScreenshot(
            tag: String,
            screenshotName: String
        ) {
            onView(isRoot())
                .captureToBitmap()
                .writeToTestStorage("$screenshotName - $tag")
        }
    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule(MainActivity::class.java)

    private fun navigateTo(route: String) =
        runBlocking {
            withContext(Dispatchers.Main) {
                composeTestRule.activity.navControllerForTesting.navigate(route)
            }
        }

    private fun ComposeContentTestRule.navigateInHomeTab(destinationTag: String) {
        onNodeWithTag(destinationTag).also {
            it.assertExists()
            it.performClick()
        }
    }

    private fun runWith(
        uiMode: UiMode,
        locale: String,
        action: (Context, String) -> Unit
    ) {
        val configurationOverride = ConfigurationOverride(uiMode, LocaleList.forLanguageTags(locale))
        composeTestRule.activity.configurationOverrideFlow.value = configurationOverride

        val applicationContext = ApplicationProvider.getApplicationContext<Context>()
        val configuration = configurationOverride.newConfiguration(applicationContext.resources.configuration)
        val resContext = applicationContext.createConfigurationContext(configuration)

        action(resContext, "$uiMode-$locale")
    }

    @Test
    @LargeTest
    fun takeScreenshotsForRestoreWalletLightEnXA() {
        runWith(UiMode.Light, "en-XA") { context, tag ->
            takeScreenshotsForRestoreWallet(context, tag)
        }
    }

    @Test
    @LargeTest
    fun takeScreenshotsForRestoreWalletLightArXB() {
        runWith(UiMode.Light, "ar-XB") { context, tag ->
            takeScreenshotsForRestoreWallet(context, tag)
        }
    }

    @Test
    @LargeTest
    fun takeScreenshotsForRestoreWalletLightEnUS() {
        runWith(UiMode.Light, "en-US") { context, tag ->
            takeScreenshotsForRestoreWallet(context, tag)
        }
    }

    @Test
    @LargeTest
    fun takeScreenshotsForRestoreWalletLightEsSP() {
        runWith(UiMode.Light, "es-SP") { context, tag ->
            takeScreenshotsForRestoreWallet(context, tag)
        }
    }

    // Dark mode was introduced in Android Q
    @Test
    @LargeTest
    fun takeScreenshotsForRestoreWalletDarkEnUS() {
        runWith(UiMode.Dark, "en-US") { context, tag ->
            takeScreenshotsForRestoreWallet(context, tag)
        }
    }

    @Test
    @LargeTest
    fun takeScreenshotsForRestoreWalletDarkEsSP() {
        runWith(UiMode.Dark, "es-SP") { context, tag ->
            takeScreenshotsForRestoreWallet(context, tag)
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Suppress("LongMethod", "CyclomaticComplexMethod")
    private fun takeScreenshotsForRestoreWallet(
        resContext: Context,
        tag: String
    ) {
        // TODO [#286]: Screenshot tests fail on Firebase Test Lab
        // TODO [#286]: https://github.com/Electric-Coin-Company/zashi-android/issues/286
        if (FirebaseTestLabUtil.isFirebaseTestLab(ApplicationProvider.getApplicationContext())) {
            return
        }

        composeTestRule.waitUntil(DEFAULT_TIMEOUT_MILLISECONDS) {
            composeTestRule.activity.walletViewModel.secretState.value is SecretState.None
        }

        composeTestRule.waitUntilDoesNotExist(hasTestTag(WELCOME_ANIM_TEST_TAG), DEFAULT_TIMEOUT_MILLISECONDS)

        composeTestRule
            .onNodeWithText(
                text =
                    resContext.getString(
                        R.string.onboarding_import_existing_wallet
                    ),
                ignoreCase = true
            ).also {
                it.performScrollTo()
                it.assertExists()
                it.performClick()
            }

        // To ensure that the new screen is available, or wait until it is
        composeTestRule.waitUntilAtLeastOneExists(
            hasText(resContext.getString(R.string.restore_title)),
            DEFAULT_TIMEOUT_MILLISECONDS
        )

        composeTestRule.onNodeWithText(resContext.getString(R.string.restore_title)).also {
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

        composeTestRule.waitUntil {
            composeTestRule.activity
                .viewModels<RestoreViewModel>()
                .value.userWordList.current.value.size ==
                SeedPhrase.SEED_PHRASE_SIZE
        }

        composeTestRule
            .onNodeWithText(
                text = resContext.getString(R.string.restore_seed_button_next),
                ignoreCase = true
            ).also {
                // Even with waiting for the word list in the view model, there's some latency before the button is
                // enabled
                composeTestRule.waitUntil(5.seconds.inWholeMilliseconds) {
                    runCatching { it.assertIsEnabled() }.isSuccess
                }
                it.performScrollTo()
                it.performClick()
            }

        composeTestRule.onNodeWithText(resContext.getString(R.string.restore_birthday_header)).also {
            it.assertExists()
        }

        takeScreenshot(tag, "Import 3")

        composeTestRule
            .onNodeWithText(
                text = resContext.getString(R.string.restore_birthday_button_restore),
                ignoreCase = true
            ).also {
                it.performScrollTo()
                it.performClick()
            }

        composeTestRule.waitUntil(DEFAULT_TIMEOUT_MILLISECONDS) {
            composeTestRule
                .onNodeWithText(
                    text = resContext.getString(R.string.restore_success_button),
                    ignoreCase = true
                ).exists()
        }

        composeTestRule
            .onNodeWithText(
                text = resContext.getString(R.string.restore_success_button),
                ignoreCase = true
            ).also {
                it.performScrollTo()
                it.performClick()
            }

        composeTestRule.waitUntil(DEFAULT_TIMEOUT_MILLISECONDS) {
            composeTestRule.activity.walletViewModel.currentWalletSnapshot.value != null
        }

        composeTestRule.waitUntilDoesNotExist(hasTestTag(ACKNOWLEDGE_CHECKBOX_TAG), DEFAULT_TIMEOUT_MILLISECONDS)
    }

    @Test
    @LargeTest
    fun takeScreenshotsForNewWalletAndRestOfAppLightEnXA() {
        runWith(UiMode.Light, "en-XA") { context, tag ->
            takeScreenshotsForNewWalletAndRestOfApp(context, tag)
        }
    }

    @Test
    @LargeTest
    fun takeScreenshotsForNewWalletAndRestOfAppLightArXB() {
        runWith(UiMode.Light, "ar-XB") { context, tag ->
            takeScreenshotsForNewWalletAndRestOfApp(context, tag)
        }
    }

    @Test
    @LargeTest
    fun takeScreenshotsForNewWalletAndRestOfAppLightEnUS() {
        runWith(UiMode.Light, "en-US") { context, tag ->
            takeScreenshotsForNewWalletAndRestOfApp(context, tag)
        }
    }

    @Test
    @LargeTest
    fun takeScreenshotsForNewWalletAndRestOfAppLightEsSP() {
        runWith(UiMode.Light, "es-SP") { context, tag ->
            takeScreenshotsForNewWalletAndRestOfApp(context, tag)
        }
    }

    // Dark mode was introduced in Android Q
    @Test
    @LargeTest
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.Q)
    fun takeScreenshotsForNewWalletAndRestOfAppDarkEnUS() {
        runWith(UiMode.Dark, "en-US") { context, tag ->
            takeScreenshotsForNewWalletAndRestOfApp(context, tag)
        }
    }

    @Test
    @LargeTest
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.Q)
    fun takeScreenshotsForNewWalletAndRestOfAppDarkEsSP() {
        runWith(UiMode.Dark, "es-SP") { context, tag ->
            takeScreenshotsForNewWalletAndRestOfApp(context, tag)
        }
    }

    @OptIn(ExperimentalTestApi::class)
    private fun takeScreenshotsForNewWalletAndRestOfApp(
        resContext: Context,
        tag: String
    ) {
        // TODO [#286]: Screenshot tests fail on Firebase Test Lab
        // TODO [#286]: https://github.com/Electric-Coin-Company/zashi-android/issues/286
        if (FirebaseTestLabUtil.isFirebaseTestLab(ApplicationProvider.getApplicationContext())) {
            return
        }

        // These are the home screen bottom navigation sub-screens
        onboardingScreenshots(resContext, tag, composeTestRule)

        // To ensure that the bottom tab is available, or wait until it is
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag(HomeTag.TAB_ACCOUNT), DEFAULT_TIMEOUT_MILLISECONDS)

        composeTestRule.navigateInHomeTab(HomeTag.TAB_ACCOUNT)
        accountScreenshots(tag, composeTestRule)

        composeTestRule.navigateInHomeTab(HomeTag.TAB_SEND)
        sendZecScreenshots(resContext, tag, composeTestRule)

        composeTestRule.navigateInHomeTab(HomeTag.TAB_RECEIVE)
        receiveZecScreenshots(resContext, tag, composeTestRule)

        composeTestRule.navigateInHomeTab(HomeTag.TAB_BALANCES)
        balancesScreenshots(resContext, tag, composeTestRule)

        navigateTo(NavigationTargets.SETTINGS)
        settingsScreenshots(resContext, tag, composeTestRule)

        // These are the Settings screen items
        // We could manually click on each one, which is a better integration test but a worse screenshot test
        navigateTo(NavigationTargets.SEED_RECOVERY)
        seedScreenshots(resContext, tag, composeTestRule)

        navigateTo(NavigationTargets.SUPPORT)
        supportScreenshots(resContext, tag, composeTestRule)

        navigateTo(NavigationTargets.EXPORT_PRIVATE_DATA)
        exportPrivateDataScreenshots(resContext, tag, composeTestRule)

        navigateTo(NavigationTargets.ABOUT)
        aboutScreenshots(resContext, tag, composeTestRule)
    }
}

@OptIn(ExperimentalTestApi::class)
private fun onboardingScreenshots(
    resContext: Context,
    tag: String,
    composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>
) {
    composeTestRule.waitUntil(DEFAULT_TIMEOUT_MILLISECONDS) {
        composeTestRule.activity.walletViewModel.secretState.value is SecretState.None
    }

    // Welcome screen
    composeTestRule
        .onNodeWithText(
            resContext.getString(R.string.onboarding_header, ZcashCurrency.getLocalizedName(resContext)),
            useUnmergedTree = true
        ).also {
            it.assertExists()
            ScreenshotTest.takeScreenshot(tag, "Onboarding 1")
        }

    composeTestRule.waitUntilDoesNotExist(hasTestTag(WELCOME_ANIM_TEST_TAG), DEFAULT_TIMEOUT_MILLISECONDS)

    composeTestRule
        .onNodeWithText(
            text = resContext.getString(R.string.onboarding_create_new_wallet),
            ignoreCase = true,
            useUnmergedTree = true
        ).also {
            it.performClick()
        }

    // Security Warning screen
    composeTestRule
        .onNodeWithText(
            text = resContext.getString(R.string.security_warning_acknowledge),
            ignoreCase = true,
            useUnmergedTree = true
        ).also {
            it.assertExists()
            it.performClick()
            ScreenshotTest.takeScreenshot(tag, "Security Warning")
        }
    composeTestRule
        .onNodeWithText(
            text = resContext.getString(R.string.security_warning_confirm),
            ignoreCase = true,
            useUnmergedTree = true
        ).performClick()

    composeTestRule.waitForIdle()

    composeTestRule.waitUntil {
        composeTestRule
            .onNodeWithText(
                text = resContext.getString(R.string.seed_recovery_next_button),
                ignoreCase = true,
                useUnmergedTree = true
            ).exists()
    }

    composeTestRule
        .onNodeWithText(
            text = resContext.getString(R.string.seed_recovery_next_button),
            ignoreCase = true,
            useUnmergedTree = true
        ).also {
            it.performScrollTo()
            it.performClick()
        }
}

private fun accountScreenshots(
    tag: String,
    composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>
) {
    composeTestRule.waitUntil(DEFAULT_TIMEOUT_MILLISECONDS) {
        composeTestRule.activity.walletViewModel.secretState.value is SecretState.Ready
    }
    composeTestRule.waitUntil(DEFAULT_TIMEOUT_MILLISECONDS) {
        composeTestRule.activity.walletViewModel.currentWalletSnapshot.value != null
    }

    composeTestRule.onNodeWithTag(AccountTag.BALANCE_VIEWS).also {
        it.assertExists()
        ScreenshotTest.takeScreenshot(tag, "Account 1")
    }
}

private fun balancesScreenshots(
    resContext: Context,
    tag: String,
    composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>
) {
    // TODO [#1127]: Implement Balances screen
    // TODO [#1127]: https://github.com/Electric-Coin-Company/zashi-android/issues/1127

    composeTestRule.onNodeWithText(resContext.getString(R.string.balances_title)).also {
        it.assertExists()
        ScreenshotTest.takeScreenshot(tag, "Balances 1")
    }
}

private fun settingsScreenshots(
    resContext: Context,
    tag: String,
    composeTestRule: ComposeTestRule
) {
    composeTestRule
        .onNode(
            hasText(resContext.getString(R.string.settings_feedback), ignoreCase = true)
        ).also {
            it.assertExists()
        }

    ScreenshotTest.takeScreenshot(tag, "Settings 1")
}

private fun receiveZecScreenshots(
    resContext: Context,
    tag: String,
    composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>
) {
    composeTestRule
        .onNode(
            hasText(
                text = resContext.getString(R.string.receive_header),
                ignoreCase = true
            )
        ).also {
            it.assertExists()
        }

    ScreenshotTest.takeScreenshot(tag, "Receive 1")
}

private fun sendZecScreenshots(
    resContext: Context,
    tag: String,
    composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>
) {
    composeTestRule.waitUntil(DEFAULT_TIMEOUT_MILLISECONDS) {
        composeTestRule.activity.walletViewModel.synchronizer.value != null
    }
    composeTestRule.waitUntil(DEFAULT_TIMEOUT_MILLISECONDS) {
        composeTestRule.activity.walletViewModel.currentWalletSnapshot.value != null
    }

    composeTestRule.onNode(hasText(resContext.getString(R.string.send_stage_send_title))).also {
        it.assertExists()
    }

    // Screenshot: Empty form
    ScreenshotTest.takeScreenshot(tag, "Send 1")

    composeTestRule
        .onNode(
            hasTestTag(SendTag.SEND_AMOUNT_FIELD)
        ).also {
            val separators = MonetarySeparators.current()

            it.performTextInput("0${separators.decimal}123")
        }

    composeTestRule.onNodeWithText(resContext.getString(R.string.send_address_hint)).also {
        it.performTextInput(WalletAddressFixture.UNIFIED_ADDRESS_STRING)
    }

    composeTestRule.onNodeWithText(resContext.getString(R.string.send_memo_hint)).also {
        it.performTextInput(MemoFixture.MEMO_STRING)
    }

    // To close soft keyboard to reveal the send button
    Espresso.closeSoftKeyboard()

    // Screenshot: Fulfilled form
    ScreenshotTest.takeScreenshot(tag, "Send 2")

    // The rest of the Send screens (i.e. Send Confirmation) depends on sufficient available balance which can'
    // achieve in this kind of the test in a reasonable time thanks to block synchronization
}

private fun supportScreenshots(
    resContext: Context,
    tag: String,
    composeTestRule: ComposeTestRule
) {
    composeTestRule.onNode(hasText(resContext.getString(R.string.support_header).uppercase())).also {
        it.assertExists()
    }

    ScreenshotTest.takeScreenshot(tag, "Support 1")
}

private fun exportPrivateDataScreenshots(
    resContext: Context,
    tag: String,
    composeTestRule: ComposeTestRule
) {
    composeTestRule.onNode(hasText(resContext.getString(R.string.export_data_header))).also {
        it.assertExists()
    }

    ScreenshotTest.takeScreenshot(tag, "Export Private Data 1")
}

private fun aboutScreenshots(
    resContext: Context,
    tag: String,
    composeTestRule: ComposeTestRule
) {
    composeTestRule
        .onNode(
            hasText(resContext.getString(R.string.about_title).uppercase())
        ).also {
            it.assertExists()
        }

    ScreenshotTest.takeScreenshot(tag, "About 1")
}

private fun seedScreenshots(
    resContext: Context,
    tag: String,
    composeTestRule: ComposeTestRule
) {
    composeTestRule.onNode(hasText(resContext.getString(R.string.seed_recovery_header))).also {
        it.assertExists()
    }

    ScreenshotTest.takeScreenshot(tag, "Seed 1")
}

@Suppress("SwallowedException", "TooGenericExceptionCaught")
private fun SemanticsNodeInteraction.exists(): Boolean =
    try {
        this.assertExists()
        true
    } catch (e: Throwable) {
        false
    }
