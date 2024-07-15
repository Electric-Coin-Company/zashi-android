package cash.z.ecc.sdk.repository

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.WalletCoordinator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

interface WalletRepository {
    val synchronizer: StateFlow<Synchronizer?>
}

class WalletRepositoryImpl(
    walletCoordinator: WalletCoordinator,
) : WalletRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val synchronizer: StateFlow<Synchronizer?> =
        walletCoordinator.synchronizer.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}
