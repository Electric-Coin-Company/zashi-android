package co.electriccoin.zcash.ui.screen.syncnotification

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.showMessage
import co.electriccoin.zcash.ui.screen.home.viewmodel.HomeViewModel
import co.electriccoin.zcash.ui.screen.scan.util.SettingsUtil
import co.electriccoin.zcash.ui.screen.syncnotification.view.SyncNotification
import co.electriccoin.zcash.ui.screen.syncnotification.viewmodel.SyncNotificationViewModel

@Composable
internal fun MainActivity.AndroidSyncNotification(onBack: () -> Unit) {
    WrapSyncNotification(activity = this, onBack = onBack)
}

@Composable
internal fun WrapSyncNotification(activity: ComponentActivity, onBack: () -> Unit) {
    val homeViewModel by activity.viewModels<HomeViewModel>()
    val syncNotificationViewModel by activity.viewModels<SyncNotificationViewModel>()

    DisposableEffect(key1 = Unit) {
        val previousVisibility = homeViewModel.isBottomNavBarVisible.value
        homeViewModel.onBottomNavBarVisibilityChanged(show = false)
        onDispose {
            homeViewModel.onBottomNavBarVisibilityChanged(show = previousVisibility)
        }
    }

    val syncIntervalOption = syncNotificationViewModel.syncIntervalOption.collectAsStateWithLifecycle().value

    if (syncIntervalOption != null) {
        SyncNotification(
            selectedSyncOption = syncIntervalOption,
            onBack = onBack,
            onSyncOptionSelected = syncNotificationViewModel::updateSyncIntervalOption,
            openSettings = {
                runCatching {
                    activity.startActivity(SettingsUtil.newSettingsIntent(activity.packageName))
                }.onFailure {
                    activity.showMessage(activity.getString(R.string.scan_settings_open_failed))
                }
            }
        )
    }
}
