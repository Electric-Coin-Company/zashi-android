package co.electriccoin.zcash.preference.model.entry

import co.electriccoin.zcash.preference.api.PreferenceProvider
import java.time.Instant

class TimestampPreferenceDefault(override val key: PreferenceKey): PreferenceDefault<Instant?> {
    override suspend fun getValue(preferenceProvider: PreferenceProvider) =
        preferenceProvider.getLong(key)?.let { Instant.ofEpochMilli(it) }

    override suspend fun putValue(
        preferenceProvider: PreferenceProvider,
        newValue: Instant?
    ) = preferenceProvider.putLong(key, newValue?.toEpochMilli())
}