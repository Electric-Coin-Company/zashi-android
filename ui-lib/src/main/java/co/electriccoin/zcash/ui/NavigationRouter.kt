package co.electriccoin.zcash.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

interface NavigationRouter {
    fun forward(route: String)

    fun replace(route: String)

    fun back()

    fun observe(): Flow<NavigationCommand>
}

class NavigationRouterImpl : NavigationRouter {
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    private val channel = Channel<NavigationCommand>()

    override fun forward(route: String) {
        scope.launch {
            channel.send(NavigationCommand.Forward(route))
        }
    }

    override fun replace(route: String) {
        scope.launch {
            channel.send(NavigationCommand.Replace(route))
        }
    }

    override fun back() {
        scope.launch {
            channel.send(NavigationCommand.Back)
        }
    }

    override fun observe() = channel.consumeAsFlow()
}

sealed interface NavigationCommand {
    data class Forward(val route: String) : NavigationCommand

    data class Replace(val route: String) : NavigationCommand

    data object Back : NavigationCommand
}
