package co.electriccoin.zcash.ui.common.provider

import cash.z.ecc.android.sdk.SdkSynchronizer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.WalletCoordinator
import co.electriccoin.zcash.spackle.Twig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

interface SynchronizerProvider {
    val synchronizer: StateFlow<Synchronizer?>

    suspend fun getSynchronizer(): Synchronizer

    suspend fun getSdkSynchronizer() = getSynchronizer() as SdkSynchronizer
}

class SynchronizerProviderImpl(
    walletCoordinator: WalletCoordinator
) : SynchronizerProvider {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @OptIn(ExperimentalCoroutinesApi::class)
    override val synchronizer: StateFlow<Synchronizer?> =
        walletCoordinator.synchronizer
            .flatMapLatest { synchronizer ->
                flow {
                    if (synchronizer == null) {
                        emit(null)
                    } else {
                        emit(null)
                        // Waiting for the synchronizer to be ready, i.e. its database is set/migrated
                        synchronizer.status.first {
                            Twig.info { "Current Synchronizer.Status: $it" }
                            it != Synchronizer.Status.INITIALIZING
                        }
                        emit(synchronizer)
                    }
                }
            }.flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = null
            )

    override suspend fun getSynchronizer(): Synchronizer =
        withContext(Dispatchers.IO) {
            synchronizer
                .filterNotNull()
                .first()
        }
}
