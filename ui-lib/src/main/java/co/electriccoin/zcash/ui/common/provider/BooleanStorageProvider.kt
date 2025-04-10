package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.PreferenceHolder
import co.electriccoin.zcash.preference.api.PreferenceProvider
import co.electriccoin.zcash.preference.model.entry.BooleanPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

interface BooleanStorageProvider {
    suspend fun get(): Boolean

    suspend fun store(flag: Boolean)

    fun observe(): Flow<Boolean>

    suspend fun clear()
}

abstract class BaseBooleanStorageProvider(key: PreferenceKey) : BooleanStorageProvider {
    protected abstract val preferenceHolder: PreferenceHolder

    private val default = BooleanPreferenceDefault(key = key, defaultValue = DEFAULT)

    private suspend fun getPreferenceProvider(): PreferenceProvider = preferenceHolder()

    override suspend fun get(): Boolean = default.getValue(getPreferenceProvider())

    override suspend fun store(flag: Boolean) = default.putValue(getPreferenceProvider(), flag)

    override fun observe(): Flow<Boolean> = flow { emitAll(default.observe(getPreferenceProvider())) }

    override suspend fun clear() = default.putValue(getPreferenceProvider(), DEFAULT)
}

private const val DEFAULT = false
