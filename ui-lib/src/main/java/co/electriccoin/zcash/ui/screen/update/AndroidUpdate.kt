package co.electriccoin.zcash.ui.screen.update

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.annotation.VisibleForTesting
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.viewmodel.CheckUpdateViewModel
import co.electriccoin.zcash.ui.screen.update.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update.model.UpdateState
import co.electriccoin.zcash.ui.screen.update.view.Update
import co.electriccoin.zcash.ui.screen.update.viewmodel.UpdateViewModel
import co.electriccoin.zcash.ui.util.PlayStoreUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

@Composable
internal fun WrapCheckForUpdate() {
    // TODO [#403]: Manual testing of already implemented in-app update mechanisms
    // TODO [#403]: https://github.com/Electric-Coin-Company/zashi-android/issues/403
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val checkUpdateViewModel = koinActivityViewModel<CheckUpdateViewModel>()

    val activity = LocalActivity.current

    val inputUpdateInfo = checkUpdateViewModel.updateInfo.collectAsStateWithLifecycle().value ?: return

    val viewModel = koinActivityViewModel<UpdateViewModel> { parametersOf(inputUpdateInfo) }
    val updateInfo = viewModel.updateInfo.collectAsStateWithLifecycle().value

    if (updateInfo.appUpdateInfo != null && updateInfo.state == UpdateState.Prepared) {
        WrapUpdate(
            updateInfo = updateInfo,
            checkForUpdate = viewModel::checkForAppUpdate,
            remindLater = viewModel::remindLater,
            goForUpdate = {
                viewModel.goForUpdate(
                    activity = activity,
                    appUpdateInfo = updateInfo.appUpdateInfo
                )
            }
        )
    }

    // Check for an app update asynchronously. We create an effect that matches the activity
    // lifecycle. If the wrapping compose recomposes, the check shouldn't run again.
    LaunchedEffect(true) {
        checkUpdateViewModel.checkForAppUpdate()
    }
}

@VisibleForTesting
@Composable
internal fun WrapUpdate(
    updateInfo: UpdateInfo,
    checkForUpdate: () -> Unit,
    remindLater: () -> Unit,
    goForUpdate: () -> Unit,
) {
    val activity = LocalActivity.current

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    when (updateInfo.state) {
        UpdateState.Done, UpdateState.Canceled -> {
            // just return as we are already in Home compose
            return
        }

        UpdateState.Failed -> {
            // we need to refresh AppUpdateInfo object, as it can be used only once
            checkForUpdate()
        }

        UpdateState.Prepared, UpdateState.Running -> {
            // valid stages
        }
    }

    val onLaterAction = {
        if (!updateInfo.isForce && updateInfo.state != UpdateState.Running) {
            remindLater()
        }
    }

    BackHandler {
        onLaterAction()
    }

    Update(
        snackbarHostState,
        updateInfo,
        onDownload = {
            // in this state of the update we have the AppUpdateInfo filled
            requireNotNull(updateInfo.appUpdateInfo)
            goForUpdate()
        },
        onLater = onLaterAction,
        onReference = {
            openPlayStoreAppSite(
                activity.applicationContext,
                snackbarHostState,
                scope
            )
        }
    )
}

private fun openPlayStoreAppSite(
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
                message = context.getString(R.string.unable_to_open_play_store)
            )
        }
    }
}
