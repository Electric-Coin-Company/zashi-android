package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class GetSelectedWalletAccountUseCase(private val accountDataSource: AccountDataSource) {
    suspend operator fun invoke() = accountDataSource.getSelectedAccount()
}
