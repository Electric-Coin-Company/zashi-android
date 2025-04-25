package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.PreferenceHolder
import co.electriccoin.zcash.preference.api.PreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceDefault
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

interface StorageProvider<T : Any> {
    suspend fun get(): T

    suspend fun store(amount: T)

    fun observe(): Flow<T>

    suspend fun clear()
}

interface NullableStorageProvider<T : Any> {
    suspend fun get(): T?

    suspend fun store(amount: T)

    fun observe(): Flow<T?>

    suspend fun clear()
}

abstract class BaseStorageProvider<T : Any> : StorageProvider<T> {
    protected abstract val preferenceHolder: PreferenceHolder

    protected abstract val default: PreferenceDefault<T>

    final override suspend fun get(): T = default.getValue(getPreferenceProvider())

    final override suspend fun store(amount: T) = default.putValue(getPreferenceProvider(), amount)

    final override fun observe(): Flow<T> = flow { emitAll(default.observe(getPreferenceProvider())) }

    final override suspend fun clear() = default.clear(getPreferenceProvider())

    private suspend fun getPreferenceProvider(): PreferenceProvider = preferenceHolder()
}

abstract class BaseNullableStorageProvider<T : Any> : NullableStorageProvider<T> {
    protected abstract val preferenceHolder: PreferenceHolder

    protected abstract val default: PreferenceDefault<T?>

    final override suspend fun get(): T? = default.getValue(getPreferenceProvider())

    final override suspend fun store(amount: T) = default.putValue(getPreferenceProvider(), amount)

    final override fun observe(): Flow<T?> = flow { emitAll(default.observe(getPreferenceProvider())) }

    final override suspend fun clear() = default.clear(getPreferenceProvider())

    private suspend fun getPreferenceProvider(): PreferenceProvider = preferenceHolder()
}
