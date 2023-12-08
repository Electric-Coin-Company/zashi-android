package co.electriccoin.zcash.preference.api

import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import kotlinx.coroutines.flow.Flow

interface PreferenceProvider {
    suspend fun hasKey(key: PreferenceKey): Boolean

    suspend fun putString(
        key: PreferenceKey,
        value: String?
    )

    suspend fun getString(key: PreferenceKey): String?

    fun observe(key: PreferenceKey): Flow<String?>
}
