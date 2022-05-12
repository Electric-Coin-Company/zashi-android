package co.electriccoin.zcash.ui.screen.update.model

import co.electriccoin.zcash.ui.screen.update.AppUpdateChecker
import com.google.android.play.core.appupdate.AppUpdateInfo

// UpdateInfo can be refactored once to have stronger representation invariants
// (eliminate the null, priority + failed state probably doesn't have much meaning, etc).
//
// sealed class UpdateInfo {
//     data class Success(priority, info, state) : UpdateInfo()
//     object Failed : UpdateInfo()
// }

data class UpdateInfo(
    val priority: AppUpdateChecker.Priority,
    val isForce: Boolean,
    val appUpdateInfo: AppUpdateInfo?,
    val state: UpdateState
)
