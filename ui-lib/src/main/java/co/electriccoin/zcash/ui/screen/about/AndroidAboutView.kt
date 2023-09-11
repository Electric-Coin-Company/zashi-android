@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.screen.about

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import co.electriccoin.zcash.configuration.AndroidConfigurationFactory
import co.electriccoin.zcash.spackle.getPackageInfoCompat
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.about.model.VersionInfo
import co.electriccoin.zcash.ui.screen.about.view.About

@Composable
internal fun MainActivity.WrapAbout(
    goBack: () -> Unit
) {
    WrapAbout(this, goBack)
}

@Composable
internal fun WrapAbout(
    activity: ComponentActivity,
    goBack: () -> Unit
) {
    val packageInfo = activity.packageManager.getPackageInfoCompat(activity.packageName, 0L)
    // val configurationProvider = AndroidConfigurationFactory.getInstance(activity.applicationContext)

    About(VersionInfo.new(packageInfo), goBack)

    // Allows an implicit way to force configuration refresh by simply visiting the About screen
    LaunchedEffect(key1 = true) {
        AndroidConfigurationFactory.getInstance(activity.applicationContext).hintToRefresh()
    }
}
