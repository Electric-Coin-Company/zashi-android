package co.electriccoin.zcash.ui.fixture

import co.electriccoin.zcash.ui.AppUpdateChecker
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateInfo
import com.google.android.play.core.appupdate.AppUpdateInfo

object UpdateInfoFixture {

    private val INITIAL_PRIORITY = AppUpdateChecker.Priority.LOW
    private const val INITIAL_FORCE = false

    fun new(
        priority: AppUpdateChecker.Priority = INITIAL_PRIORITY,
        force: Boolean = INITIAL_FORCE,
        appUpdateInfo: AppUpdateInfo?
    ) = UpdateInfo(
        priority,
        force,
        appUpdateInfo
    )
}
