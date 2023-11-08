package co.electriccoin.zcash.ui.screen.settings

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.settings.model.TroubleshootingParameters
import co.electriccoin.zcash.ui.screen.settings.view.Settings
import java.util.concurrent.atomic.AtomicInteger

class SettingsViewTestSetup(
    private val composeTestRule: ComposeContentTestRule,
    private val troubleshootingParameters: TroubleshootingParameters
) {
    private val onBackCount = AtomicInteger(0)
    private val onBackupCount = AtomicInteger(0)
    private val onDocumentationCount = AtomicInteger(0)
    private val onPrivacyPolicyCount = AtomicInteger(0)
    private val onFeedbackCount = AtomicInteger(0)
    private val onExportPrivateData = AtomicInteger(0)
    private val onAboutCount = AtomicInteger(0)
    private val onRescanCount = AtomicInteger(0)
    private val onBackgroundSyncChangedCount = AtomicInteger(0)
    private val onKeepScreenOnChangedCount = AtomicInteger(0)
    private val onAnalyticsChangedCount = AtomicInteger(0)

    fun getBackCount(): Int {
        composeTestRule.waitForIdle()
        return onBackCount.get()
    }

    fun getBackupCount(): Int {
        composeTestRule.waitForIdle()
        return onBackupCount.get()
    }

    fun getDocumentationCount(): Int {
        composeTestRule.waitForIdle()
        return onDocumentationCount.get()
    }

    fun getPrivacyPolicyCount(): Int {
        composeTestRule.waitForIdle()
        return onPrivacyPolicyCount.get()
    }

    fun getFeedbackCount(): Int {
        composeTestRule.waitForIdle()
        return onFeedbackCount.get()
    }

    fun getExportPrivateDataCount(): Int {
        composeTestRule.waitForIdle()
        return onExportPrivateData.get()
    }

    fun getAboutCount(): Int {
        composeTestRule.waitForIdle()
        return onAboutCount.get()
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
                    onBackup = {
                        onBackupCount.incrementAndGet()
                    },
                    onDocumentation = {
                        onDocumentationCount.incrementAndGet()
                    },
                    onPrivacyPolicy = {
                        onPrivacyPolicyCount.incrementAndGet()
                    },
                    onFeedback = {
                        onFeedbackCount.incrementAndGet()
                    },
                    onExportPrivateData = {
                        onExportPrivateData.incrementAndGet()
                    },
                    onAbout = {
                        onAboutCount.incrementAndGet()
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
