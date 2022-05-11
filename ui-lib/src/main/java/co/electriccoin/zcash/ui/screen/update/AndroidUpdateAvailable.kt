package co.electriccoin.zcash.ui.screen.update

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.update.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update.model.UpdateState
import co.electriccoin.zcash.ui.screen.update.util.PlayStoreUtil
import co.electriccoin.zcash.ui.screen.update.view.UpdateAvailable
import co.electriccoin.zcash.ui.screen.update.viewmodel.UpdateAvailableViewModel

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
            // in this state of the update we have the AppUpdateInfo filled
            requireNotNull(updateInfo.appUpdateInfo)

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
