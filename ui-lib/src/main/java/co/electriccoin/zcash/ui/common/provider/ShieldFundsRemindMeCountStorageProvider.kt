package co.electriccoin.zcash.ui.common.provider

import cash.z.ecc.android.sdk.model.AccountUuid
import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.IntegerPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import kotlinx.coroutines.flow.Flow

interface ShieldFundsRemindMeCountStorageProvider {
    suspend fun get(forAccount: AccountUuid): Int

    suspend fun store(forAccount: AccountUuid, amount: Int)

    suspend fun observe(forAccount: AccountUuid): Flow<Int>
}

class ShieldFundsRemindMeCountStorageProviderImpl(
    private val encryptedPreferenceProvider: EncryptedPreferenceProvider
) : ShieldFundsRemindMeCountStorageProvider {
    @OptIn(ExperimentalStdlibApi::class)
    private fun getDefault(forAccount: AccountUuid): IntegerPreferenceDefault {
        val key = PreferenceKey("shield_funds_remind_me_count_${forAccount.value.toHexString()}")
        return IntegerPreferenceDefault(key = key, defaultValue = 0)
    }

    override suspend fun get(forAccount: AccountUuid): Int =
        getDefault(forAccount)
            .getValue(encryptedPreferenceProvider())

    override suspend fun store(forAccount: AccountUuid, amount: Int) {
        getDefault(forAccount).putValue(encryptedPreferenceProvider(), amount)
    }

    override suspend fun observe(forAccount: AccountUuid): Flow<Int> =
        getDefault(forAccount)
            .observe(encryptedPreferenceProvider())
}
