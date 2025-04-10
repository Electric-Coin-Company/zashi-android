package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.PreferenceHolder
import co.electriccoin.zcash.preference.api.PreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import co.electriccoin.zcash.preference.model.entry.TimestampPreferenceDefault
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import java.time.Instant

interface TimestampStorageProvider {
    suspend fun get(): Instant?

    suspend fun store(timestamp: Instant)

    fun observe(): Flow<Instant?>

    suspend fun clear()
}

abstract class BaseTimestampStorageProvider(key: PreferenceKey) : TimestampStorageProvider {
    protected abstract val preferenceHolder: PreferenceHolder

    private val default = TimestampPreferenceDefault(key)

    private suspend fun getPreferenceProvider(): PreferenceProvider = preferenceHolder()

    override suspend fun get(): Instant? = default.getValue(getPreferenceProvider())

    override suspend fun store(timestamp: Instant) = default.putValue(getPreferenceProvider(), timestamp)

    override fun observe(): Flow<Instant?> = flow { emitAll(default.observe(getPreferenceProvider())) }

    override suspend fun clear() = default.putValue(getPreferenceProvider(), null)
}