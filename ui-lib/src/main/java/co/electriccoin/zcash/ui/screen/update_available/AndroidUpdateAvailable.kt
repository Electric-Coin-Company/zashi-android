@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateState
import co.electriccoin.zcash.ui.screen.update_available.util.PlayStoreUtil
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
            // AppUpdateCheckerTest.new() MANUAL_IN_APP_UPDATE_TEST (use instead of AppUpdateCheckerImp)
            AppUpdateCheckerImp.new()
        )
    }

    val updateInfo = viewModel.updateInfo.collectAsState().value

    // In this state of the update we should already have the AppUpdateInfo filled.
    requireNotNull(updateInfo.appUpdateInfo)

    when (updateInfo.state) {
        UpdateState.Done, UpdateState.Canceled -> {
            // just return as we are already in Home compose
            return
        }
        UpdateState.Failed -> {
            // we need to refresh AppUpdateInfo object, as it can be used only once
            viewModel.checkForAppUpdate()
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
        onReference = {
            openPlayStoreAppPage(activity.applicationContext)
        }
    )
}

fun openPlayStoreAppPage(context: Context) {
    val storeIntent = PlayStoreUtil.newActivityIntent(context)
    context.startActivity(storeIntent)
}
