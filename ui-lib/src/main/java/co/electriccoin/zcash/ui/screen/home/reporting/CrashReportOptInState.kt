package co.electriccoin.zcash.ui.screen.home.reporting

data class CrashReportOptInState(
    val onBack: () -> Unit,
    val onOptOutClick: () -> Unit,
    val onOptInClick: () -> Unit,
)
