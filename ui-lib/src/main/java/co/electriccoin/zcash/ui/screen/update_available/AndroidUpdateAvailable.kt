@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.update_available.view.UpdateAvailable
import co.electriccoin.zcash.ui.screen.update_available.viewmodel.UpdateAvailableViewModel
import com.google.android.play.core.appupdate.AppUpdateInfo

@Composable
internal fun MainActivity.WrapUpdateAvailable(appUpdateInfo: AppUpdateInfo) {
    WrapUpdateAvailable(this, appUpdateInfo)
}

@Composable
internal fun WrapUpdateAvailable(activity: ComponentActivity, appUpdateInfo: AppUpdateInfo) {
    val viewModel by activity.viewModels<UpdateAvailableViewModel> {
        UpdateAvailableViewModel.UpdateAvailableViewModelFactory(activity.application, appUpdateInfo)
    }

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
