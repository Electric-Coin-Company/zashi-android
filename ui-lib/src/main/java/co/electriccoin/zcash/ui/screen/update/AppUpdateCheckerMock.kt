package co.electriccoin.zcash.ui.screen.update

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import co.electriccoin.zcash.spackle.getPackageInfoCompat
import co.electriccoin.zcash.spackle.versionCodeCompat
import co.electriccoin.zcash.ui.fixture.UpdateInfoFixture
import co.electriccoin.zcash.ui.screen.update.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update.model.UpdateState
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration.Companion.milliseconds

class AppUpdateCheckerMock private constructor() : AppUpdateChecker {
    companion object {
        private const val DEFAULT_STALENESS_DAYS = 3

        fun new() = AppUpdateCheckerMock()

        // Used mostly for tests
        val resultUpdateInfo =
            UpdateInfoFixture.new(
                appUpdateInfo = null,
                state = UpdateState.Prepared,
                priority = AppUpdateChecker.Priority.LOW,
                force = false
            )
    }

    override val stalenessDays = DEFAULT_STALENESS_DAYS

    override fun newCheckForUpdateAvailabilityFlow(context: Context): Flow<UpdateInfo> =
        callbackFlow {
            val fakeAppUpdateManager =
                FakeAppUpdateManager(context.applicationContext).also {
                    it.setClientVersionStalenessDays(stalenessDays)
                    it.setUpdateAvailable(
                        context.packageManager.getPackageInfoCompat(context.packageName, 0L).versionCodeCompat.toInt(),
                        AppUpdateType.IMMEDIATE
                    )
                    it.setUpdatePriority(resultUpdateInfo.priority.priorityUpperBorder())
                }

            val appUpdateInfoTask = fakeAppUpdateManager.appUpdateInfo

            // To simulate a real-world situation
            delay(100.milliseconds)

            appUpdateInfoTask.addOnCompleteListener { infoTask ->
                emitResult(this, infoTask.result)
            }

            awaitClose {
                // No resources to release
            }
        }

    private fun emitResult(
        producerScope: ProducerScope<UpdateInfo>,
        info: AppUpdateInfo
    ) {
        producerScope.trySend(
            UpdateInfoFixture.new(
                getPriority(info.updatePriority()),
                isHighPriority(info.updatePriority()),
                info,
                resultUpdateInfo.state
            )
        )
    }

    override fun newStartUpdateFlow(
        activity: ComponentActivity,
        appUpdateInfo: AppUpdateInfo
    ): Flow<Int> =
        flow {
            // To simulate a real-world situation
            delay(2000.milliseconds)
            emit(Activity.RESULT_OK)
        }
}
