package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.exception.InitializeException
import cash.z.ecc.android.sdk.model.AccountImportSetup
import cash.z.ecc.android.sdk.model.AccountPurpose
import cash.z.ecc.android.sdk.model.UnifiedFullViewingKey
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import com.keystone.module.ZcashAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.jvm.Throws

class CreateKeystoneAccountUseCase(
    private val accountDataSource: AccountDataSource,
    private val synchronizerProvider: SynchronizerProvider,
    private val navigationRouter: NavigationRouter
) {
    @Throws(InitializeException.ImportAccountException::class)
    suspend operator fun invoke(account: ZcashAccount) = withContext(Dispatchers.IO) {
        val createdAccount = synchronizerProvider.getSynchronizer().importAccountByUfvk(
            purpose = AccountPurpose.Spending,
            setup = AccountImportSetup(
                accountName = "",
                keySource = "keystone",
                ufvk = UnifiedFullViewingKey(account.ufvk),
            ),
        )

        accountDataSource.selectAccount(createdAccount)
        navigationRouter.backToRoot()
    }
}
