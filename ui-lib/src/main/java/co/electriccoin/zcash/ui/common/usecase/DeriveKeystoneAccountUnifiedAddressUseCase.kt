package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.WalletCoordinator
import cash.z.ecc.android.sdk.tool.DerivationTool
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import com.keystone.module.ZcashAccount
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class DeriveKeystoneAccountUnifiedAddressUseCase(
    private val walletCoordinator: WalletCoordinator,
) {
    suspend operator fun invoke(account: ZcashAccount): String {
        // TODO keystone derivation
        return DerivationTool.getInstance().deriveUnifiedAddress(
            viewingKey = account.ufvk,
            network = walletCoordinator.persistableWallet.filterNotNull().first().network
        )
    }
}
