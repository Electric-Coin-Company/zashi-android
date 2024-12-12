package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.tool.DerivationTool
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider
import co.electriccoin.zcash.ui.screen.addressbook.viewmodel.ADDRESS_MAX_LENGTH
import com.keystone.module.ZcashAccount

class DeriveKeystoneAccountUnifiedAddressUseCase(
    private val persistableWalletProvider: PersistableWalletProvider
) {
    suspend operator fun invoke(account: ZcashAccount): String {
        val address = DerivationTool.getInstance().deriveUnifiedAddress(
            viewingKey = account.ufvk,
            network = persistableWalletProvider.getPersistableWallet().network
        )
        return "${address.take(ADDRESS_MAX_LENGTH)}..."
    }
}
