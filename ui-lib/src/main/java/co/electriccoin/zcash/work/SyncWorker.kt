package co.electriccoin.zcash.work

import android.content.Context
import androidx.annotation.Keep
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import cash.z.ecc.android.sdk.Synchronizer
import co.electriccoin.zcash.global.WalletCoordinator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.takeWhile
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

@Keep
class SyncWorker(context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun doWork(): Result {
        // Enhancements to this implementation would be:
        // - Quit early if the synchronizer is null after a timeout period
        // - Return better status information

        WalletCoordinator.getInstance(applicationContext).synchronizer
            .flatMapLatest {
                it?.status?.combine(it.progress) { status, progress ->
                    StatusAndProgress(status, progress)
                } ?: emptyFlow()
            }
            .takeWhile {
                it.status != Synchronizer.Status.DISCONNECTED && it.progress < ONE_HUNDRED_PERCENT
            }
        // .collect()

        return Result.success()
    }

    companion object {
        private const val ONE_HUNDRED_PERCENT = 100

        /*
         * There may be better periods; we have not optimized for this yet.
         */
        private val DEFAULT_SYNC_PERIOD = 24.hours

        fun newWorkRequest(): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiresStorageNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            return PeriodicWorkRequestBuilder<SyncWorker>(DEFAULT_SYNC_PERIOD.toJavaDuration())
                .setConstraints(constraints)
                .build()
        }
    }
}

private data class StatusAndProgress(val status: Synchronizer.Status, val progress: Int)
