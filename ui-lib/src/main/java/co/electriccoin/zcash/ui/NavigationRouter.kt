package co.electriccoin.zcash.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

interface NavigationRouter {
    fun forward(route: Any)

    fun replace(route: Any)

    fun replaceAll(route: Any)

    fun newRoot(route: Any)

    fun back()

    fun backToRoot()

    fun observe(): Flow<NavigationCommand>
}

class NavigationRouterImpl : NavigationRouter {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val channel = Channel<NavigationCommand>()

    override fun forward(route: Any) {
        scope.launch {
            channel.send(NavigationCommand.Forward(route))
        }
    }

    override fun replace(route: Any) {
        scope.launch {
            channel.send(NavigationCommand.Replace(route))
        }
    }

    override fun replaceAll(route: Any) {
        scope.launch {
            channel.send(NavigationCommand.ReplaceAll(route))
        }
    }

    override fun newRoot(route: Any) {
        scope.launch {
            channel.send(NavigationCommand.NewRoot(route))
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
    data class Forward(val route: Any) : NavigationCommand

    data class Replace(val route: Any) : NavigationCommand

    data class ReplaceAll(val route: Any) : NavigationCommand

    data class NewRoot(val route: Any) : NavigationCommand

    data object Back : NavigationCommand

    data object BackToRoot : NavigationCommand
}
