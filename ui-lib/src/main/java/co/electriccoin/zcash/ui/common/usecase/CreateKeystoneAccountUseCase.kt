package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.exception.InitializeException
import cash.z.ecc.android.sdk.model.AccountImportSetup
import cash.z.ecc.android.sdk.model.AccountPurpose
import cash.z.ecc.android.sdk.model.UnifiedFullViewingKey
import cash.z.ecc.android.sdk.model.Zip32AccountIndex
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import com.keystone.module.ZcashAccount
import com.keystone.module.ZcashAccounts

class CreateKeystoneAccountUseCase(
    private val accountDataSource: AccountDataSource,
    private val navigationRouter: NavigationRouter
) {
    @OptIn(ExperimentalStdlibApi::class)
    @Throws(InitializeException.ImportAccountException::class)
    suspend operator fun invoke(
        accounts: ZcashAccounts,
        account: ZcashAccount
    ) {
        val createdAccount =
            accountDataSource.importAccountByUfvk(
                purpose = AccountPurpose.Spending,
                setup =
                    AccountImportSetup(
                        accountName = "",
                        keySource = "keystone",
                        ufvk = UnifiedFullViewingKey(account.ufvk),
                        seedFingerprint = accounts.seedFingerprint.hexToByteArray(),
                        zip32AccountIndex = account.name?.toLongOrNull()?.let { Zip32AccountIndex.new(it) }
                    ),
            )

        accountDataSource.selectAccount(createdAccount)
        navigationRouter.backToRoot()
    }
}
