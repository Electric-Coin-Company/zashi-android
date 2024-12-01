package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.tool.DerivationTool
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import com.keystone.module.ZcashAccount

class DeriveKeystoneAccountUnifiedAddressUseCase(
    private val accountDataSource: AccountDataSource,
) {
    suspend operator fun invoke(account: ZcashAccount): String =
        DerivationTool.getInstance().deriveUnifiedAddress(
            viewingKey = account.ufvk,
            network = accountDataSource.getZashiAccount().network
        )
}
