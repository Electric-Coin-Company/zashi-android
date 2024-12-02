package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.WalletCoordinator
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration

interface PersistableWalletProvider {
    val persistableWallet: Flow<PersistableWallet?>

    suspend fun getPersistableWallet(): PersistableWallet
}

class PersistableWalletProviderImpl(
    walletCoordinator: WalletCoordinator
) : PersistableWalletProvider {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val persistableWallet: Flow<PersistableWallet?> = walletCoordinator.persistableWallet
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT, Duration.ZERO),
            initialValue = null
        )

    override suspend fun getPersistableWallet(): PersistableWallet = persistableWallet
        .filterNotNull()
        .first()
}