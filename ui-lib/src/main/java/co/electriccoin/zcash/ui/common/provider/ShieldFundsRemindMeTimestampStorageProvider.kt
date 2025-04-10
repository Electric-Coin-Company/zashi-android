package co.electriccoin.zcash.ui.common.provider

import cash.z.ecc.android.sdk.model.AccountUuid
import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.TimestampPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface ShieldFundsRemindMeTimestampStorageProvider {
    suspend fun get(forAccount: AccountUuid): Instant?

    suspend fun store(forAccount: AccountUuid, timestamp: Instant)

    suspend fun observe(forAccount: AccountUuid): Flow<Instant?>
}

class ShieldFundsRemindMeTimestampStorageProviderImpl(
    private val encryptedPreferenceProvider: EncryptedPreferenceProvider
) :  ShieldFundsRemindMeTimestampStorageProvider {
    @OptIn(ExperimentalStdlibApi::class)
    private fun getDefault(forAccount: AccountUuid): TimestampPreferenceDefault {
        val key = PreferenceKey("shield_funds_remind_me_timestamp_${forAccount.value.toHexString()}")
        return TimestampPreferenceDefault(key = key)
    }

    override suspend fun get(forAccount: AccountUuid): Instant? {
        return getDefault(forAccount).getValue(encryptedPreferenceProvider())
    }

    override suspend fun store(forAccount: AccountUuid, timestamp: Instant) {
        getDefault(forAccount).putValue(encryptedPreferenceProvider(), timestamp)
    }

    override suspend fun observe(forAccount: AccountUuid): Flow<Instant?> {
        return getDefault(forAccount).observe(encryptedPreferenceProvider())
    }
}
