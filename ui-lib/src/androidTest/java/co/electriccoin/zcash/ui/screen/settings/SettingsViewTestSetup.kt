package co.electriccoin.zcash.ui.screen.settings

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.settings.model.SettingsState
import co.electriccoin.zcash.ui.screen.settings.model.SettingsTroubleshootingState
import co.electriccoin.zcash.ui.screen.settings.model.TroubleshootingItemState
import co.electriccoin.zcash.ui.screen.settings.view.Settings
import java.util.concurrent.atomic.AtomicInteger

class SettingsViewTestSetup(
    private val composeTestRule: ComposeContentTestRule,
    isTroubleshootingEnabled: Boolean = false,
    isBackgroundSyncEnabled: Boolean = false,
    isKeepScreenOnDuringSyncEnabled: Boolean = false,
    isAnalyticsEnabled: Boolean = false,
    isRescanEnabled: Boolean = false
) {
    private val onBackCount = AtomicInteger(0)
    private val onFeedbackCount = AtomicInteger(0)
    private val onAdvancedSettingsCount = AtomicInteger(0)
    private val onAboutCount = AtomicInteger(0)
    private val onRescanCount = AtomicInteger(0)
    private val onBackgroundSyncChangedCount = AtomicInteger(0)
    private val onKeepScreenOnChangedCount = AtomicInteger(0)
    private val onAnalyticsChangedCount = AtomicInteger(0)
    private val onAddressBookCount = AtomicInteger(0)

    private val settingsTroubleshootingState =
        if (isTroubleshootingEnabled) {
            SettingsTroubleshootingState(
                rescan =
                    TroubleshootingItemState(isRescanEnabled) {
                        onRescanCount.incrementAndGet()
                    },
                backgroundSync =
                    TroubleshootingItemState(isBackgroundSyncEnabled) {
                        onBackgroundSyncChangedCount.incrementAndGet()
                    },
                keepScreenOnDuringSync =
                    TroubleshootingItemState(isKeepScreenOnDuringSyncEnabled) {
                        onKeepScreenOnChangedCount.incrementAndGet()
                    },
                analytics =
                    TroubleshootingItemState(isAnalyticsEnabled) {
                        onAnalyticsChangedCount.incrementAndGet()
                    }
            )
        } else {
            null
        }

    fun getBackCount(): Int {
        composeTestRule.waitForIdle()
        return onBackCount.get()
    }

    fun getFeedbackCount(): Int {
        composeTestRule.waitForIdle()
        return onFeedbackCount.get()
    }

    fun getAdvancedSettingsCount(): Int {
        composeTestRule.waitForIdle()
        return onAdvancedSettingsCount.get()
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

    fun getAddressBookCount(): Int {
        composeTestRule.waitForIdle()
        return onAddressBookCount.get()
    }

    init {
        composeTestRule.setContent {
            ZcashTheme {
                Settings(
                    state =
                        SettingsState(
                            isLoading = false,
                            version = stringRes("app_version"),
                            settingsTroubleshootingState = settingsTroubleshootingState,
                            onBack = {
                                onBackCount.incrementAndGet()
                            },
                            onSendUsFeedbackClick = {
                                onFeedbackCount.incrementAndGet()
                            },
                            onAdvancedSettingsClick = {
                                onAdvancedSettingsCount.incrementAndGet()
                            },
                            onAboutUsClick = {
                                onAboutCount.incrementAndGet()
                            },
                            onAddressBookClick = {
                                onAddressBookCount.incrementAndGet()
                            }
                        ),
                    topAppBarSubTitleState = TopAppBarSubTitleState.None,
                )
            }
        }
    }
}
