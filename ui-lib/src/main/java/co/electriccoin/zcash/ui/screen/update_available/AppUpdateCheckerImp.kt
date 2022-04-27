@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available

import android.content.Context
import androidx.activity.ComponentActivity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AppUpdateCheckerImp : AppUpdateChecker {

    companion object {
        fun new() = AppUpdateCheckerTest()
    }

    @Suppress("MagicNumber")
    override val stanelessDays = 3

    /**
     * TODO
     *
     * For setting up the PRIORITY of an update @see https://developer.android.com/guide/playcore/in-app-updates/kotlin-java#update-priority.
     *
     * @param
     * @param
     *
     * @return
     */
    override fun checkForUpdateAvailability(
        context: Context,
        stalenessDays: Int
    ): Flow<AppUpdateInfo?> = callbackFlow {
        val appUpdateInfoTask = AppUpdateManagerFactory.create(context).appUpdateInfo

        appUpdateInfoTask.addOnCompleteListener { infoTask ->
            if (!infoTask.isSuccessful)
                return@addOnCompleteListener

            val appUpdateInfo = infoTask.result
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // we force user to update immediately in case of high priority
                // or in case of staleness days passed
                if (getPriority(appUpdateInfo.updatePriority()) == AppUpdateChecker.Priority.HIGH ||
                    (appUpdateInfo.clientVersionStalenessDays() ?: -1) >= stalenessDays
                ) {
                    trySend(appUpdateInfo)
                }
            }
        }
        awaitClose { cancel() }
    }

    /**
     * TODO
     *
     * The immediate update can result in these values:
     * Activity.RESULT_OK: The user accepted and the update succeeded (which, in practice, your app
     * never should never receive because it already updated).
     * Activity.RESULT_CANCELED: The user denied or canceled the update.
     * ActivityResult.RESULT_IN_APP_UPDATE_FAILED: The flow failed either during the user confirmation,
     * the download, or the installation.
     *
     * @param
     * @param
     *
     * @return
     */
    override fun startUpdate(
        activity: ComponentActivity,
        appUpdateInfo: AppUpdateInfo?,
        onUpdateResult: (resultCode: Int) -> Unit
    ) {
        val appUpdateResultTask = AppUpdateManagerFactory.create(activity).startUpdateFlow(
            appUpdateInfo!!,
            activity,
            AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE)
        )

        appUpdateResultTask.addOnCompleteListener { resultTask ->
            if (!resultTask.isSuccessful)
                return@addOnCompleteListener

            val resultCode = resultTask.result
            onUpdateResult(resultCode)
        }
    }
}
