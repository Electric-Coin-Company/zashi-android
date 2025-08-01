package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.WalletRepository
import co.electriccoin.zcash.ui.screen.advancedsettings.AdvancedSettingsArgs

class OptInTorUseCase(
    private val navigationRouter: NavigationRouter,
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke(
        optIn: Boolean,
        onFinish: NavigationRouter.() -> Unit = { backTo(AdvancedSettingsArgs::class) }
    ) {
        walletRepository.enableTor(optIn)
        navigationRouter.onFinish()
    }
}
