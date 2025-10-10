package co.electriccoin.zcash.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.provider.ApplicationStateProvider
import co.electriccoin.zcash.ui.common.usecase.GetHomeMessageUseCase
import co.electriccoin.zcash.ui.common.viewmodel.SecretState
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.design.LocalKeyboardManager
import co.electriccoin.zcash.ui.design.LocalSheetStateManager
import co.electriccoin.zcash.ui.design.animation.ScreenAnimation.enterTransition
import co.electriccoin.zcash.ui.design.animation.ScreenAnimation.exitTransition
import co.electriccoin.zcash.ui.design.animation.ScreenAnimation.popEnterTransition
import co.electriccoin.zcash.ui.design.animation.ScreenAnimation.popExitTransition
import co.electriccoin.zcash.ui.screen.flexa.FlexaViewModel
import co.electriccoin.zcash.ui.screen.warning.viewmodel.StorageCheckViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun RootNavGraph(
    secretState: SecretState,
    walletViewModel: WalletViewModel,
    storageCheckViewModel: StorageCheckViewModel = koinViewModel(),
) {
    val keyboardManager = LocalKeyboardManager.current
    val flexaViewModel = koinViewModel<FlexaViewModel>()
    val navigationRouter = koinInject<NavigationRouter>()
    val sheetStateManager = LocalSheetStateManager.current
    val applicationStateProvider = koinInject<ApplicationStateProvider>()
    val navController = LocalNavController.current
    val activity = LocalActivity.current
    val getHomeMessage = koinInject<GetHomeMessageUseCase>()
    getHomeMessage.observe().collectAsStateWithLifecycle() // hook up for collection
    val navigator: Navigator =
        remember(
            activity,
            navController,
            flexaViewModel,
            keyboardManager,
            sheetStateManager,
            applicationStateProvider
        ) {
            NavigatorImpl(
                activity = activity,
                navController = navController,
                flexaViewModel = flexaViewModel,
                keyboardManager = keyboardManager,
                sheetStateManager = sheetStateManager,
                applicationStateProvider = applicationStateProvider
            )
        }

    LaunchedEffect(navigationRouter) {
        navigationRouter.observePipeline().collect {
            navigator.executeCommand(it)
        }
    }

    NavHost(
        navController = navController,
        startDestination = OnboardingGraph,
        modifier = Modifier.fillMaxSize(),
        enterTransition = { enterTransition() },
        exitTransition = { exitTransition() },
        popEnterTransition = { popEnterTransition() },
        popExitTransition = { popExitTransition() }
    ) {
        this.onboardingNavGraph(
            activity = activity,
            navigationRouter = navigationRouter,
            walletViewModel = walletViewModel
        )

        this.walletNavGraph(
            activity = activity,
            walletViewModel = walletViewModel,
            storageCheckViewModel = storageCheckViewModel,
            navigationRouter = navigationRouter
        )
    }

    LaunchedEffect(secretState, navController) {
        if (secretState == SecretState.READY &&
            navController.currentDestination?.parent?.route != MainAppGraph::class.qualifiedName
        ) {
            keyboardManager.close()
            sheetStateManager.hide()
            navController.navigate(MainAppGraph) {
                popUpTo(OnboardingGraph) {
                    inclusive = true
                }
            }
        } else if (
            secretState == SecretState.NONE &&
            navController.currentDestination?.parent?.route != OnboardingGraph::class.qualifiedName
        ) {
            keyboardManager.close()
            sheetStateManager.hide()
            navController.navigate(OnboardingGraph) {
                popUpTo(MainAppGraph) {
                    inclusive = true
                }
            }
        }
    }
}

@Serializable
data object OnboardingGraph

@Serializable
data object MainAppGraph
