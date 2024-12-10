package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.WalletCoordinator
import com.keystone.module.ZcashAccount

class DeriveKeystoneAccountUnifiedAddressUseCase(
    private val walletCoordinator: WalletCoordinator,
) {
    suspend operator fun invoke(account: ZcashAccount): String {
        // TODO keystone derivation
        // return DerivationTool.getInstance().deriveUnifiedAddress(
        //     viewingKey = account.ufvk,
        //     network = walletCoordinator.persistableWallet.filterNotNull().first().network
        // )
        return "placeholder because sdk crashes" // TODO keystone
    }
}
