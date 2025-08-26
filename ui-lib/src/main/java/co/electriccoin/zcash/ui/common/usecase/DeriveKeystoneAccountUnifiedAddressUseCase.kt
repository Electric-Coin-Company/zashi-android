package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.tool.DerivationTool
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider
import co.electriccoin.zcash.ui.screen.addressbook.ADDRESS_MAX_LENGTH
import com.keystone.module.ZcashAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeriveKeystoneAccountUnifiedAddressUseCase(
    private val persistableWalletProvider: PersistableWalletProvider
) {
    suspend operator fun invoke(account: ZcashAccount): String =
        withContext(Dispatchers.Default) {
            val address =
                DerivationTool.getInstance().deriveUnifiedAddress(
                    viewingKey = account.ufvk,
                    network = persistableWalletProvider.requirePersistableWallet().network
                )
            "${address.take(ADDRESS_MAX_LENGTH)}..."
        }
}
