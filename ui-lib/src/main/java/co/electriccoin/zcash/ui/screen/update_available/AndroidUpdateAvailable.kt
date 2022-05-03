@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateState
import co.electriccoin.zcash.ui.screen.update_available.view.UpdateAvailable
import co.electriccoin.zcash.ui.screen.update_available.viewmodel.UpdateAvailableViewModel

@Composable
internal fun MainActivity.WrapUpdateAvailable(
    updateInfo: UpdateInfo
) {
    WrapUpdateAvailable(
        activity = this,
        inputUpdateInfo = updateInfo
    )
}

@Composable
internal fun WrapUpdateAvailable(
    activity: ComponentActivity,
    inputUpdateInfo: UpdateInfo
) {
    val viewModel by activity.viewModels<UpdateAvailableViewModel> {
        UpdateAvailableViewModel.UpdateAvailableViewModelFactory(
            activity.application,
            inputUpdateInfo,
            AppUpdateCheckerImp.new()
        )
    }

    val updateInfo = viewModel.updateInfo.collectAsState().value

    when (updateInfo.state) {
        UpdateState.Done, UpdateState.Canceled -> {
            // just return as we are already in Home compose
            return
        }
        UpdateState.Failed -> {
            // we need to refresh AppUpdateInfo object, as it can be used only once
            viewModel.checkForAppUpdate(activity)
        }
        UpdateState.Prepared, UpdateState.Running -> {
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
        },
        onReference = {}
    )
}
