package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.datasource.AccountDataSource

class ObserveSelectedWalletAccountUseCase(private val accountDataSource: AccountDataSource) {
    operator fun invoke() = accountDataSource.selectedAccount
}
