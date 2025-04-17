package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.PreferenceHolder
import co.electriccoin.zcash.preference.api.PreferenceProvider
import co.electriccoin.zcash.preference.model.entry.IntegerPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

interface IntStorageProvider {
    suspend fun get(): Int

    suspend fun store(amount: Int)

    fun observe(): Flow<Int>

    suspend fun clear()
}

abstract class BaseIntStorageProvider(key: PreferenceKey) : IntStorageProvider {
    protected abstract val preferenceHolder: PreferenceHolder

    private val default = IntegerPreferenceDefault(key = key, defaultValue = DEFAULT)

    private suspend fun getPreferenceProvider(): PreferenceProvider = preferenceHolder()

    override suspend fun get(): Int = default.getValue(getPreferenceProvider())

    override suspend fun store(amount: Int) = default.putValue(getPreferenceProvider(), amount)

    override fun observe(): Flow<Int> = flow { emitAll(default.observe(getPreferenceProvider())) }

    override suspend fun clear() = default.putValue(getPreferenceProvider(), DEFAULT)
}

private const val DEFAULT = 0
