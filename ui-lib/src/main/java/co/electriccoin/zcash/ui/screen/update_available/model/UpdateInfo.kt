@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available.model

import co.electriccoin.zcash.ui.screen.update_available.AppUpdateChecker
import com.google.android.play.core.appupdate.AppUpdateInfo

data class UpdateInfo(
    val priority: AppUpdateChecker.Priority,
    var isForce: Boolean,
    var appUpdateInfo: AppUpdateInfo?,
    var state: UpdateState
)
