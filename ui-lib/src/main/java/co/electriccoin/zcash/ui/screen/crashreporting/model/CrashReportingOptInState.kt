package co.electriccoin.zcash.ui.screen.crashreporting.model

data class CrashReportingOptInState(
    val isOptedIn: Boolean,
    val onBack: () -> Unit,
    val onSaveClicked: (enabled: Boolean) -> Unit,
) {
    companion object {
        fun new(
            isOptedIn: Boolean = true,
            onBack: () -> Unit = {},
            onSaveClicked: (enabled: Boolean) -> Unit = {}
        ) = CrashReportingOptInState(
            isOptedIn = isOptedIn,
            onBack = onBack,
            onSaveClicked = onSaveClicked
        )
    }
}
