package co.electriccoin.zcash.ui

import androidx.activity.ComponentActivity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

object AppUpdateCheckerImpl : AppUpdateChecker {

    @Suppress("MagicNumber")
    override val stanelessDays = 3

    /**
     * Bla bla
     *
     * For setting up the PRIORITY of an update @see https://developer.android.com/guide/playcore/in-app-updates/kotlin-java#update-priority.
     *
     * @param
     * @param
     *
     * @return
     */
    override fun checkForUpdateAvailability(
        appUpdateManager: AppUpdateManager,
        stalenessDays: Int,
        onUpdateAvailable: (appUpdateInfo: AppUpdateInfo) -> Unit
    ) {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // we force user to update immediately in case of high priority
                if (getPriority(appUpdateInfo.updatePriority()) == AppUpdateChecker.Priority.HIGH) {
                    onUpdateAvailable(appUpdateInfo)
                    return@addOnSuccessListener
                }

                // or in case of staleness days passed
                if ((appUpdateInfo.clientVersionStalenessDays() ?: -1) >= stalenessDays)
                    onUpdateAvailable(appUpdateInfo)
            }
        }
    }

    override fun startUpdate(
        activity: ComponentActivity,
        appUpdateManager: AppUpdateManager,
        appUpdateInfo: AppUpdateInfo,
        requestCode: Int
    ) {
        appUpdateManager.startUpdateFlowForResult(
            // Pass the intent that is returned by 'getAppUpdateInfo()'.
            appUpdateInfo,
            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
            AppUpdateType.IMMEDIATE,
            // The current activity making the update request.
            activity,
            // Include a request code to later monitor this update request.
            requestCode
        )
    }
}
