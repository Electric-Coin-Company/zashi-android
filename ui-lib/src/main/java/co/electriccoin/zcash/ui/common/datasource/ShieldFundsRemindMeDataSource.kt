package co.electriccoin.zcash.ui.common.datasource

import cash.z.ecc.android.sdk.model.AccountUuid
import co.electriccoin.zcash.ui.common.provider.ShieldFundsRemindMeCountStorageProvider
import co.electriccoin.zcash.ui.common.provider.ShieldFundsRemindMeTimestampStorageProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

interface ShieldFundsRemindMeDataSource {
    suspend fun observe(forAccount: AccountUuid): Flow<ShieldFundsAvailability>
}

class ShieldFundsRemindMeDataSourceImpl(
    private val shieldFundsRemindMeCountStorageProvider: ShieldFundsRemindMeCountStorageProvider,
    private val shieldFundsRemindMeTimestampStorageProvider: ShieldFundsRemindMeTimestampStorageProvider
): ShieldFundsRemindMeDataSource {
    override suspend fun observe(forAccount: AccountUuid): Flow<ShieldFundsAvailability> = combine(
        shieldFundsRemindMeCountStorageProvider.observe(forAccount),
        shieldFundsRemindMeTimestampStorageProvider.observe(forAccount)
    ) { count, timestamp ->
        when {
            timestamp == null -> ShieldFundsAvailability.Available(1.days)
            count == 1 -> ShieldFundsAvailability.Available(2.days)
            count == 2 -> ShieldFundsAvailability.Available(3.days)
            else -> ShieldFundsAvailability.Unavailable
        }
    }
}

sealed interface ShieldFundsAvailability {
    data class Available(val nextLockoutDuration: Duration) : ShieldFundsAvailability
    data object Unavailable : ShieldFundsAvailability
}
