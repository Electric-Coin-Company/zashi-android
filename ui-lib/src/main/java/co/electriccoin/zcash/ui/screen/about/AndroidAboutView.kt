@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.screen.about

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import co.electriccoin.zcash.configuration.AndroidConfigurationFactory
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.screen.about.view.About
import co.electriccoin.zcash.ui.screen.support.model.ConfigInfo

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
    val configInfo = ConfigInfo.new(AndroidConfigurationFactory.getInstance(activity.applicationContext))
    val versionInfo = VersionInfo.new(activity.applicationContext)

    // Allows an implicit way to force configuration refresh by simply visiting the About screen
    LaunchedEffect(key1 = true) {
        AndroidConfigurationFactory.getInstance(activity.applicationContext).hintToRefresh()
    }

    About(
        onBack = goBack,
        versionInfo = versionInfo,
        configInfo = configInfo
    )
}
