package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider
import com.keystone.module.ZcashAccount

class DeriveKeystoneAccountUnifiedAddressUseCase(
    private val persistableWalletProvider: PersistableWalletProvider
) {
    suspend operator fun invoke(account: ZcashAccount): String {
        // TODO keystone derivation
        // return DerivationTool.getInstance().deriveUnifiedAddress(
        //     viewingKey = account.ufvk,
        //     network = persistableWalletProvider.getPersistableWallet().network
        // )
        return "placeholder because sdk crashes" // TODO keystone
    }
}
