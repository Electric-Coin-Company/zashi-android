package co.electriccoin.zcash.ui.common.datasource

import co.electriccoin.zcash.ui.common.provider.RestoreTimestampStorageProvider
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant

interface RestoreTimestampDataSource {
    suspend fun getOrCreate(): Instant
    suspend fun clear()
}

class RestoreTimestampDataSourceImpl(
    private val restoreTimestampStorageProvider: RestoreTimestampStorageProvider
) : RestoreTimestampDataSource {

    private val mutex = Mutex()

    override suspend fun getOrCreate(): Instant = mutex.withLock {
        val existing = restoreTimestampStorageProvider.get()

        return if (existing == null) {
            val now = Instant.now()
            restoreTimestampStorageProvider.store(now)
            now
        } else {
            existing
        }
    }

    override suspend fun clear() = mutex.withLock {
        restoreTimestampStorageProvider.clear()
    }
}
