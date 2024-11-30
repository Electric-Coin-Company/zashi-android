package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.model.WalletAccount

class SelectWalletAccountUseCase(private val accountDataSource: AccountDataSource) {
    suspend operator fun invoke(account: WalletAccount) = accountDataSource.selectAccount(account)
}
