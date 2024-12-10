package co.electriccoin.zcash.ui.common.provider

import cash.z.ecc.android.sdk.SdkSynchronizer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.WalletCoordinator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlin.time.Duration

interface SynchronizerProvider {
    val synchronizer: StateFlow<Synchronizer?>

    suspend fun getSynchronizer(): Synchronizer

    suspend fun getSdkSynchronizer() = getSynchronizer() as SdkSynchronizer
}

class SynchronizerProviderImpl(walletCoordinator: WalletCoordinator) : SynchronizerProvider {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @OptIn(ExperimentalCoroutinesApi::class)
    override val synchronizer: StateFlow<Synchronizer?> =
        walletCoordinator.synchronizer
            .mapLatest { synchronizer ->
                synchronizer?.networkHeight?.filterNotNull()?.first()
                synchronizer
            }.stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(Duration.ZERO, Duration.ZERO),
                initialValue = null
            ) // TODO keystone

    override suspend fun getSynchronizer(): Synchronizer =
        withContext(Dispatchers.IO) {
            synchronizer
                .filterNotNull()
                .first()
        }
}
