package co.electriccoin.zcash.preference.api

import co.electriccoin.zcash.preference.model.entry.Key
import kotlinx.coroutines.flow.Flow

interface PreferenceProvider {

    suspend fun hasKey(key: Key): Boolean

    suspend fun putString(key: Key, value: String?)

    suspend fun getString(key: Key): String?

    /**
     * @return Flow to observe potential changes to the value associated with the key in the preferences.
     * Consumers of the flow will need to then query the value and determine whether it has changed.
     */
    fun observe(key: Key): Flow<Unit>
}
