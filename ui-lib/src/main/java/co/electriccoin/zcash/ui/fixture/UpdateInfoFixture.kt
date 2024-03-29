package co.electriccoin.zcash.ui.fixture

import co.electriccoin.zcash.ui.screen.update.AppUpdateChecker
import co.electriccoin.zcash.ui.screen.update.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update.model.UpdateState
import com.google.android.play.core.appupdate.AppUpdateInfo

object UpdateInfoFixture {
    val INITIAL_PRIORITY = AppUpdateChecker.Priority.LOW
    val INITIAL_STATE = UpdateState.Prepared
    const val INITIAL_FORCE = false

    fun new(
        priority: AppUpdateChecker.Priority = INITIAL_PRIORITY,
        force: Boolean = INITIAL_FORCE,
        appUpdateInfo: AppUpdateInfo? = null,
        state: UpdateState = INITIAL_STATE
    ) = UpdateInfo(
        priority,
        force,
        appUpdateInfo,
        state
    )
}
