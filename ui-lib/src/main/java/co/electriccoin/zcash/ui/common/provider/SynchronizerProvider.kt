package co.electriccoin.zcash.ui.common.provider

import cash.z.ecc.android.sdk.SdkSynchronizer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.WalletCoordinator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

interface SynchronizerProvider {
    val synchronizer: StateFlow<Synchronizer?>

    /**
     * Get synchronizer and wait for it to be ready.
     */
    suspend fun getSynchronizer(): Synchronizer

    /**
     * Get synchronizer and wait for it to be ready.
     */
    suspend fun getSdkSynchronizer() = getSynchronizer() as SdkSynchronizer
}

class SynchronizerProviderImpl(
    walletCoordinator: WalletCoordinator
) : SynchronizerProvider {
    override val synchronizer: StateFlow<Synchronizer?> = walletCoordinator.synchronizer

    override suspend fun getSynchronizer(): Synchronizer =
        withContext(Dispatchers.IO) {
            synchronizer
                .filterNotNull()
                .first()
        }
}
