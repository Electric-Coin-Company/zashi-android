package co.electriccoin.zcash.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
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
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.SdkSuppress
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.screenshot.Screenshot
import cash.z.ecc.sdk.ext.ui.model.MonetarySeparators
import cash.z.ecc.sdk.fixture.SeedPhraseFixture
import cash.z.ecc.sdk.fixture.WalletAddressFixture
import co.electriccoin.zcash.app.test.EccScreenCaptureProcessor
import co.electriccoin.zcash.app.test.getStringResource
import co.electriccoin.zcash.spackle.FirebaseTestLabUtil
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.backup.BackupTag
import co.electriccoin.zcash.ui.screen.home.viewmodel.SecretState
import co.electriccoin.zcash.ui.screen.restore.RestoreTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

// TODO [#285]: Screenshot tests fail on older devices due to issue granting external storage permission
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.Q)
class ScreenshotTest : UiTestPrerequisites() {

    companion object {
        @BeforeClass
        @JvmStatic
        fun setupPPlus() {
            if (Build.VERSION_CODES.P <= Build.VERSION.SDK_INT) {
                val instrumentation = InstrumentationRegistry.getInstrumentation()
                if (PackageManager.PERMISSION_DENIED == instrumentation.context.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    instrumentation.uiAutomation.grantRuntimePermission(instrumentation.context.packageName, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }

        fun takeScreenshot(screenshotName: String) {
            val screenshot = Screenshot.capture().apply {
                name = screenshotName
            }
            screenshot.process(setOf(EccScreenCaptureProcessor.new()))
        }
    }

    private val composeTestRule = createAndroidComposeRule(MainActivity::class.java)

    @get:Rule
    val ruleChain = if (Build.VERSION_CODES.P <= Build.VERSION.SDK_INT) {
        composeTestRule
    } else {
        val runtimePermissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        RuleChain.outerRule(runtimePermissionRule).around(composeTestRule)
    }

    private fun navigateTo(route: String) = runBlocking {
        withContext(Dispatchers.Main) {
            composeTestRule.activity.navControllerForTesting.navigate(route)
        }
    }

    @Test
    @SmallTest
    fun take_screenshots_for_restore_wallet() {
        // TODO [#286]: Screenshot tests fail on Firebase Test Lab
        if (FirebaseTestLabUtil.isFirebaseTestLab(ApplicationProvider.getApplicationContext())) {
            return
        }

        composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.secretState.value is SecretState.None }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_1_header)).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_skip)).also {
            it.assertExists()
            it.performClick()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_4_import_existing_wallet)).also {
            it.assertExists()
            it.performClick()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_header)).also {
            it.assertExists()
        }

        takeScreenshot("Import 1")

        val seedPhraseSplitLength = SeedPhraseFixture.new().split.size
        SeedPhraseFixture.new().split.forEachIndexed { index, string ->
            composeTestRule.onNodeWithTag(RestoreTag.SEED_WORD_TEXT_FIELD).also {
                it.performTextInput(string)

                // Take a screenshot half-way through filling in the seed phrase
                if (index == seedPhraseSplitLength / 2) {
                    takeScreenshot("Import 2")
                }
            }
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.restore_complete_header)).also {
            it.assertExists()
        }

        takeScreenshot("Import 3")
    }

    @Test
    @SmallTest
    fun take_screenshots_for_new_wallet_and_rest_of_app() {
        // TODO [#286]: Screenshot tests fail on Firebase Test Lab
        if (FirebaseTestLabUtil.isFirebaseTestLab(ApplicationProvider.getApplicationContext())) {
            return
        }

        onboardingScreenshots(composeTestRule)
        backupScreenshots(composeTestRule)
        homeScreenshots(composeTestRule)

        // Profile screen
        // navigateTo(MainActivity.NAV_PROFILE)
        composeTestRule.onNode(hasContentDescription(getStringResource(R.string.home_profile_content_description))).also {
            it.assertExists()
            it.performClick()
        }
        profileScreenshots(composeTestRule)

        // Settings is a subscreen of profile
        composeTestRule.onNode(hasText(getStringResource(R.string.profile_settings))).also {
            it.assertExists()
            it.performClick()
        }
        settingsScreenshots(composeTestRule)

        // Back to profile
        composeTestRule.onNode(hasContentDescription(getStringResource(R.string.settings_back_content_description))).also {
            it.assertExists()
            it.performClick()
        }

        // Address Details is a subscreen of profile
        composeTestRule.onNode(hasText(getStringResource(R.string.profile_see_address_details))).also {
            it.assertExists()
            it.performClick()
        }
        addressDetailsScreenshots(composeTestRule)

        // Back to profile
        composeTestRule.onNode(hasContentDescription(getStringResource(R.string.wallet_address_back_content_description))).also {
            it.assertExists()
            it.performClick()
        }

        // Contact Support is a subscreen of profile
        composeTestRule.onNode(hasText(getStringResource(R.string.profile_support))).also {
            it.performScrollTo()
            it.assertExists()
            it.performClick()
        }
        supportScreenshots(composeTestRule)

        // Back to profile
        composeTestRule.onNode(hasContentDescription(getStringResource(R.string.support_back_content_description))).also {
            it.assertExists()
            it.performClick()
        }

        composeTestRule.onNode(hasText(getStringResource(R.string.profile_title))).also {
            it.assertExists()
            it.performClick()
        }

        // About is a subscreen of profile
        composeTestRule.onNode(hasText(getStringResource(R.string.profile_about))).also {
            it.performScrollTo()
            it.assertExists()
            it.performClick()
        }
        aboutScreenshots(composeTestRule)

        // Back to profile
        composeTestRule.onNode(hasContentDescription(getStringResource(R.string.about_back_content_description))).also {
            it.assertExists()
            it.performClick()
        }

        // Back to home
        composeTestRule.onNode(hasContentDescription(getStringResource(R.string.settings_back_content_description))).also {
            it.assertExists()
            it.performClick()
        }

        composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.secretState.value is SecretState.Ready }
        composeTestRule.onNode(hasText(getStringResource(R.string.home_button_request))).also {
            it.assertExists()
            it.performClick()
        }
        composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.walletSnapshot.value != null }
        requestZecScreenshots(composeTestRule)

        navigateTo(MainActivity.NAV_HOME)
        composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.secretState.value is SecretState.Ready }

        composeTestRule.onNode(hasText(getStringResource(R.string.home_button_send))).also {
            it.assertExists()
            it.performClick()
        }
        composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.synchronizer.value != null }
        composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.spendingKey.value != null }
        composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.walletSnapshot.value != null }
        sendZecScreenshots(composeTestRule)

        navigateTo(MainActivity.NAV_HOME)
    }
}

private fun onboardingScreenshots(composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) {
    composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.secretState.value is SecretState.None }

    composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_1_header)).also {
        it.assertExists()
    }
    ScreenshotTest.takeScreenshot("Onboarding 1")

    composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_next)).also {
        it.performClick()
    }

    composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_2_header)).also {
        it.assertExists()
        ScreenshotTest.takeScreenshot("Onboarding 2")
    }
    composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_next)).also {
        it.performClick()
    }

    composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_3_header)).also {
        it.assertExists()
        ScreenshotTest.takeScreenshot("Onboarding 3")
    }
    composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_next)).also {
        it.performClick()
    }

    composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_4_header)).also {
        it.assertExists()
        ScreenshotTest.takeScreenshot("Onboarding 4")
    }

    composeTestRule.onNodeWithText(getStringResource(R.string.onboarding_4_create_new_wallet)).also {
        it.performClick()
    }
}

private fun backupScreenshots(composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) {
    composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.secretState.value is SecretState.NeedsBackup }

    composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_1_header)).also {
        it.assertExists()
    }
    ScreenshotTest.takeScreenshot("Backup 1")

    composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_1_button)).also {
        it.performClick()
    }

    composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_2_header)).also {
        it.assertExists()
    }
    ScreenshotTest.takeScreenshot("Backup 2")

    composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_2_button)).also {
        it.performClick()
    }

    composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_3_header)).also {
        it.assertExists()
    }
    ScreenshotTest.takeScreenshot("Backup 3")

    composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_3_button_finished)).also {
        it.performClick()
    }

    composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_4_header_verify)).also {
        it.assertExists()
    }
    ScreenshotTest.takeScreenshot("Backup 4")

    // Fail test first
    composeTestRule.onAllNodesWithTag(BackupTag.DROPDOWN_CHIP).also {
        it[0].performClick()
        composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[0].performClick()

        it[1].performClick()
        composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[1].performClick()

        it[2].performClick()
        composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[2].performClick()

        it[3].performClick()
        composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[3].performClick()
    }
    composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_4_header_ouch)).also {
        it.assertExists()
        ScreenshotTest.takeScreenshot("Backup Fail")
    }

    composeTestRule.onNode(hasText(getStringResource(R.string.new_wallet_4_button_retry))).performClick()

    composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_3_header)).also {
        it.assertExists()
    }
    composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_3_button_finished)).also {
        it.performClick()
    }

    composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_4_header_verify)).also {
        it.assertExists()
    }

    composeTestRule.onAllNodesWithTag(BackupTag.DROPDOWN_CHIP).also {
        it.assertCountEquals(4)

        it[0].performClick()
        composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[1].performClick()

        it[1].performClick()
        composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[0].performClick()

        it[2].performClick()
        composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[3].performClick()

        it[3].performClick()
        composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[2].performClick()
    }

    composeTestRule.onNode(hasText(getStringResource(R.string.new_wallet_5_body))).also {
        it.assertExists()
        ScreenshotTest.takeScreenshot("Backup 5")
    }

    composeTestRule.onNode(hasText(getStringResource(R.string.new_wallet_5_button_finished))).also {
        it.assertExists()
        it.performClick()
    }
}

private fun homeScreenshots(composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) {
    composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.secretState.value is SecretState.Ready }
    composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.walletSnapshot.value != null }

    composeTestRule.onNode(hasText(getStringResource(R.string.home_button_send))).also {
        it.assertExists()
        ScreenshotTest.takeScreenshot("Home 1")
    }
}

private fun profileScreenshots(composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) {
    composeTestRule.waitUntil { composeTestRule.activity.walletViewModel.addresses.value != null }

    composeTestRule.onNode(hasText(getStringResource(R.string.profile_title))).also {
        it.assertExists()
    }

    ScreenshotTest.takeScreenshot("Profile 1")
}

private fun settingsScreenshots(composeTestRule: ComposeTestRule) {
    composeTestRule.onNode(hasText(getStringResource(R.string.settings_header))).also {
        it.assertExists()
    }

    ScreenshotTest.takeScreenshot("Settings 1")
}

private fun addressDetailsScreenshots(composeTestRule: ComposeTestRule) {
    composeTestRule.onNode(hasText(getStringResource(R.string.wallet_address_title))).also {
        it.assertExists()
    }

    ScreenshotTest.takeScreenshot("Addresses 1")
}

private fun requestZecScreenshots(composeTestRule: ComposeTestRule) {
    composeTestRule.onNode(hasText(getStringResource(R.string.request_title))).also {
        it.assertExists()
    }

    ScreenshotTest.takeScreenshot("Request 1")
}

private fun sendZecScreenshots(composeTestRule: ComposeTestRule) {
    composeTestRule.onNode(hasText(getStringResource(R.string.send_title))).also {
        it.assertExists()
    }

    ScreenshotTest.takeScreenshot("Send 1")

    composeTestRule.onNodeWithText(getStringResource(R.string.send_amount)).also {
        val separators = MonetarySeparators.current()

        it.performTextInput("{${separators.decimal}}123")
    }

    composeTestRule.onNodeWithText(getStringResource(R.string.send_to)).also {
        it.performTextInput(WalletAddressFixture.UNIFIED_ADDRESS_STRING)
    }

    composeTestRule.onNodeWithText(getStringResource(R.string.send_create)).also {
        it.performClick()
    }

    composeTestRule.waitForIdle()

    ScreenshotTest.takeScreenshot("Send 2")
}

private fun supportScreenshots(composeTestRule: ComposeTestRule) {
    composeTestRule.onNode(hasText(getStringResource(R.string.support_header))).also {
        it.assertExists()
    }

    ScreenshotTest.takeScreenshot("Support 1")
}

private fun aboutScreenshots(composeTestRule: ComposeTestRule) {
    composeTestRule.onNode(hasText(getStringResource(R.string.about_title))).also {
        it.assertExists()
    }

    ScreenshotTest.takeScreenshot("About 1")
}
