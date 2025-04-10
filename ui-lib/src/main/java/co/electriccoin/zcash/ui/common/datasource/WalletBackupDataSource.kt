package co.electriccoin.zcash.ui.common.datasource

import co.electriccoin.zcash.ui.common.provider.WalletBackupFlagStorageProvider
import co.electriccoin.zcash.ui.common.provider.WalletBackupRemindMeCountStorageProvider
import co.electriccoin.zcash.ui.common.provider.WalletBackupRemindMeTimestampStorageProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface WalletBackupDataSource {
    fun observe(): Flow<WalletBackupAvailability>

    suspend fun onUserSavedWalletBackup()

    suspend fun remindMeLater()
}

class WalletBackupDataSourceImpl(
    private val walletBackupFlagStorageProvider: WalletBackupFlagStorageProvider,
    private val walletBackupRemindMeCountStorageProvider: WalletBackupRemindMeCountStorageProvider,
    private val walletBackupRemindMeTimestampStorageProvider: WalletBackupRemindMeTimestampStorageProvider
) : WalletBackupDataSource {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observe(): Flow<WalletBackupAvailability> = combine(
        walletBackupFlagStorageProvider.observe(),
        walletBackupRemindMeCountStorageProvider.observe(),
        walletBackupRemindMeTimestampStorageProvider.observe()
    ) { isBackedUp, count, timestamp ->
        Triple(isBackedUp, count, timestamp)
    }.flatMapLatest { (isBackedUp, count, timestamp) ->
        when {
            isBackedUp -> flowOf(WalletBackupAvailability.Unavailable)
            timestamp == null -> flowOf(WalletBackupAvailability.Available(WalletBackupLockoutDuration.ONE_DAY))
            count == 1 -> calculateNext(
                lastTimestamp = timestamp,
                lastLockoutDuration = WalletBackupLockoutDuration.ONE_DAY,
                nextLockoutDuration = WalletBackupLockoutDuration.TWO_DAYS
            )

            else -> calculateNext(
                lastTimestamp = timestamp,
                lastLockoutDuration = if (count == 2) {
                    WalletBackupLockoutDuration.TWO_DAYS
                } else {
                    WalletBackupLockoutDuration.THREE_DAYS
                },
                nextLockoutDuration = WalletBackupLockoutDuration.THREE_DAYS
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
    ): Flow<WalletBackupAvailability> {
        val nextAvailableTimestamp = lastTimestamp.plusMillis(lastLockoutDuration.duration.inWholeMilliseconds)
        val now = Instant.now()
        return if (nextAvailableTimestamp > now) {
            flow {
                val remaining = nextAvailableTimestamp.toEpochMilli() - now.toEpochMilli()
                emit(WalletBackupAvailability.Unavailable)
                delay(remaining)
                emit(WalletBackupAvailability.Available(nextLockoutDuration))
            }
        } else {
            flowOf(WalletBackupAvailability.Available(nextLockoutDuration))
        }
    }
}

sealed interface WalletBackupAvailability {
    data class Available(val lockoutDuration: WalletBackupLockoutDuration) : WalletBackupAvailability
    data object Unavailable : WalletBackupAvailability
}

enum class WalletBackupLockoutDuration(val duration: Duration) {
    ONE_DAY(10.seconds),
    TWO_DAYS(20.seconds),
    THREE_DAYS(30.seconds)
}
