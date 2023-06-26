package co.electriccoin.zcash.work

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import co.electriccoin.zcash.ui.common.WORKER_TAG_SYNC_NOTIFICATION
import co.electriccoin.zcash.ui.screen.syncnotification.viewmodel.SyncNotificationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object WorkIds {
    const val WORK_ID_BACKGROUND_SYNC = "co.electriccoin.zcash.background_sync"
    const val SYNC_NOTIFICATION_CHANNEL_ID = "syncNotificationChannelId"
    const val SYNC_NOTIFICATION_CHANNEL_NAME = "Periodic Sync"
    const val SYNC_NOTIFICATION_CHANNEL_DESC = "To keep app sync"

    fun enableBackgroundSynchronization(context: Context) {
        val workManager = WorkManager.getInstance(context)

        workManager.enqueueUniquePeriodicWork(
            WORK_ID_BACKGROUND_SYNC,
            ExistingPeriodicWorkPolicy.KEEP,
            SyncWorker.newWorkRequest()
        )
    }

    fun disableBackgroundSynchronization(context: Context) {
        val workManager = WorkManager.getInstance(context)

        workManager.cancelUniqueWork(WORK_ID_BACKGROUND_SYNC)
    }

    fun cancelSyncAppNotificationAndReRegister(syncIntervalOption: SyncNotificationViewModel.SyncIntervalOption, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            cancelSyncNotificationWork(context)

            if (syncIntervalOption != SyncNotificationViewModel.SyncIntervalOption.OFF) {
                startPeriodicSyncNotificationWork(
                    context,
                    syncIntervalOption.interval.toLong()
                )
            }
        }
    }

    private fun cancelSyncNotificationWork(appContext: Context) {
        WorkManager.getInstance(appContext).cancelAllWorkByTag(WORKER_TAG_SYNC_NOTIFICATION)
    }

    private fun startPeriodicSyncNotificationWork(appContext: Context, frequencyInDays: Long) {
        WorkManager.getInstance(appContext).enqueueUniquePeriodicWork(
            WORKER_TAG_SYNC_NOTIFICATION,
            ExistingPeriodicWorkPolicy.UPDATE,
            SyncNotificationWorker.newWorkRequest(frequencyInDays)
        )
    }
}
