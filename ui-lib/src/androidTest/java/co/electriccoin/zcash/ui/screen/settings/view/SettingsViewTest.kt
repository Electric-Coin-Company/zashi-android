package co.electriccoin.zcash.ui.screen.settings.view

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import androidx.test.filters.SmallTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.settings.SettingsTag
import co.electriccoin.zcash.ui.screen.settings.SettingsViewTestSetup
import co.electriccoin.zcash.ui.screen.settings.fixture.TroubleshootingParametersFixture
import co.electriccoin.zcash.ui.test.getStringResource
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SettingsViewTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun on_back_test() {
        val testSetup = SettingsViewTestSetup(composeTestRule, TroubleshootingParametersFixture.new())

        assertEquals(0, testSetup.getBackCount())

        composeTestRule.onNodeWithContentDescription(
            getStringResource(R.string.settings_back_content_description)
        ).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getBackCount())
    }

    @Test
    @MediumTest
    fun on_feedback_test() {
        val testSetup = SettingsViewTestSetup(composeTestRule, TroubleshootingParametersFixture.new())

        assertEquals(0, testSetup.getFeedbackCount())

        composeTestRule.onNodeWithText(
            getStringResource(R.string.settings_send_us_feedback)
        ).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getFeedbackCount())
    }

    @Test
    @MediumTest
    fun on_backup_test() {
        val testSetup = SettingsViewTestSetup(composeTestRule, TroubleshootingParametersFixture.new())

        assertEquals(0, testSetup.getBackupCount())

        composeTestRule.onNodeWithText(
            getStringResource(R.string.settings_backup_wallet)
        ).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getBackupCount())
    }

    @Test
    @MediumTest
    fun on_documentation_test() {
        val testSetup = SettingsViewTestSetup(composeTestRule, TroubleshootingParametersFixture.new())

        assertEquals(0, testSetup.getDocumentationCount())

        composeTestRule.onNodeWithText(
            getStringResource(R.string.settings_documentation)
        ).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getDocumentationCount())
    }

    @Test
    @MediumTest
    fun on_privacy_policy_test() {
        val testSetup = SettingsViewTestSetup(composeTestRule, TroubleshootingParametersFixture.new())

        assertEquals(0, testSetup.getPrivacyPolicyCount())

        composeTestRule.onNodeWithText(
            getStringResource(R.string.settings_privacy_policy)
        ).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getPrivacyPolicyCount())
    }

    @Test
    @MediumTest
    fun on_about_test() {
        val testSetup = SettingsViewTestSetup(composeTestRule, TroubleshootingParametersFixture.new())

        assertEquals(0, testSetup.getAboutCount())

        composeTestRule.onNodeWithText(
            getStringResource(R.string.settings_about)
        ).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getAboutCount())
    }

    @Test
    @SmallTest
    fun troubleshooting_menu_visible_test() {
        SettingsViewTestSetup(composeTestRule, TroubleshootingParametersFixture.new(isEnabled = true))

        composeTestRule.onNodeWithTag(SettingsTag.TROUBLESHOOTING_MENU).also {
            it.assertExists()
        }
    }

    @Test
    @SmallTest
    fun troubleshooting_menu_not_visible_test() {
        SettingsViewTestSetup(composeTestRule, TroubleshootingParametersFixture.new(isEnabled = false))

        composeTestRule.onNodeWithTag(SettingsTag.TROUBLESHOOTING_MENU).also {
            it.assertDoesNotExist()
        }
    }

    @Test
    @MediumTest
    fun troubleshooting_rescan_test() {
        val testSetup = SettingsViewTestSetup(
            composeTestRule,
            TroubleshootingParametersFixture.new(
                isEnabled = true,
                isRescanEnabled = true
            )
        )

        assertEquals(0, testSetup.getRescanCount())

        composeTestRule.openTroubleshootingMenu()

        composeTestRule.onNodeWithText(getStringResource(R.string.settings_troubleshooting_rescan)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getRescanCount())
    }

    @Test
    @MediumTest
    fun troubleshooting_background_sync_test() {
        val testSetup = SettingsViewTestSetup(
            composeTestRule,
            TroubleshootingParametersFixture.new(
                isEnabled = true,
                isBackgroundSyncEnabled = true
            )
        )

        assertEquals(0, testSetup.getBackgroundSyncCount())

        composeTestRule.openTroubleshootingMenu()

        composeTestRule.onNodeWithText(
            getStringResource(R.string.settings_troubleshooting_enable_background_sync)
        ).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getBackgroundSyncCount())
    }

    @Test
    @MediumTest
    fun troubleshooting_keep_screen_on_during_sync_test() {
        val testSetup = SettingsViewTestSetup(
            composeTestRule,
            TroubleshootingParametersFixture.new(
                isEnabled = true,
                isKeepScreenOnDuringSyncEnabled = true
            )
        )

        assertEquals(0, testSetup.getKeepScreenOnSyncCount())

        composeTestRule.openTroubleshootingMenu()

        composeTestRule.onNodeWithText(
            getStringResource(R.string.settings_troubleshooting_enable_keep_screen_on)
        ).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getKeepScreenOnSyncCount())
    }

    @Test
    @MediumTest
    fun troubleshooting_analytics_test() {
        val testSetup = SettingsViewTestSetup(
            composeTestRule,
            TroubleshootingParametersFixture.new(
                isEnabled = true,
                isAnalyticsEnabled = true
            )
        )

        assertEquals(0, testSetup.getAnalyticsCount())

        composeTestRule.openTroubleshootingMenu()

        composeTestRule.onNodeWithText(
            getStringResource(R.string.settings_troubleshooting_enable_analytics)
        ).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getAnalyticsCount())
    }
}

fun ComposeContentTestRule.openTroubleshootingMenu() {
    onNodeWithContentDescription(
        getStringResource(R.string.settings_troubleshooting_menu_content_description)
    ).also {
        it.performClick()
    }
}
