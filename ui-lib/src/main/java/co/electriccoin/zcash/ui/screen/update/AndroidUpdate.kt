package co.electriccoin.zcash.ui.screen.update

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.home.viewmodel.CheckUpdateViewModel
import co.electriccoin.zcash.ui.screen.update.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update.model.UpdateState
import co.electriccoin.zcash.ui.screen.update.util.PlayStoreUtil
import co.electriccoin.zcash.ui.screen.update.view.Update
import co.electriccoin.zcash.ui.screen.update.viewmodel.UpdateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun MainActivity.WrapCheckForUpdate() {
    WrapCheckForUpdate(this)
}

@Composable
private fun WrapCheckForUpdate(activity: ComponentActivity) {
    // TODO [#382]: https://github.com/zcash/secant-android-wallet/issues/382
    // TODO [#403]: https://github.com/zcash/secant-android-wallet/issues/403
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val checkUpdateViewModel by activity.viewModels<CheckUpdateViewModel> {
        CheckUpdateViewModel.CheckUpdateViewModelFactory(
            activity.application,
            AppUpdateCheckerImp.new()
        )
    }

    val updateInfo = checkUpdateViewModel.updateInfo.collectAsState().value

    updateInfo?.let {
        if (it.appUpdateInfo != null && it.state == UpdateState.Prepared) {
            WrapUpdate(activity, updateInfo)
        }
    }

    // Check for an app update asynchronously. We create an effect that matches the activity
    // lifecycle. If the wrapping compose recomposes, the check shouldn't run again.
    LaunchedEffect(true) {
        checkUpdateViewModel.checkForAppUpdate()
    }
}

@Composable
private fun WrapUpdate(
    activity: ComponentActivity,
    inputUpdateInfo: UpdateInfo
) {
    val viewModel by activity.viewModels<UpdateViewModel> {
        UpdateViewModel.UpdateViewModelFactory(
            activity.application,
            inputUpdateInfo,
            AppUpdateCheckerImp.new()
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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

    Update(
        snackbarHostState,
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
            openPlayStoreAppPage(
                activity.applicationContext,
                snackbarHostState,
                scope
            )
        }
    )
}

fun openPlayStoreAppPage(
    context: Context,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    val storeIntent = PlayStoreUtil.newActivityIntent(context)
    runCatching {
        context.startActivity(storeIntent)
    }.onFailure {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = context.getString(R.string.update_unable_to_open_play_store)
            )
        }
    }
}
