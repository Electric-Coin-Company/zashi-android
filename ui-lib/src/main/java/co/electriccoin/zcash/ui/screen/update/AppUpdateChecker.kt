package co.electriccoin.zcash.ui.screen.update

import android.content.Context
import androidx.activity.ComponentActivity
import co.electriccoin.zcash.ui.screen.update.model.UpdateInfo
import com.google.android.play.core.appupdate.AppUpdateInfo
import kotlinx.coroutines.flow.Flow

interface AppUpdateChecker {

    val stalenessDays: Int

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
        return getPriority(inAppUpdatePriority) == Priority.HIGH
    }

    fun newCheckForUpdateAvailabilityFlow(
        context: Context,
    ): Flow<UpdateInfo>

    fun newStartUpdateFlow(
        activity: ComponentActivity,
        appUpdateInfo: AppUpdateInfo
    ): Flow<Int>
}
