@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.update_available.view.UpdateAvailable
import co.electriccoin.zcash.ui.screen.update_available.viewmodel.UpdateAvailableViewModel

@Composable
internal fun MainActivity.WrapUpdateAvailable() {
    WrapUpdateAvailable(this)
}

@Composable
internal fun WrapUpdateAvailable(activity: ComponentActivity) {
    val viewModel by activity.viewModels<UpdateAvailableViewModel>()
    val updateInfo = viewModel.updateInfo.collectAsState().value

    UpdateAvailable(
        updateInfo,
        onDownload = {
            viewModel.goForUpdate()
        },
        onLater = {
            viewModel.skipUpdate()
        }
    )
}
