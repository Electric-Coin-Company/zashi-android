package co.electriccoin.zcash.preference.model.entry

import co.electriccoin.zcash.preference.api.PreferenceProvider

data class NullableSetPreferenceDefault(
    override val key: PreferenceKey,
) : PreferenceDefault<Set<String>?> {
    override suspend fun getValue(preferenceProvider: PreferenceProvider) =
        preferenceProvider.getStringSet(key)

    override suspend fun putValue(preferenceProvider: PreferenceProvider, newValue: Set<String>?) {
        preferenceProvider.putStringSet(key, newValue)
    }
}
