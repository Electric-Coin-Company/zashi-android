package co.electriccoin.zcash.preference.test

import co.electriccoin.zcash.preference.api.PreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * @param mutableMapFactory Emits a new mutable map.  Thread safety depends on the factory implementation.
 */
class MockPreferenceProvider(
    mutableMapFactory: () -> MutableMap<String, String?> = { mutableMapOf() }
) : PreferenceProvider {
    private val map = mutableMapFactory()

    override suspend fun getString(key: PreferenceKey) = map[key.key]

    override suspend fun getStringSet(key: PreferenceKey): Set<String>? {
        TODO("Not yet implemented")
    }

    // For the mock implementation, does not support observability of changes
    override fun observe(key: PreferenceKey): Flow<String?> = flow { emit(getString(key)) }

    override suspend fun remove(key: PreferenceKey) {
        map.remove(key.key)
    }

    override suspend fun clearPreferences(): Boolean {
        map.clear()
        return true
    }

    override suspend fun hasKey(key: PreferenceKey) = map.containsKey(key.key)

    override suspend fun putString(
        key: PreferenceKey,
        value: String?
    ) {
        map[key.key] = value
    }

    override suspend fun putStringSet(
        key: PreferenceKey,
        value: Set<String>?
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun putLong(
        key: PreferenceKey,
        value: Long?
    ) {
        map[key.key] = value?.toString()
    }

    override suspend fun getLong(key: PreferenceKey): Long? = map[key.key]?.toLongOrNull()
}
