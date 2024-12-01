package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import com.keystone.module.ZcashAccount

class CreateKeystoneAccountUseCase(private val accountDataSource: AccountDataSource) {
    suspend operator fun invoke(account: ZcashAccount) {
        // create
        // select
        // accountDataSource.selectAccount(account)
    }
}
