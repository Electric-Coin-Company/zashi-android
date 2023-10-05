package co.electriccoin.zcash.ui.screen.securitywarning

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import co.electriccoin.zcash.configuration.AndroidConfigurationFactory
import co.electriccoin.zcash.spackle.getPackageInfoCompat
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.about.model.VersionInfo
import co.electriccoin.zcash.ui.screen.securitywarning.view.SecurityWarning

@Composable
internal fun MainActivity.WrapSecurityWarning(
    goBack: () -> Unit
) {
    WrapSecurityWarning(
        this,
        goBack = goBack
    )
}

@Composable
internal fun WrapSecurityWarning(
    activity: ComponentActivity,
    goBack: () -> Unit
) {
    val packageInfo = activity.packageManager.getPackageInfoCompat(activity.packageName, 0L)

    SecurityWarning(
        VersionInfo.new(packageInfo),
        onBack = goBack
    )

    LaunchedEffect(key1 = true) {
        AndroidConfigurationFactory.getInstance(activity.applicationContext).hintToRefresh()
    }
}
