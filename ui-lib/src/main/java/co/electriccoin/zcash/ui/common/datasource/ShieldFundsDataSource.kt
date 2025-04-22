package co.electriccoin.zcash.ui.common.datasource

import androidx.annotation.StringRes
import cash.z.ecc.android.sdk.model.AccountUuid
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.provider.ShieldFundsRemindMeCountStorageProvider
import co.electriccoin.zcash.ui.common.provider.ShieldFundsRemindMeTimestampStorageProvider
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

interface ShieldFundsDataSource {
    suspend fun observe(forAccount: AccountUuid): Flow<ShieldFundsAvailability>

    suspend fun remindMeLater(forAccount: AccountUuid)
}

class ShieldFundsDataSourceImpl(
    private val shieldFundsRemindMeCountStorageProvider: ShieldFundsRemindMeCountStorageProvider,
    private val shieldFundsRemindMeTimestampStorageProvider: ShieldFundsRemindMeTimestampStorageProvider
) : ShieldFundsDataSource {
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun observe(forAccount: AccountUuid): Flow<ShieldFundsAvailability> =
        combine(
            shieldFundsRemindMeCountStorageProvider.observe(forAccount),
            shieldFundsRemindMeTimestampStorageProvider.observe(forAccount)
        ) { count, timestamp ->
            count to timestamp
        }.flatMapLatest { (count, timestamp) ->
            when {
                timestamp == null -> flowOf(ShieldFundsAvailability.Available(ShieldFundsLockoutDuration.TWO_DAYS))
                count == 1 ->
                    calculateNext(
                        lastTimestamp = timestamp,
                        lastLockoutDuration = ShieldFundsLockoutDuration.TWO_DAYS,
                        nextLockoutDuration = ShieldFundsLockoutDuration.TWO_WEEKS
                    )

                else ->
                    calculateNext(
                        lastTimestamp = timestamp,
                        lastLockoutDuration =
                            if (count == 2) {
                                ShieldFundsLockoutDuration.TWO_WEEKS
                            } else {
                                ShieldFundsLockoutDuration.ONE_MONTH
                            },
                        nextLockoutDuration = ShieldFundsLockoutDuration.ONE_MONTH
                    )
            }
        }

    override suspend fun remindMeLater(forAccount: AccountUuid) {
        val count = shieldFundsRemindMeCountStorageProvider.get(forAccount)
        val timestamp = Instant.now()
        shieldFundsRemindMeCountStorageProvider.store(forAccount, count + 1)
        shieldFundsRemindMeTimestampStorageProvider.store(forAccount, timestamp)
    }

    private fun calculateNext(
        lastTimestamp: Instant,
        lastLockoutDuration: ShieldFundsLockoutDuration,
        nextLockoutDuration: ShieldFundsLockoutDuration
    ): Flow<ShieldFundsAvailability> {
        val nextAvailableTimestamp = lastTimestamp.plusMillis(lastLockoutDuration.duration.inWholeMilliseconds)
        val now = Instant.now()
        return if (nextAvailableTimestamp > now) {
            flow {
                val remaining = nextAvailableTimestamp.toEpochMilli() - now.toEpochMilli()
                emit(ShieldFundsAvailability.Unavailable)
                delay(remaining)
                emit(ShieldFundsAvailability.Available(nextLockoutDuration))
            }
        } else {
            flowOf(ShieldFundsAvailability.Available(nextLockoutDuration))
        }
    }
}

sealed interface ShieldFundsAvailability {
    data class Available(
        val lockoutDuration: ShieldFundsLockoutDuration
    ) : ShieldFundsAvailability

    data object Unavailable : ShieldFundsAvailability
}

enum class ShieldFundsLockoutDuration(
    val duration: Duration,
    @StringRes val res: Int
) {
    TWO_DAYS(2.days, R.string.general_remind_me_in_two_days),
    TWO_WEEKS(2.days, R.string.general_remind_me_in_two_weeks),
    ONE_MONTH(30.days, R.string.general_remind_me_in_two_months)
}
