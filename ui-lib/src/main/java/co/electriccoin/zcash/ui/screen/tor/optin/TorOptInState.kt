package co.electriccoin.zcash.ui.screen.tor.optin

data class TorOptInState(
    val onEnableClick: () -> Unit,
    val onBack: () -> Unit,
    val onSkipClick: () -> Unit,
)
