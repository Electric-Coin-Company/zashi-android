package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.datasource.AccountDataSource

class ObserveOnAccountChangedUseCase(private val accountDataSource: AccountDataSource) {
    operator fun invoke() = accountDataSource.onAccountChanged
}
