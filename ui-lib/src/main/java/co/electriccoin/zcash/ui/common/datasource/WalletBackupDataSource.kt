package co.electriccoin.zcash.ui.common.datasource

import androidx.annotation.StringRes
import cash.z.ecc.android.sdk.Synchronizer
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.provider.WalletBackupFlagStorageProvider
import co.electriccoin.zcash.ui.common.provider.WalletBackupRemindMeCountStorageProvider
import co.electriccoin.zcash.ui.common.provider.WalletBackupRemindMeTimestampStorageProvider
import co.electriccoin.zcash.ui.util.Quadruple
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

interface WalletBackupDataSource {
    fun observe(): Flow<WalletBackupData>

    suspend fun onUserSavedWalletBackup()

    suspend fun remindMeLater()
}

class WalletBackupDataSourceImpl(
    private val synchronizerProvider: SynchronizerProvider,
    private val walletBackupFlagStorageProvider: WalletBackupFlagStorageProvider,
    private val walletBackupRemindMeCountStorageProvider: WalletBackupRemindMeCountStorageProvider,
    private val walletBackupRemindMeTimestampStorageProvider: WalletBackupRemindMeTimestampStorageProvider
) : WalletBackupDataSource {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observe(): Flow<WalletBackupData> =
        combine(
            synchronizerProvider.synchronizer.flatMapLatest { it?.status ?: flowOf(null) },
            walletBackupFlagStorageProvider.observe(),
            walletBackupRemindMeCountStorageProvider.observe(),
            walletBackupRemindMeTimestampStorageProvider.observe()
        ) { status, isBackedUp, count, timestamp ->
            Quadruple(status, isBackedUp, count, timestamp)
        }.flatMapLatest { (status, isBackedUp, count, timestamp) ->
            when {
                status in listOf(null, Synchronizer.Status.INITIALIZING) -> flowOf(WalletBackupData.Unavailable)
                isBackedUp -> flowOf(WalletBackupData.Unavailable)
                timestamp == null -> flowOf(WalletBackupData.Available(WalletBackupLockoutDuration.TWO_DAYS))
                count == 1 ->
                    calculateNext(
                        lastTimestamp = timestamp,
                        lastLockoutDuration = WalletBackupLockoutDuration.TWO_DAYS,
                        nextLockoutDuration = WalletBackupLockoutDuration.TWO_WEEKS
                    )

                else ->
                    calculateNext(
                        lastTimestamp = timestamp,
                        lastLockoutDuration =
                            if (count == 2) {
                                WalletBackupLockoutDuration.TWO_WEEKS
                            } else {
                                WalletBackupLockoutDuration.ONE_MONTH
                            },
                        nextLockoutDuration = WalletBackupLockoutDuration.ONE_MONTH
                    )
            }
        }

    override suspend fun onUserSavedWalletBackup() {
        walletBackupFlagStorageProvider.store(true)
    }

    override suspend fun remindMeLater() {
        val count = walletBackupRemindMeCountStorageProvider.get()
        val timestamp = Instant.now()
        walletBackupRemindMeCountStorageProvider.store(count + 1)
        walletBackupRemindMeTimestampStorageProvider.store(timestamp)
    }

    private fun calculateNext(
        lastTimestamp: Instant,
        lastLockoutDuration: WalletBackupLockoutDuration,
        nextLockoutDuration: WalletBackupLockoutDuration
    ): Flow<WalletBackupData> {
        val nextAvailableTimestamp = lastTimestamp.plusMillis(lastLockoutDuration.duration.inWholeMilliseconds)
        val now = Instant.now()
        return if (nextAvailableTimestamp > now) {
            flow {
                val remaining = nextAvailableTimestamp.toEpochMilli() - now.toEpochMilli()
                emit(WalletBackupData.Unavailable)
                delay(remaining)
                emit(WalletBackupData.Available(nextLockoutDuration))
            }
        } else {
            flowOf(WalletBackupData.Available(nextLockoutDuration))
        }
    }
}

sealed interface WalletBackupData {
    data class Available(
        val lockoutDuration: WalletBackupLockoutDuration
    ) : WalletBackupData

    data object Unavailable : WalletBackupData
}

enum class WalletBackupLockoutDuration(
    val duration: Duration,
    @StringRes val res: Int
) {
    TWO_DAYS(2.days, R.string.general_remind_me_in_two_days),
    TWO_WEEKS(14.days, R.string.general_remind_me_in_two_weeks),
    ONE_MONTH(30.days, R.string.general_remind_me_in_two_months),
}
