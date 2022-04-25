package co.electriccoin.zcash.ui

import androidx.activity.ComponentActivity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager

interface AppUpdateChecker {

    // TODO javadocs

    val stanelessDays: Int

    enum class Priority {
        LOW {
            override fun priorityUpperBorder() = 1
            override fun belongs(actualPriority: Int) =
                actualPriority <= this.priorityUpperBorder()
        },
        MEDIUM {
            override fun priorityUpperBorder() = 3
            override fun belongs(actualPriority: Int) =
                actualPriority > LOW.priorityUpperBorder() && actualPriority <= this.priorityUpperBorder()
        },
        HIGH {
            override fun priorityUpperBorder() = 5
            override fun belongs(actualPriority: Int) =
                actualPriority > MEDIUM.priorityUpperBorder() && actualPriority <= this.priorityUpperBorder()
        };

        abstract fun priorityUpperBorder(): Int
        abstract fun belongs(actualPriority: Int): Boolean
    }

    fun getPriority(inAppUpdatePriority: Int): Priority {
        return when {
            Priority.LOW.belongs(inAppUpdatePriority) -> Priority.LOW
            Priority.MEDIUM.belongs(inAppUpdatePriority) -> Priority.MEDIUM
            Priority.HIGH.belongs(inAppUpdatePriority) -> Priority.HIGH
            else -> Priority.LOW
        }
    }

    fun isHighPriority(inAppUpdatePriority: Int): Boolean {
        return Priority.HIGH.belongs(inAppUpdatePriority)
    }

    fun checkForUpdateAvailability(
        appUpdateManager: AppUpdateManager,
        stalenessDays: Int,
        onUpdateAvailable: (appUpdateInfo: AppUpdateInfo) -> Unit
    )

    fun startUpdate(
        activity: ComponentActivity,
        appUpdateManager: AppUpdateManager,
        appUpdateInfo: AppUpdateInfo,
        requestCode: Int
    )
}
