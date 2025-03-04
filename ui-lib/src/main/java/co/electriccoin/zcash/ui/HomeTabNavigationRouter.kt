package co.electriccoin.zcash.ui

import co.electriccoin.zcash.ui.screen.home.HomeScreenIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

interface HomeTabNavigationRouter {
    fun select(tab: HomeScreenIndex)

    fun observe(): Flow<HomeScreenIndex>
}

class HomeTabNavigationRouterImpl : HomeTabNavigationRouter {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val channel = Channel<HomeScreenIndex>()

    override fun select(tab: HomeScreenIndex) {
        scope.launch {
            channel.send(tab)
        }
    }

    override fun observe() = channel.receiveAsFlow()
}
