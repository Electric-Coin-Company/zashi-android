package co.electriccoin.zcash.preference.api

import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import kotlinx.coroutines.flow.Flow

interface PreferenceProvider {
    suspend fun hasKey(key: PreferenceKey): Boolean

    suspend fun putString(
        key: PreferenceKey,
        value: String?
    )

    suspend fun putStringSet(
        key: PreferenceKey,
        value: Set<String>?
    )

    suspend fun putLong(
        key: PreferenceKey,
        value: Long?
    )

    suspend fun getLong(key: PreferenceKey): Long?

    suspend fun getString(key: PreferenceKey): String?

    suspend fun getStringSet(key: PreferenceKey): Set<String>?

    fun observe(key: PreferenceKey): Flow<String?>

    suspend fun remove(key: PreferenceKey)

    suspend fun clearPreferences(): Boolean
}
