package co.electriccoin.zcash.ui.screen.seed.backup

data class SeedBackupState(
    val onBack: () -> Unit,
    val onNextClick: () -> Unit,
    val onInfoClick: () -> Unit,
)
