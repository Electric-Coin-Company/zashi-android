package co.electriccoin.zcash.ui.common.provider

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

interface PersistableWalletTorProvider {
    fun observe(): Flow<TorState?>

    suspend fun get(): TorState
}

class PersistableWalletTorProviderImpl(
    private val synchronizerProvider: SynchronizerProvider,
    private val isTorEnabledStorageProvider: IsTorEnabledStorageProvider
) : PersistableWalletTorProvider {

    override fun observe(): Flow<TorState?> = combine(
        synchronizerProvider.synchronizer,
        isTorEnabledStorageProvider.observe()
    ) { synchronizer, isTorEnabled ->
        if (synchronizer == null) return@combine null

        when (isTorEnabled) {
            true -> TorState.EXPLICITLY_ENABLED
            false -> TorState.EXPLICITLY_DISABLED
            null -> TorState.IMPLICITLY_DISABLED
        }
    }

    override suspend fun get(): TorState {
        val isTorEnabled = isTorEnabledStorageProvider.get()

        return when (isTorEnabled) {
            true -> TorState.EXPLICITLY_ENABLED
            false -> TorState.EXPLICITLY_DISABLED
            null -> TorState.IMPLICITLY_DISABLED
        }
    }
}

enum class TorState {
    EXPLICITLY_ENABLED,
    EXPLICITLY_DISABLED,
    IMPLICITLY_DISABLED
}