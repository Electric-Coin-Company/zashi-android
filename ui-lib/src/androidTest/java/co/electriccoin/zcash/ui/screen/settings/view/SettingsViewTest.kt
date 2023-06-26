package co.electriccoin.zcash.ui.screen.settings.view

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.configuration.model.map.StringConfiguration
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.configuration.ConfigurationEntries
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.test.getStringResource
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun back() = runTest {
        val testSetup = TestSetup(composeTestRule)

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.onNodeWithContentDescription(
            getStringResource(R.string.settings_back_content_description)
        ).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnBackCount())
    }

    @Test
    @MediumTest
    fun rescan() = runTest {
        val testSetup = TestSetup(composeTestRule)

        if (ConfigurationEntries.IS_RESCAN_ENABLED.getValue(
                StringConfiguration(emptyMap<String, String>().toPersistentMap(), null)
            )
        ) {
            assertEquals(0, testSetup.getRescanCount())

            composeTestRule.onNodeWithContentDescription(
                getStringResource(R.string.settings_overflow_content_description)
            ).also {
                it.performClick()
            }

            composeTestRule.onNodeWithText(getStringResource(R.string.settings_rescan)).also {
                it.performClick()
            }

            assertEquals(1, testSetup.getRescanCount())
        }
    }

    @Test
    @MediumTest
    fun toggle_background_sync() = runTest {
        val testSetup = TestSetup(composeTestRule)

        assertEquals(0, testSetup.getBackgroundSyncToggleCount())

        composeTestRule.onNodeWithText(getStringResource(R.string.settings_enable_background_sync)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getBackgroundSyncToggleCount())
    }

    @Test
    @MediumTest
    fun toggle_keep_screen_on() = runTest {
        val testSetup = TestSetup(composeTestRule)

        assertEquals(0, testSetup.getKeepScreenOnSyncToggleCount())

        composeTestRule.onNodeWithText(getStringResource(R.string.settings_enable_keep_screen_on)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getKeepScreenOnSyncToggleCount())
    }

    @Test
    @MediumTest
    fun toggle_analytics() = runTest {
        val testSetup = TestSetup(composeTestRule)

        assertEquals(0, testSetup.getAnalyticsToggleCount())

        composeTestRule.onNodeWithText(getStringResource(R.string.settings_enable_analytics)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getAnalyticsToggleCount())
    }

    private class TestSetup(private val composeTestRule: ComposeContentTestRule) {

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

        fun getBackgroundSyncToggleCount(): Int {
            composeTestRule.waitForIdle()
            return onBackgroundSyncChangedCount.get()
        }

        fun getKeepScreenOnSyncToggleCount(): Int {
            composeTestRule.waitForIdle()
            return onKeepScreenOnChangedCount.get()
        }

        fun getAnalyticsToggleCount(): Int {
            composeTestRule.waitForIdle()
            return onAnalyticsChangedCount.get()
        }

        init {
            composeTestRule.setContent {
                ZcashTheme {
                    Settings(
                        isBackgroundSyncEnabled = true,
                        isKeepScreenOnDuringSyncEnabled = true,
                        isAnalyticsEnabled = true,
                        isRescanEnabled = true,
                        onBack = {
                            onBackCount.incrementAndGet()
                        },
                        onRescanWallet = {
                            onRescanCount.incrementAndGet()
                        },
                        onBackgroundSyncSettingsChanged = {
                            onBackgroundSyncChangedCount.incrementAndGet()
                        },
                        onIsKeepScreenOnDuringSyncSettingsChanged = {
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
