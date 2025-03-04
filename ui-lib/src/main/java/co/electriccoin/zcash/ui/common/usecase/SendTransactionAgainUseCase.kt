package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.HomeTabNavigationRouter
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.screen.home.HomeScreenIndex

class SendTransactionAgainUseCase(
    private val prefillSendUseCase: PrefillSendUseCase,
    private val homeTabNavigationRouter: HomeTabNavigationRouter,
    private val navigationRouter: NavigationRouter
) {
    operator fun invoke(value: DetailedTransactionData) {
        homeTabNavigationRouter.select(HomeScreenIndex.SEND)
        prefillSendUseCase.request(value)
        navigationRouter.backToRoot()
    }
}
