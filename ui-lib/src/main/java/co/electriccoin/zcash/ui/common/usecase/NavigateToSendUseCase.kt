package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.HomeTabNavigationRouter
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.screen.home.HomeScreenIndex

class NavigateToSendUseCase(
    private val navigationRouter: NavigationRouter,
    private val homeTabNavigationRouter: HomeTabNavigationRouter
) {
    operator fun invoke() {
        homeTabNavigationRouter.select(HomeScreenIndex.SEND)
        navigationRouter.backToRoot()
    }
}
