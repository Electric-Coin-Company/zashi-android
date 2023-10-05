package co.electriccoin.zcash.ui.screen.settings.model

data class TroubleshootingParameters(
    val isEnabled: Boolean,
    val isBackgroundSyncEnabled: Boolean,
    val isKeepScreenOnDuringSyncEnabled: Boolean,
    val isAnalyticsEnabled: Boolean,
    val isRescanEnabled: Boolean,
)
