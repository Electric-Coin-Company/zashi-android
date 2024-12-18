package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import kotlinx.coroutines.flow.filterNotNull

class ObserveWalletAccountsUseCase(private val accountDataSource: AccountDataSource) {
    operator fun invoke() = accountDataSource.allAccounts

    fun require() = accountDataSource.allAccounts.filterNotNull()
}
