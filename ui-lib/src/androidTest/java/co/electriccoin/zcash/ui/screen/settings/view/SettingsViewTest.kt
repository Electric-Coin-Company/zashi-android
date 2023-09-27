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
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.settings.SettingsTag
import co.electriccoin.zcash.ui.screen.settings.fixture.TroubleshootingParametersFixture
import co.electriccoin.zcash.ui.screen.settings.model.TroubleshootingParameters
import co.electriccoin.zcash.ui.test.getStringResource
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class SettingsViewTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun back() {
        val testSetup = TestSetup(composeTestRule, TroubleshootingParametersFixture.new())

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.onNodeWithContentDescription(
            getStringResource(R.string.settings_back_content_description)
        ).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnBackCount())
    }

    @Test
    @SmallTest
    fun troubleshooting_menu_visible_test() {
        TestSetup(composeTestRule, TroubleshootingParametersFixture.new(isEnabled = true))

        composeTestRule.onNodeWithTag(SettingsTag.TROUBLESHOOTING_MENU).also {
            it.assertExists()
        }
    }

    @Test
    @SmallTest
    fun troubleshooting_menu_not_visible_test() {
        TestSetup(composeTestRule, TroubleshootingParametersFixture.new(isEnabled = false))

        composeTestRule.onNodeWithTag(SettingsTag.TROUBLESHOOTING_MENU).also {
            it.assertDoesNotExist()
        }
    }

    @Test
    @MediumTest
    fun troubleshooting_rescan_test() {
        val testSetup = TestSetup(
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
        val testSetup = TestSetup(
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
        val testSetup = TestSetup(
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
        val testSetup = TestSetup(
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

    private class TestSetup(
        private val composeTestRule: ComposeContentTestRule,
        private val troubleshootingParameters: TroubleshootingParameters
    ) {
        private val onBackCount = AtomicInteger(0)
        private val onBackupCount = AtomicInteger(0)
        private val onRescanCount = AtomicInteger(0)
        private val onBackgroundSyncChangedCount = AtomicInteger(0)
        private val onKeepScreenOnChangedCount = AtomicInteger(0)
        private val onAnalyticsChangedCount = AtomicInteger(0)

        fun getOnBackCount(): Int {
            composeTestRule.waitForIdle()
            return onBackCount.get()
        }

        fun getBackupCount(): Int {
            composeTestRule.waitForIdle()
            return onBackupCount.get()
        }

        fun getRescanCount(): Int {
            composeTestRule.waitForIdle()
            return onRescanCount.get()
        }

        fun getBackgroundSyncCount(): Int {
            composeTestRule.waitForIdle()
            return onBackgroundSyncChangedCount.get()
        }

        fun getKeepScreenOnSyncCount(): Int {
            composeTestRule.waitForIdle()
            return onKeepScreenOnChangedCount.get()
        }

        fun getAnalyticsCount(): Int {
            composeTestRule.waitForIdle()
            return onAnalyticsChangedCount.get()
        }

        init {
            composeTestRule.setContent {
                ZcashTheme {
                    Settings(
                        troubleshootingParameters = troubleshootingParameters,
                        onBack = {
                            onBackCount.incrementAndGet()
                        },
                        onRescanWallet = {
                            onRescanCount.incrementAndGet()
                        },
                        onBackgroundSyncSettingsChanged = {
                            onBackgroundSyncChangedCount.incrementAndGet()
                        },
                        onKeepScreenOnDuringSyncSettingsChanged = {
                            onKeepScreenOnChangedCount.incrementAndGet()
                        },
                        onAnalyticsSettingsChanged = {
                            onAnalyticsChangedCount.incrementAndGet()
                        }
                    )
                }
            }
        }
    }
}

fun ComposeContentTestRule.openTroubleshootingMenu() {
    onNodeWithContentDescription(
        getStringResource(R.string.settings_troubleshooting_menu_content_description)
    ).also {
        it.performClick()
    }
}
