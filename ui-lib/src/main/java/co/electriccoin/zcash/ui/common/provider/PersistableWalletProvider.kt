package co.electriccoin.zcash.ui.common.provider

import cash.z.ecc.android.sdk.model.PersistableWallet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration

interface PersistableWalletProvider {
    val persistableWallet: Flow<PersistableWallet?>

    suspend fun store(persistableWallet: PersistableWallet)

    suspend fun getPersistableWallet(): PersistableWallet?

    suspend fun requirePersistableWallet(): PersistableWallet
}

class PersistableWalletProviderImpl(
    private val persistableWalletStorageProvider: PersistableWalletStorageProvider,
) : PersistableWalletProvider {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val persistableWallet: Flow<PersistableWallet?> =
        persistableWalletStorageProvider
            .observe()
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(Duration.ZERO, Duration.ZERO),
                initialValue = null
            )

    override suspend fun store(persistableWallet: PersistableWallet) {
        persistableWalletStorageProvider.store(persistableWallet)
    }

    override suspend fun getPersistableWallet() = persistableWalletStorageProvider.get()

    override suspend fun requirePersistableWallet() = checkNotNull(persistableWalletStorageProvider.get())
}
