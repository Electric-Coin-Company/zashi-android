package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.model.WalletAccount

class SelectWalletAccountUseCase(
    private val accountDataSource: AccountDataSource,
    private val navigationRouter: NavigationRouter
) {
    suspend operator fun invoke(
        account: WalletAccount,
        hideBottomSheet: suspend () -> Unit
    ) {
        accountDataSource.selectAccount(account)
        hideBottomSheet()
        navigationRouter.back()
    }
}
