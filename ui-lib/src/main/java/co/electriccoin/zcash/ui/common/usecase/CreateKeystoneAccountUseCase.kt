package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.exception.InitializeException
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import com.keystone.module.ZcashAccount
import com.keystone.module.ZcashAccounts

class CreateKeystoneAccountUseCase(
    private val accountDataSource: AccountDataSource,
    private val navigationRouter: NavigationRouter
) {
    @Throws(InitializeException.ImportAccountException::class)
    suspend operator fun invoke(
        accounts: ZcashAccounts,
        account: ZcashAccount
    ) {
        val createdAccount =
            accountDataSource.importKeystoneAccount(
                ufvk = account.ufvk,
                seedFingerprint = accounts.seedFingerprint,
                index = account.index.toLong()
            )

        accountDataSource.selectAccount(createdAccount)
        navigationRouter.backToRoot()
    }
}
