package co.electriccoin.zcash.ui.screen.exchangerate.optin

data class ExchangeRateOptInState(
    val onEnableClick: () -> Unit,
    val onBack: () -> Unit,
    val onSkipClick: () -> Unit,
)
