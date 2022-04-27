@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available.fixture

import co.electriccoin.zcash.ui.screen.update_available.AppUpdateChecker
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateStage
import com.google.android.play.core.appupdate.AppUpdateInfo

object UpdateInfoFixture {

    private val INITIAL_PRIORITY = AppUpdateChecker.Priority.LOW
    private const val INITIAL_FORCE = false
    private val INITIAL_STAGE = UpdateStage.Prepared

    fun new(
        priority: AppUpdateChecker.Priority = INITIAL_PRIORITY,
        force: Boolean = INITIAL_FORCE,
        appUpdateInfo: AppUpdateInfo?,
        updateStage: UpdateStage = INITIAL_STAGE
    ) = UpdateInfo(
        priority,
        force,
        appUpdateInfo,
        updateStage
    )
}
