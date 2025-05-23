package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import kotlinx.coroutines.flow.filterNotNull

class GetWalletAccountsUseCase(
    private val accountDataSource: AccountDataSource
) {
    fun observe() = accountDataSource.allAccounts

    fun require() = accountDataSource.allAccounts.filterNotNull()
}
