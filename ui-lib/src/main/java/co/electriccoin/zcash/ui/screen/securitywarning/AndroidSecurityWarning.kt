package co.electriccoin.zcash.ui.screen.securitywarning

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import co.electriccoin.zcash.configuration.api.ConfigurationProvider
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.screen.securitywarning.view.SecurityWarning
import org.koin.compose.koinInject

@Composable
internal fun WrapSecurityWarning(
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    val activity = LocalActivity.current
    val androidConfigurationProvider = koinInject<ConfigurationProvider>()
    BackHandler {
        onBack()
    }

    SecurityWarning(
        versionInfo = VersionInfo.new(activity.applicationContext),
        onBack = onBack,
        onAcknowledge = {
            // Needed for UI testing only
        },
        onConfirm = onConfirm
    )

    LaunchedEffect(key1 = true) {
        androidConfigurationProvider.hintToRefresh()
    }
}
