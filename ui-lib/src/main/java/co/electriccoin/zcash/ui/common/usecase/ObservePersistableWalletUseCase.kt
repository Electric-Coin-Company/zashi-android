package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.PersistableWallet
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider
import kotlinx.coroutines.flow.Flow

class ObservePersistableWalletUseCase(
    private val persistableWalletProvider: PersistableWalletProvider,
) {
    operator fun invoke(): Flow<PersistableWallet?> = persistableWalletProvider.persistableWallet
}
