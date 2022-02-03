package cash.z.ecc.work

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager

object WorkIds {
    const val WORK_ID_BACKGROUND_SYNC = "co.electriccoin.zcash.background_sync"

    /*
     * For now, sync is always enabled.  In the future, we can consider whether a preference
     * is a good idea.
     *
     * Also note that if we ever change the sync interval period, this code won't re-run on
     * existing installations unless we make changes to call this during app startup.
     */
    fun enableBackgroundSynchronization(context: Context) {
        val workManager = WorkManager.getInstance(context)

        workManager.enqueueUniquePeriodicWork(
            WORK_ID_BACKGROUND_SYNC,
            ExistingPeriodicWorkPolicy.REPLACE,
            SyncWorker.newWorkRequest()
        )
    }
}
