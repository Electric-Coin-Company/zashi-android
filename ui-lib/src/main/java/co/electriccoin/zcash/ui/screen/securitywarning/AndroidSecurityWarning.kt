package co.electriccoin.zcash.ui.screen.securitywarning

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import co.electriccoin.zcash.configuration.AndroidConfigurationFactory
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.screen.securitywarning.view.SecurityWarning

@Composable
internal fun MainActivity.WrapSecurityWarning(
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    WrapSecurityWarning(
        this,
        onBack = onBack,
        onConfirm = onConfirm
    )
}

@Composable
internal fun WrapSecurityWarning(
    activity: ComponentActivity,
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    SecurityWarning(
        versionInfo = VersionInfo.new(activity.applicationContext),
        onBack = onBack,
        onAcknowledged = {
            // Needed for UI testing only
        },
        onConfirm = onConfirm
    )

    LaunchedEffect(key1 = true) {
        AndroidConfigurationFactory.getInstance(activity.applicationContext).hintToRefresh()
    }
}
