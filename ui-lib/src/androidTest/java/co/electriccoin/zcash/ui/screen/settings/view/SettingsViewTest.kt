package co.electriccoin.zcash.ui.screen.settings.view

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.test.getStringResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Ignore
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

        composeTestRule.onNodeWithContentDescription(getStringResource(R.string.settings_back_content_description)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnBackCount())
    }

    @Test
    @MediumTest
    fun backup() = runTest {
        val testSetup = TestSetup(composeTestRule)

        assertEquals(0, testSetup.getBackupCount())

        composeTestRule.onNodeWithText(getStringResource(R.string.settings_backup)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getBackupCount())
    }

    @Test
    @MediumTest
    fun rescan() = runTest {
        val testSetup = TestSetup(composeTestRule)

        assertEquals(0, testSetup.getBackupCount())

        composeTestRule.onNodeWithText(getStringResource(R.string.settings_rescan)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getRescanCount())
    }

    @Test
    @MediumTest
    fun toggle_analytics() = runTest {
        val testSetup = TestSetup(composeTestRule)

        assertEquals(0, testSetup.getBackupCount())

        composeTestRule.onNodeWithText(getStringResource(R.string.settings_enable_analytics)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getAnalyticsToggleCount())
    }

    @Test
    @MediumTest
    @Ignore("Wipe has been disabled in Settings and is now a debug-only option")
    fun wipe() = runTest {
        val testSetup = TestSetup(composeTestRule)

        assertEquals(0, testSetup.getBackupCount())

        composeTestRule.onNodeWithText(getStringResource(R.string.settings_wipe)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getWipeCount())
    }

    private class TestSetup(private val composeTestRule: ComposeContentTestRule) {

        private val onBackCount = AtomicInteger(0)
        private val onBackupCount = AtomicInteger(0)
        private val onRescanCount = AtomicInteger(0)
        private val onWipeCount = AtomicInteger(0)
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

        fun getWipeCount(): Int {
            composeTestRule.waitForIdle()
            return onWipeCount.get()
        }

        fun getAnalyticsToggleCount(): Int {
            composeTestRule.waitForIdle()
            return onAnalyticsChangedCount.get()
        }

        init {
            composeTestRule.setContent {
                ZcashTheme {
                    Settings(
                        isAnalyticsEnabled = true,
                        onBack = {
                            onBackCount.incrementAndGet()
                        },
                        onBackupWallet = {
                            onBackupCount.incrementAndGet()
                        },
                        onRescanWallet = {
                            onRescanCount.incrementAndGet()
                        },
                        onWipeWallet = {
                            onWipeCount.incrementAndGet()
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
