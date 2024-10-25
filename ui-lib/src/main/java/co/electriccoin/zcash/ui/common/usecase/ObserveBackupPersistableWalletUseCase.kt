package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.PersistableWallet
import co.electriccoin.zcash.ui.common.repository.WalletRepository
import co.electriccoin.zcash.ui.common.viewmodel.SecretState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveBackupPersistableWalletUseCase(
    private val walletRepository: WalletRepository
) {
    operator fun invoke(): Flow<PersistableWallet?> = walletRepository
        .secretState.map { (it as? SecretState.NeedsBackup)?.persistableWallet }
}
