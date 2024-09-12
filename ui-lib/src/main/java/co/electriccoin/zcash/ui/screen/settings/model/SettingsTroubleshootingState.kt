package co.electriccoin.zcash.ui.screen.settings.model

import androidx.compose.runtime.Immutable

@Immutable
data class SettingsTroubleshootingState(
    val backgroundSync: TroubleshootingItemState,
    val keepScreenOnDuringSync: TroubleshootingItemState,
    val analytics: TroubleshootingItemState,
    val rescan: TroubleshootingItemState,
)

@Immutable
data class TroubleshootingItemState(
    val isEnabled: Boolean,
    val onClick: () -> Unit,
)
