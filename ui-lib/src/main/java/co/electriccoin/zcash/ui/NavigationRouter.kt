package co.electriccoin.zcash.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

interface NavigationRouter {
    fun forward(route: String)

    fun <T : Any> forward(route: T)

    fun replace(route: String)

    fun <T : Any> replace(route: T)

    fun back()

    fun backToRoot()

    fun observe(): Flow<NavigationCommand>
}

class NavigationRouterImpl : NavigationRouter {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val channel = Channel<NavigationCommand>()

    override fun forward(route: String) {
        scope.launch {
            channel.send(NavigationCommand.Forward.ByRoute(route))
        }
    }

    override fun <T : Any> forward(route: T) {
        scope.launch {
            channel.send(NavigationCommand.Forward.ByTypeSafetyRoute(route))
        }
    }

    override fun replace(route: String) {
        scope.launch {
            channel.send(NavigationCommand.Replace.ByRoute(route))
        }
    }

    override fun <T : Any> replace(route: T) {
        scope.launch {
            channel.send(NavigationCommand.Replace.ByTypeSafetyRoute(route))
        }
    }

    override fun back() {
        scope.launch {
            channel.send(NavigationCommand.Back)
        }
    }

    override fun backToRoot() {
        scope.launch {
            channel.send(NavigationCommand.BackToRoot)
        }
    }

    override fun observe() = channel.receiveAsFlow()
}

sealed interface NavigationCommand {
    sealed interface Forward : NavigationCommand {
        data class ByRoute(val route: String) : Forward

        data class ByTypeSafetyRoute<T : Any>(val route: T) : Forward
    }

    sealed interface Replace : NavigationCommand {
        data class ByRoute(val route: String) : Replace

        data class ByTypeSafetyRoute<T : Any>(val route: T) : Replace
    }

    data object Back : NavigationCommand

    data object BackToRoot : NavigationCommand
}
