@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available.view

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import co.electriccoin.zcash.ui.screen.update_available.AppUpdateChecker
import co.electriccoin.zcash.ui.screen.update_available.fixture.UpdateInfoFixture
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateState
import co.electriccoin.zcash.util.VersionCodeCompat
import co.electriccoin.zcash.util.myPackageInfo
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

class AppUpdateCheckerMock private constructor() : AppUpdateChecker {

    companion object {
        fun new() = AppUpdateCheckerMock()

        // used mostly for tests
        val resultUpdateInfo = UpdateInfoFixture.new(
            appUpdateInfo = null,
            state = UpdateState.Prepared,
            priority = AppUpdateChecker.Priority.HIGH,
            force = true
        )
    }

    @Suppress("MagicNumber")
    override val stalenessDays = 3

    override fun checkForUpdateAvailability(
        context: Context,
        stalenessDays: Int
    ): Flow<UpdateInfo> = callbackFlow {
        val fakeAppUpdateManager = FakeAppUpdateManager(context.applicationContext).also {
            it.setClientVersionStalenessDays(stalenessDays)
            it.setUpdateAvailable(
                VersionCodeCompat.getVersionCode(context.myPackageInfo(0)).toInt(),
                AppUpdateType.IMMEDIATE
            )
            it.setUpdatePriority(resultUpdateInfo.priority.priorityUpperBorder())
        }

        val appUpdateInfoTask = fakeAppUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnCompleteListener { infoTask ->
            emitResult(this, infoTask.result)
        }

        awaitClose {
            // No resources to release
        }
    }

    private fun emitResult(producerScope: ProducerScope<UpdateInfo>, info: AppUpdateInfo) {
        producerScope.trySend(
            UpdateInfoFixture.new(
                getPriority(info.updatePriority()),
                isHighPriority(info.updatePriority()),
                info,
                resultUpdateInfo.state
            )
        )
    }

    override fun startUpdate(
        activity: ComponentActivity,
        appUpdateInfo: AppUpdateInfo?
    ): Flow<Int> = flow {
        emit(Activity.RESULT_OK)
    }
}
