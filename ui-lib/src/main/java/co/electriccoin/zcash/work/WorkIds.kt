package co.electriccoin.zcash.work

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager

object WorkIds {
    const val WORK_ID_BACKGROUND_SYNC = "co.electriccoin.zcash.background_sync"

    fun enableBackgroundSynchronization(context: Context) {
        val workManager = WorkManager.getInstance(context)

        workManager.enqueueUniquePeriodicWork(
            WORK_ID_BACKGROUND_SYNC,
            ExistingPeriodicWorkPolicy.REPLACE,
            SyncWorker.newWorkRequest()
        )
    }

    fun disableBackgroundSynchronization(context: Context) {
        val workManager = WorkManager.getInstance(context)

        workManager.cancelUniqueWork(WORK_ID_BACKGROUND_SYNC)
    }
}
