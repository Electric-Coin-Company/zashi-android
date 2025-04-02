package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class GetKeystoneStatusUseCase(
    private val accountDataSource: AccountDataSource,
) {
    fun observe() =
        accountDataSource.allAccounts
            .map {
                val enabled = it?.none { account -> account is KeystoneAccount } ?: false
                if (enabled) {
                    Status.ENABLED
                } else {
                    Status.UNAVAILABLE
                }
            }.distinctUntilChanged()
}
