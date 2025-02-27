package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.api.PreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import java.time.Instant

interface RestoreTimestampStorageProvider {
    suspend fun get(): Instant?

    suspend fun store(key: Instant)

    suspend fun clear()
}

class RestoreTimestampStorageProviderImpl(
    private val encryptedPreferenceProvider: EncryptedPreferenceProvider
) : RestoreTimestampStorageProvider {
    private val default = RestoreTimestampPreferenceDefault()

    override suspend fun get(): Instant? {
        return default.getValue(encryptedPreferenceProvider())
    }

    override suspend fun store(key: Instant) {
        default.putValue(encryptedPreferenceProvider(), key)
    }

    override suspend fun clear() {
        default.putValue(encryptedPreferenceProvider(), null)
    }
}

private class RestoreTimestampPreferenceDefault : PreferenceDefault<Instant?> {
    override val key: PreferenceKey = PreferenceKey("restore_timestamp")

    override suspend fun getValue(preferenceProvider: PreferenceProvider) =
        preferenceProvider.getLong(key)?.let {
            Instant.ofEpochMilli(it)
        }

    override suspend fun putValue(
        preferenceProvider: PreferenceProvider,
        newValue: Instant?
    ) = preferenceProvider.putLong(key, newValue?.toEpochMilli())
}
