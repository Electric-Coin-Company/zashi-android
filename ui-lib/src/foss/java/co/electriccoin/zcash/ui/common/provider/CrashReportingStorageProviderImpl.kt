package co.electriccoin.zcash.ui.common.provider

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class CrashReportingStorageProviderImpl: CrashReportingStorageProvider {
    override suspend fun get(): Boolean = false

    override suspend fun store(amount: Boolean) {
        // do nothing
    }

    override fun observe(): Flow<Boolean?> = flowOf(false)

    override suspend fun clear() {
        // do nothing
    }
}