package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import kotlinx.coroutines.flow.filterNotNull

class ObserveSelectedWalletAccountUseCase(
    private val accountDataSource: AccountDataSource
) {
    operator fun invoke() = accountDataSource.selectedAccount

    fun require() = accountDataSource.selectedAccount.filterNotNull()
}
