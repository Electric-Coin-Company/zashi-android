package co.electriccoin.zcash.ui.screen.securitywarning

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import co.electriccoin.zcash.configuration.AndroidConfigurationFactory
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.screen.securitywarning.view.SecurityWarning

@Composable
internal fun WrapSecurityWarning(
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    val activity = LocalActivity.current

    BackHandler {
        onBack()
    }

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
