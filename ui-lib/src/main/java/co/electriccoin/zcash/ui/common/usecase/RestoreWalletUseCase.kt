package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.zcash.ui.common.repository.WalletRepository

class RestoreWalletUseCase(
    private val walletRepository: WalletRepository,
    private val context: Context,
) {
    operator fun invoke(
        seedPhrase: SeedPhrase,
        birthday: BlockHeight?
    ) {
        walletRepository.restoreWallet(
            network = ZcashNetwork.fromResources(context),
            seedPhrase = seedPhrase,
            birthday = birthday
        )
    }
}
