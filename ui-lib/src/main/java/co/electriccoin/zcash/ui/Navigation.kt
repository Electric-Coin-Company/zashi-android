package co.electriccoin.zcash.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.electriccoin.zcash.ui.NavigationArguments.SEND_AMOUNT
import co.electriccoin.zcash.ui.NavigationArguments.SEND_MEMO
import co.electriccoin.zcash.ui.NavigationArguments.SEND_RECIPIENT_ADDRESS
import co.electriccoin.zcash.ui.NavigationTargets.ABOUT
import co.electriccoin.zcash.ui.NavigationTargets.ADVANCED_SETTINGS
import co.electriccoin.zcash.ui.NavigationTargets.EXPORT_PRIVATE_DATA
import co.electriccoin.zcash.ui.NavigationTargets.HISTORY
import co.electriccoin.zcash.ui.NavigationTargets.HOME
import co.electriccoin.zcash.ui.NavigationTargets.REQUEST
import co.electriccoin.zcash.ui.NavigationTargets.SCAN
import co.electriccoin.zcash.ui.NavigationTargets.SEED_RECOVERY
import co.electriccoin.zcash.ui.NavigationTargets.SETTINGS
import co.electriccoin.zcash.ui.NavigationTargets.SUPPORT
import co.electriccoin.zcash.ui.configuration.ConfigurationEntries
import co.electriccoin.zcash.ui.configuration.RemoteConfig
import co.electriccoin.zcash.ui.screen.about.WrapAbout
import co.electriccoin.zcash.ui.screen.advancedsettings.WrapAdvancedSettings
import co.electriccoin.zcash.ui.screen.exportdata.WrapExportPrivateData
import co.electriccoin.zcash.ui.screen.history.WrapHistory
import co.electriccoin.zcash.ui.screen.home.WrapHome
import co.electriccoin.zcash.ui.screen.request.WrapRequest
import co.electriccoin.zcash.ui.screen.scan.WrapScanValidator
import co.electriccoin.zcash.ui.screen.scan.model.ScanResult
import co.electriccoin.zcash.ui.screen.seedrecovery.WrapSeedRecovery
import co.electriccoin.zcash.ui.screen.send.model.SendArgumentsWrapper
import co.electriccoin.zcash.ui.screen.settings.WrapSettings
import co.electriccoin.zcash.ui.screen.support.WrapSupport
import co.electriccoin.zcash.ui.screen.update.WrapCheckForUpdate
import kotlinx.serialization.json.Json

@Composable
@Suppress("LongMethod")
internal fun MainActivity.Navigation() {
    val navController =
        rememberNavController().also {
            navControllerForTesting = it
        }

    NavHost(navController = navController, startDestination = HOME) {
        composable(HOME) { backStackEntry ->
            WrapHome(
                onPageChange = {
                    homeViewModel.screenIndex.value = it
                },
                goBack = { finish() },
                goHistory = { navController.navigateJustOnce(HISTORY) },
                goSettings = { navController.navigateJustOnce(SETTINGS) },
                goScan = { navController.navigateJustOnce(SCAN) },
                // At this point we only read scan result data
                sendArgumentsWrapper =
                    SendArgumentsWrapper(
                        recipientAddress =
                            backStackEntry.savedStateHandle.get<String>(SEND_RECIPIENT_ADDRESS)?.let {
                                Json.decodeFromString<ScanResult>(it).toRecipient()
                            },
                        amount = backStackEntry.savedStateHandle.get<String>(SEND_AMOUNT),
                        memo = backStackEntry.savedStateHandle.get<String>(SEND_MEMO)
                    ),
            )
            // Remove used Send screen parameters passed from the Scan screen if some exist
            backStackEntry.savedStateHandle.remove<String>(SEND_RECIPIENT_ADDRESS)
            backStackEntry.savedStateHandle.remove<String>(SEND_AMOUNT)
            backStackEntry.savedStateHandle.remove<String>(SEND_MEMO)

            if (ConfigurationEntries.IS_APP_UPDATE_CHECK_ENABLED.getValue(RemoteConfig.current)) {
                WrapCheckForUpdate()
            }
        }
        composable(SETTINGS) {
            WrapSettings(
                goAbout = {
                    navController.navigateJustOnce(ABOUT)
                },
                goAdvancedSettings = {
                    navController.navigateJustOnce(ADVANCED_SETTINGS)
                },
                goBack = {
                    navController.popBackStackJustOnce(SETTINGS)
                },
                goFeedback = {
                    navController.navigateJustOnce(SUPPORT)
                },
            )
        }
        composable(ADVANCED_SETTINGS) {
            WrapAdvancedSettings(
                goBack = {
                    navController.popBackStackJustOnce(ADVANCED_SETTINGS)
                },
                goExportPrivateData = {
                    navController.navigateJustOnce(EXPORT_PRIVATE_DATA)
                },
                goSeedRecovery = {
                    navController.navigateJustOnce(SEED_RECOVERY)
                },
                goChooseServer = {
                    // TODO [#1235]: Create screen for selecting the lightwalletd server
                    // TODO [#1235]: https://github.com/Electric-Coin-Company/zashi-android/issues/1235
                }
            )
        }
        composable(SEED_RECOVERY) {
            WrapSeedRecovery(
                goBack = {
                    navController.popBackStackJustOnce(SEED_RECOVERY)
                },
                onDone = {
                    navController.popBackStackJustOnce(SEED_RECOVERY)
                }
            )
        }
        composable(REQUEST) {
            WrapRequest(goBack = { navController.popBackStackJustOnce(REQUEST) })
        }
        composable(SUPPORT) {
            // Pop back stack won't be right if we deep link into support
            WrapSupport(goBack = { navController.popBackStackJustOnce(SUPPORT) })
        }
        composable(ABOUT) {
            WrapAbout(goBack = { navController.popBackStackJustOnce(ABOUT) })
        }
        composable(SCAN) {
            WrapScanValidator(
                onScanValid = { scanResult ->
                    // At this point we only pass scan result data to recipient address
                    navController.previousBackStackEntry?.savedStateHandle?.apply {
                        set(SEND_RECIPIENT_ADDRESS, Json.encodeToString(ScanResult.serializer(), scanResult))
                        set(SEND_AMOUNT, null)
                        set(SEND_MEMO, null)
                    }
                    navController.popBackStackJustOnce(SCAN)
                },
                goBack = { navController.popBackStackJustOnce(SCAN) }
            )
        }
        composable(EXPORT_PRIVATE_DATA) {
            WrapExportPrivateData(
                goBack = { navController.popBackStackJustOnce(EXPORT_PRIVATE_DATA) },
                onConfirm = { navController.popBackStackJustOnce(EXPORT_PRIVATE_DATA) }
            )
        }
        composable(HISTORY) {
            WrapHistory(goBack = { navController.navigateUp() })
        }
    }
}

private fun NavHostController.navigateJustOnce(
    route: String,
    navOptionsBuilder: (NavOptionsBuilder.() -> Unit)? = null
) {
    if (currentDestination?.route == route) {
        return
    }

    if (navOptionsBuilder != null) {
        navigate(route, navOptionsBuilder)
    } else {
        navigate(route)
    }
}

/**
 * Pops up the current screen from the back stack. Parameter currentRouteToBePopped is meant to be
 * set only to the current screen so we can easily debounce multiple screen popping from the back stack.
 *
 * @param currentRouteToBePopped current screen which should be popped up.
 */
private fun NavHostController.popBackStackJustOnce(currentRouteToBePopped: String) {
    if (currentDestination?.route != currentRouteToBePopped) {
        return
    }
    popBackStack()
}

object NavigationArguments {
    const val SEND_RECIPIENT_ADDRESS = "send_recipient_address"
    const val SEND_AMOUNT = "send_amount"
    const val SEND_MEMO = "send_memo"
}

object NavigationTargets {
    const val ABOUT = "about"
    const val ACCOUNT = "account"
    const val ADVANCED_SETTINGS = "advanced_settings"
    const val EXPORT_PRIVATE_DATA = "export_private_data"
    const val HISTORY = "history"
    const val HOME = "home"
    const val RECEIVE = "receive"
    const val REQUEST = "request"
    const val SCAN = "scan"
    const val SEED_RECOVERY = "seed_recovery"
    const val SEND = "send"
    const val SETTINGS = "settings"
    const val SUPPORT = "support"
}
