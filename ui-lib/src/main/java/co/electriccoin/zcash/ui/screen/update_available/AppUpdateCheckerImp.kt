@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available

import android.content.Context
import androidx.activity.ComponentActivity
import co.electriccoin.zcash.ui.screen.update_available.fixture.UpdateInfoFixture
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateState
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AppUpdateCheckerImp : AppUpdateChecker {

    companion object {
        private const val DEFAULT_STALENESS_DAYS = 3

        fun new() = AppUpdateCheckerImp()
    }

    override val stalenessDays = DEFAULT_STALENESS_DAYS

    /**
     * This function checks available app update released on Google Play. It returns UpdateInfo object
     * encapsulated in Flow in case of high priority update or in case of staleness days passed.
     *
     * For setting up the PRIORITY of an update in Google Play
     * https://developer.android.com/guide/playcore/in-app-updates/kotlin-java#update-priority.
     *
     * @param context
     * @param stalenessDays can be used for postponing trigger of In-App Update UI
     *
     * @return UpdateInfo object encapsulated in Flow in case of conditions succeeded
     */
    override fun checkForUpdateAvailability(
        context: Context,
        stalenessDays: Int
    ): Flow<UpdateInfo> = callbackFlow {
        val appUpdateInfoTask = AppUpdateManagerFactory.create(context.applicationContext).appUpdateInfo

        appUpdateInfoTask.addOnCompleteListener { infoTask ->
            if (!infoTask.isSuccessful) {
                emitFailure(this)
                return@addOnCompleteListener
            }

            val appUpdateInfo = infoTask.result
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // we force user to update immediately in case of high priority
                // or in case of staleness days passed
                if (isHighPriority(appUpdateInfo.updatePriority()) ||
                    (appUpdateInfo.clientVersionStalenessDays() ?: -1) >= stalenessDays
                ) {
                    emitSuccess(this, infoTask.result, UpdateState.Prepared)
                } else {
                    emitSuccess(this, infoTask.result, UpdateState.Done)
                }
            }
        }
        awaitClose {
            // No resources to release
        }
    }

    private fun emitSuccess(producerScope: ProducerScope<UpdateInfo>, info: AppUpdateInfo, state: UpdateState) {
        producerScope.trySend(
            UpdateInfoFixture.new(
                getPriority(info.updatePriority()),
                isHighPriority(info.updatePriority()),
                info,
                state
            )
        )
    }

    private fun emitFailure(producerScope: ProducerScope<UpdateInfo>) {
        producerScope.trySend(
            UpdateInfoFixture.new(
                AppUpdateChecker.Priority.LOW,
                false,
                null,
                UpdateState.Failed
            )
        )
    }

    /**
     * This function is used for triggering in-app update with IMMEDIATE app update type.
     *
     * The immediate update can result with these values:
     * Activity.RESULT_OK: The user accepted and the update succeeded (which, in practice, your app
     * never should never receive because it already updated).
     * Activity.RESULT_CANCELED: The user denied or canceled the update.
     * ActivityResult.RESULT_IN_APP_UPDATE_FAILED: The flow failed either during the user confirmation,
     * the download, or the installation.
     *
     * @param activity
     * @param appUpdateInfo object is necessary for starting the update process,
     * for getting it see {@link #checkForUpdateAvailability()}
     * @param onUpdateResult callback for sending resultCode to the caller
     *
     */
    override fun startUpdate(
        activity: ComponentActivity,
        appUpdateInfo: AppUpdateInfo?
    ): Flow<Int> = callbackFlow {
        val appUpdateResultTask = AppUpdateManagerFactory.create(activity).startUpdateFlow(
            appUpdateInfo!!,
            activity,
            AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE)
        )

        appUpdateResultTask.addOnCompleteListener { resultTask ->
            if (resultTask.isSuccessful) {
                trySend(resultTask.result)
            } else {
                trySend(ActivityResult.RESULT_IN_APP_UPDATE_FAILED)
            }
        }

        awaitClose {
            // No resources to release
        }
    }
}
