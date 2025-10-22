package co.electriccoin.zcash.ui.common.provider

import androidx.compose.ui.graphics.TransformOrigin
import cash.z.ecc.android.sdk.CloseableSynchronizer
import cash.z.ecc.android.sdk.SdkSynchronizer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.WalletCoordinator
import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.AccountBalance
import cash.z.ecc.android.sdk.model.AccountUuid
import cash.z.ecc.android.sdk.model.PercentDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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

private class DelayedSynchronizer(val original: CloseableSynchronizer): Synchronizer by original {
    override val accountsFlow: Flow<List<Account>?>
        get() = TODO("Not yet implemented")
    override val progress: Flow<PercentDecimal>
        get() = TODO("Not yet implemented")
    override val walletBalances: StateFlow<Map<AccountUuid, AccountBalance>?>
        get() = TODO("Not yet implemented")
}
