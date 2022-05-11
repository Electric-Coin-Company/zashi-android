package co.electriccoin.zcash.ui.screen.update.model

import co.electriccoin.zcash.ui.screen.update.AppUpdateChecker
import com.google.android.play.core.appupdate.AppUpdateInfo

data class UpdateInfo(
    val priority: AppUpdateChecker.Priority,
    val isForce: Boolean,
    val appUpdateInfo: AppUpdateInfo?,
    val state: UpdateState
)
