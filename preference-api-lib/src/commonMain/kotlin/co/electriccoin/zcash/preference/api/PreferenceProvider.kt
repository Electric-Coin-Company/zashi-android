package co.electriccoin.zcash.preference.api

import co.electriccoin.zcash.preference.model.entry.Key
import kotlinx.coroutines.flow.Flow

interface PreferenceProvider {

    suspend fun hasKey(key: Key): Boolean

    suspend fun putString(key: Key, value: String)

    suspend fun getString(key: Key): String?

    suspend fun observe(key: Key): Flow<String?>
}
