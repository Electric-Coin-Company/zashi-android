package co.electriccoin.zcash.ui.screen.settings.fixture

import co.electriccoin.zcash.ui.screen.settings.model.TroubleshootingParameters

internal object TroubleshootingParametersFixture {
    internal const val ENABLED = false
    internal const val BACKGROUND_SYNC_ENABLED = false
    internal const val KEEP_SCREEN_ON_DURING_SYNC_ENABLED = false
    internal const val ANALYTICS_ENABLED = false
    internal const val RESCAN_ENABLED = false

    fun new(
        isEnabled: Boolean = ENABLED,
        isBackgroundSyncEnabled: Boolean = BACKGROUND_SYNC_ENABLED,
        isKeepScreenOnDuringSyncEnabled: Boolean = KEEP_SCREEN_ON_DURING_SYNC_ENABLED,
        isAnalyticsEnabled: Boolean = ANALYTICS_ENABLED,
        isRescanEnabled: Boolean = RESCAN_ENABLED,
    ) = TroubleshootingParameters(
        isEnabled,
        isBackgroundSyncEnabled,
        isKeepScreenOnDuringSyncEnabled,
        isAnalyticsEnabled,
        isRescanEnabled
    )
}
