package co.electriccoin.zcash.ui

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.serialization.generateHashCode
import co.electriccoin.zcash.ui.common.datasource.MessageAvailabilityDataSource
import co.electriccoin.zcash.ui.design.KeyboardManager
import co.electriccoin.zcash.ui.design.SheetStateManager
import co.electriccoin.zcash.ui.screen.ExternalUrl
import co.electriccoin.zcash.ui.screen.about.util.WebBrowserUtil
import co.electriccoin.zcash.ui.screen.flexa.FlexaViewModel
import com.flexa.core.Flexa
import com.flexa.spend.buildSpend
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer

interface Navigator {
    suspend fun executeCommand(command: NavigationCommand)
}

class NavigatorImpl(
    private val activity: ComponentActivity,
    private val navController: NavHostController,
    private val flexaViewModel: FlexaViewModel,
    private val keyboardManager: KeyboardManager,
    private val sheetStateManager: SheetStateManager,
    private val messageAvailabilityDataSource: MessageAvailabilityDataSource,
) : Navigator {
    override suspend fun executeCommand(command: NavigationCommand) {
        keyboardManager.close()
        sheetStateManager.hide()
        when (command) {
            is NavigationCommand.Forward -> forward(command)
            is NavigationCommand.Replace -> replace(command)
            is NavigationCommand.ReplaceAll -> replaceAll(command)
            NavigationCommand.Back -> navController.popBackStack()
            is NavigationCommand.BackTo -> backTo(command)
            NavigationCommand.BackToRoot -> backToRoot()
        }
    }

    @SuppressLint("RestrictedApi")
    @OptIn(InternalSerializationApi::class)
    private fun backTo(command: NavigationCommand.BackTo) {
        navController.popBackStack(
            destinationId = command.route.serializer().generateHashCode(),
            inclusive = false
        )
    }

    private fun backToRoot() {
        navController.popBackStack(
            destinationId = navController.graph.startDestinationId,
            inclusive = false
        )
    }

    private fun replaceAll(command: NavigationCommand.ReplaceAll) {
        command.routes.forEachIndexed { index, route ->
            when (route) {
                co.electriccoin.zcash.ui.screen.flexa.Flexa -> {
                    if (index == 0) {
                        navController.popBackStack(
                            route = navController.graph.startDestinationId,
                            inclusive = false
                        )
                    }

                    if (index != command.routes.lastIndex) {
                        throw UnsupportedOperationException("Flexa can be opened as last screen only")
                    }

                    createFlexaFlow(flexaViewModel)
                }

                is ExternalUrl -> {
                    if (index == 0) {
                        navController.popBackStack(
                            route = navController.graph.startDestinationId,
                            inclusive = false
                        )
                    }

                    if (index != command.routes.lastIndex) {
                        throw UnsupportedOperationException("External url can be opened as last screen only")
                    }

                    messageAvailabilityDataSource.onThirdPartyUiShown()
                    WebBrowserUtil.startActivity(activity, route.url)
                }

                else -> {
                    navController.executeNavigation(route = route) {
                        if (index == 0) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = false
                            }
                        }
                    }
                }
            }
        }
    }

    private fun replace(command: NavigationCommand.Replace) {
        command.routes.forEachIndexed { index, route ->
            when (route) {
                co.electriccoin.zcash.ui.screen.flexa.Flexa -> {
                    if (index == 0) {
                        navController.popBackStack()
                    }

                    if (index != command.routes.lastIndex) {
                        throw UnsupportedOperationException("Flexa can be opened as last screen only")
                    }

                    createFlexaFlow(flexaViewModel)
                }

                is ExternalUrl -> {
                    if (index == 0) {
                        navController.popBackStack()
                    }

                    if (index != command.routes.lastIndex) {
                        throw UnsupportedOperationException("External url can be opened as last screen only")
                    }

                    messageAvailabilityDataSource.onThirdPartyUiShown()
                    WebBrowserUtil.startActivity(activity, route.url)
                }

                else -> {
                    navController.executeNavigation(route = route) {
                        if (index == 0) {
                            popUpTo(navController.currentBackStackEntry?.destination?.id ?: 0) {
                                inclusive = true
                            }
                        }
                    }
                }
            }
        }
    }

    private fun forward(command: NavigationCommand.Forward) {
        command.routes.forEach { route ->
            when (route) {
                co.electriccoin.zcash.ui.screen.flexa.Flexa -> createFlexaFlow(flexaViewModel)
                is ExternalUrl -> WebBrowserUtil.startActivity(activity, route.url)
                else -> navController.executeNavigation(route = route)
            }
        }

        if (command.routes.lastOrNull() in listOf(ExternalUrl, co.electriccoin.zcash.ui.screen.flexa.Flexa)) {
            messageAvailabilityDataSource.onThirdPartyUiShown()
        }
    }

    private fun NavHostController.executeNavigation(
        route: Any,
        builder: (NavOptionsBuilder.() -> Unit)? = null
    ) {
        if (route is String) {
            if (builder == null) {
                navigate(route)
            } else {
                navigate(route, builder)
            }
        } else {
            if (builder == null) {
                navigate(route)
            } else {
                navigate(route, builder)
            }
        }
    }

    private fun createFlexaFlow(flexaViewModel: FlexaViewModel) {
        messageAvailabilityDataSource.onThirdPartyUiShown()
        Flexa
            .buildSpend()
            .onTransactionRequest { result -> flexaViewModel.createTransaction(result) }
            .build()
            .open(activity)
    }
}
