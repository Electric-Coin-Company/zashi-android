package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.PersistableWallet
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider
import kotlinx.coroutines.flow.Flow

class GetPersistableWalletUseCase(
    private val persistableWalletProvider: PersistableWalletProvider,
) {
    fun observe(): Flow<PersistableWallet?> = persistableWalletProvider.persistableWallet
}
