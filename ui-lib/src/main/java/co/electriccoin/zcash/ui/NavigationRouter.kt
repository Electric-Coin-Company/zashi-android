package co.electriccoin.zcash.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

interface NavigationRouter {
    /**
     * Add [routes] to backstack.
     */
    fun forward(vararg routes: Any)

    /**
     * Replace current screen by the first route and add the rest of [routes] to backstack.
     */
    fun replace(vararg routes: Any)

    /**
     * Pop all screens except for the root and add [routes] to backstack.
     */
    fun replaceAll(vararg routes: Any)

    /**
     * Pop last screen from backstack.
     */
    fun back()

    fun backTo(route: KClass<*>)

    /**
     * Pop all screens from backstack except for the root.
     */
    fun backToRoot()

    fun observePipeline(): Flow<NavigationCommand>
}

class NavigationRouterImpl : NavigationRouter {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val channel = Channel<NavigationCommand>()

    override fun forward(vararg routes: Any) {
        scope.launch {
            channel.send(NavigationCommand.Forward(routes.toList()))
        }
    }

    override fun replace(vararg routes: Any) {
        scope.launch {
            channel.send(NavigationCommand.Replace(routes.toList()))
        }
    }

    override fun replaceAll(vararg routes: Any) {
        scope.launch {
            channel.send(NavigationCommand.ReplaceAll(routes.toList()))
        }
    }

    override fun back() {
        scope.launch {
            channel.send(NavigationCommand.Back)
        }
    }

    override fun backTo(route: KClass<*>) {
        scope.launch {
            channel.send(NavigationCommand.BackTo(route))
        }
    }

    override fun backToRoot() {
        scope.launch {
            channel.send(NavigationCommand.BackToRoot)
        }
    }

    override fun observePipeline() = channel.receiveAsFlow()
}

sealed interface NavigationCommand {
    data class Forward(val routes: List<Any>) : NavigationCommand

    data class Replace(val routes: List<Any>) : NavigationCommand

    data class ReplaceAll(val routes: List<Any>) : NavigationCommand

    data object Back : NavigationCommand

    data class BackTo(val route: KClass<*>) : NavigationCommand

    data object BackToRoot : NavigationCommand
}
