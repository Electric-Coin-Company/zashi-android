package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.PersistableWallet
import co.electriccoin.zcash.ui.common.provider.PersistableWalletStorageProvider
import kotlinx.coroutines.flow.Flow

class ObservePersistableWalletUseCase(
    private val persistableWalletStorageProvider: PersistableWalletStorageProvider
) {
    operator fun invoke(): Flow<PersistableWallet?> = persistableWalletStorageProvider.observe()
}
