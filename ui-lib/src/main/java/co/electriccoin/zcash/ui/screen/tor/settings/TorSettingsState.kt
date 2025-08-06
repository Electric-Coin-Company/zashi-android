package co.electriccoin.zcash.ui.screen.tor.settings

import androidx.compose.runtime.Immutable

@Immutable
data class TorSettingsState(
    val isOptedIn: Boolean,
    val onSaveClick: (optIn: Boolean) -> Unit,
    val onShareFeedbackClick: () -> Unit,
    val onDismiss: () -> Unit,
)
