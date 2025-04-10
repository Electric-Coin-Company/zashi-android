package co.electriccoin.zcash.ui.screen.exchangerate.settings

import androidx.compose.runtime.Immutable

@Immutable
data class ExchangeRateSettingsState(
    val isOptedIn: Boolean,
    val onSaveClick: (optIn: Boolean) -> Unit,
    val onDismiss: () -> Unit,
)