@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateStage
import co.electriccoin.zcash.ui.screen.update_available.view.UpdateAvailable
import co.electriccoin.zcash.ui.screen.update_available.viewmodel.UpdateAvailableViewModel
import com.google.android.play.core.appupdate.AppUpdateInfo

@Composable
internal fun MainActivity.WrapUpdateAvailable(
    appUpdateInfo: AppUpdateInfo?,
    onDone: () -> Unit
) {
    WrapUpdateAvailable(
        activity = this,
        appUpdateInfo = appUpdateInfo,
        onDone = onDone
    )
}

@Composable
internal fun WrapUpdateAvailable(
    activity: ComponentActivity,
    appUpdateInfo: AppUpdateInfo?,
    onDone: () -> Unit
) {
    val viewModel by activity.viewModels<UpdateAvailableViewModel> {
        UpdateAvailableViewModel.UpdateAvailableViewModelFactory(
            activity.application,
            appUpdateInfo,
            AppUpdateCheckerImp.new()
        )
    }

    val updateInfo = viewModel.updateInfo.collectAsState().value

    when (updateInfo.stage) {
        UpdateStage.Done, UpdateStage.Canceled -> {
            onDone()
            return
        }
        UpdateStage.Failed -> {
            // we need to refresh AppUpdateInfo object, as it can be used only once
            viewModel.checkForAppUpdate(activity)
        }
        UpdateStage.Prepared, UpdateStage.Running -> {
            // valid stages
        }
    }

    UpdateAvailable(
        updateInfo,
        onDownload = {
            viewModel.goForUpdate(
                activity,
                updateInfo.appUpdateInfo
            )
        },
        onLater = {
            viewModel.remindLater()
        }
    )
}
