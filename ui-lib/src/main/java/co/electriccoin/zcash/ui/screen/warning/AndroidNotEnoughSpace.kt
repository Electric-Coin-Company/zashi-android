@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.warning

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.screen.warning.view.NotEnoughSpaceView
import co.electriccoin.zcash.ui.screen.warning.viewmodel.StorageCheckViewModel
import co.electriccoin.zcash.ui.util.SettingsUtil
import kotlinx.coroutines.launch

@Composable
fun WrapNotEnoughSpace(
    goPrevious: () -> Unit,
    goSettings: () -> Unit
) {
    val activity = LocalActivity.current
    val storageCheckViewModel = koinActivityViewModel<StorageCheckViewModel>()

    val isEnoughFreeSpace = storageCheckViewModel.isEnoughSpace.collectAsStateWithLifecycle().value
    if (isEnoughFreeSpace == true) {
        goPrevious()
    }

    val requiredStorageSpaceGigabytes = storageCheckViewModel.requiredStorageSpaceGigabytes
    val spaceAvailableMegabytes = storageCheckViewModel.spaceAvailableMegabytes.collectAsStateWithLifecycle()

    BackHandler {
        activity.finish()
    }

    WrapNotEnoughFreeSpace(
        goSettings = goSettings,
        spaceAvailableMegabytes = spaceAvailableMegabytes.value ?: 0,
        requiredStorageSpaceGigabytes = requiredStorageSpaceGigabytes,
    )
}

@Composable
private fun WrapNotEnoughFreeSpace(
    goSettings: () -> Unit,
    requiredStorageSpaceGigabytes: Int,
    spaceAvailableMegabytes: Int,
) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    NotEnoughSpaceView(
        onSettings = goSettings,
        onSystemSettings = {
            runCatching {
                context.startActivity(SettingsUtil.newStorageSettingsIntent())
            }.onFailure {
                // This case should not really happen, as the Settings app should be available on every
                // Android device, but we rather handle it.
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.not_enough_space_settings_open_failed)
                    )
                }
            }
        },
        snackbarHostState = snackbarHostState,
        storageSpaceRequiredGigabytes = requiredStorageSpaceGigabytes,
        spaceAvailableMegabytes = spaceAvailableMegabytes,
    )
}
