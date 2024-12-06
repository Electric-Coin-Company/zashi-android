package co.electriccoin.zcash.ui.common.provider

import cash.z.ecc.android.sdk.SdkSynchronizer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.WalletCoordinator
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

interface SynchronizerProvider {
    val synchronizer: StateFlow<Synchronizer?>

    suspend fun getSynchronizer(): Synchronizer

    suspend fun getSdkSynchronizer() = getSynchronizer() as SdkSynchronizer
}

class SynchronizerProviderImpl(walletCoordinator: WalletCoordinator) : SynchronizerProvider {

    override val synchronizer: StateFlow<Synchronizer?> = walletCoordinator.synchronizer

    override suspend fun getSynchronizer(): Synchronizer = synchronizer
        .filterNotNull()
        .first()
}