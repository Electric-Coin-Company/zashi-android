@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available.fixture

import co.electriccoin.zcash.ui.screen.update_available.AppUpdateChecker
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateState
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

object UpdateInfoFixture {

    val INITIAL_PRIORITY = AppUpdateChecker.Priority.LOW
    val INITIAL_STATE = UpdateState.Prepared
    const val INITIAL_FORCE = false

    // just for test purposes we use very simple AppUpdateInfo object instance
    val APP_UPDATE_INFO: AppUpdateInfo = AppUpdateInfo.zzb(
        "",
        -1,
        UpdateAvailability.UPDATE_AVAILABLE,
        InstallStatus.UNKNOWN,
        1,
        1,
        1,
        1,
        1,
        1,
        null,
        null,
        null,
        null
    )

    fun new(
        priority: AppUpdateChecker.Priority = INITIAL_PRIORITY,
        force: Boolean = INITIAL_FORCE,
        appUpdateInfo: AppUpdateInfo?,
        state: UpdateState = INITIAL_STATE
    ) = UpdateInfo(
        priority,
        force,
        appUpdateInfo,
        state
    )
}
