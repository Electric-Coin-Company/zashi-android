package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.datasource.WalletBackupDataSource
import co.electriccoin.zcash.ui.common.repository.ShieldFundsRepository

class RemindShieldFundsLaterUseCase(
    private val navigationRouter: NavigationRouter,
    private val shieldFundsRepository: ShieldFundsRepository
) {
    suspend operator fun invoke() {
        shieldFundsRepository.remindMeLater()
        navigationRouter.backToRoot()
    }
}