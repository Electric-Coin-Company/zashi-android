package co.electriccoin.zcash.ui.screen.home.backup

import androidx.compose.runtime.Immutable

@Immutable
data class WalletBackupDetailState(
    val onBack: () -> Unit,
    val onNextClick: () -> Unit,
    val onInfoClick: () -> Unit,
)
