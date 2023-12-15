package co.electriccoin.zcash.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.NavigationArguments.SEND_AMOUNT
import co.electriccoin.zcash.ui.NavigationArguments.SEND_MEMO
import co.electriccoin.zcash.ui.NavigationArguments.SEND_RECIPIENT_ADDRESS
import co.electriccoin.zcash.ui.NavigationTargets.ABOUT
import co.electriccoin.zcash.ui.NavigationTargets.EXPORT_PRIVATE_DATA
import co.electriccoin.zcash.ui.NavigationTargets.HISTORY
import co.electriccoin.zcash.ui.NavigationTargets.HOME
import co.electriccoin.zcash.ui.NavigationTargets.REQUEST
import co.electriccoin.zcash.ui.NavigationTargets.SCAN
import co.electriccoin.zcash.ui.NavigationTargets.SEED_RECOVERY
import co.electriccoin.zcash.ui.NavigationTargets.SETTINGS
import co.electriccoin.zcash.ui.NavigationTargets.SUPPORT
import co.electriccoin.zcash.ui.NavigationTargets.WALLET_ADDRESS_DETAILS
import co.electriccoin.zcash.ui.configuration.ConfigurationEntries
import co.electriccoin.zcash.ui.configuration.RemoteConfig
import co.electriccoin.zcash.ui.screen.about.WrapAbout
import co.electriccoin.zcash.ui.screen.account.WrapAccount
import co.electriccoin.zcash.ui.screen.address.WrapWalletAddresses
import co.electriccoin.zcash.ui.screen.balances.WrapBalances
import co.electriccoin.zcash.ui.screen.exportdata.WrapExportPrivateData
import co.electriccoin.zcash.ui.screen.history.WrapHistory
import co.electriccoin.zcash.ui.screen.home.ForcePage
import co.electriccoin.zcash.ui.screen.home.WrapHome
import co.electriccoin.zcash.ui.screen.home.model.TabItem
import co.electriccoin.zcash.ui.screen.receive.WrapReceive
import co.electriccoin.zcash.ui.screen.request.WrapRequest
import co.electriccoin.zcash.ui.screen.scan.WrapScanValidator
import co.electriccoin.zcash.ui.screen.seedrecovery.WrapSeedRecovery
import co.electriccoin.zcash.ui.screen.send.WrapSend
import co.electriccoin.zcash.ui.screen.send.model.SendArgumentsWrapper
import co.electriccoin.zcash.ui.screen.settings.WrapSettings
import co.electriccoin.zcash.ui.screen.support.WrapSupport
import co.electriccoin.zcash.ui.screen.update.WrapCheckForUpdate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
@Suppress("LongMethod")
internal fun MainActivity.Navigation() {
    val context = LocalContext.current
    val navController =
        rememberNavController().also {
            navControllerForTesting = it
        }

    // Flow for propagating the new page index to the pager in the view layer
    val forceHomePageIndexFlow: MutableSharedFlow<ForcePage?> =
        MutableSharedFlow(
            Int.MAX_VALUE,
            Int.MAX_VALUE,
            BufferOverflow.SUSPEND
        )

    NavHost(navController = navController, startDestination = HOME) {
        composable(HOME) { backStackEntry ->

            val forceIndex = forceHomePageIndexFlow.collectAsState(initial = null).value

            val homeGoBack: () -> Unit = {
                when (homeViewModel.screenIndex.value) {
                    0 -> finish()
                    1, 2, 3 -> forceHomePageIndexFlow.tryEmit(ForcePage())
                }
            }

            val tabs = persistentListOf(
                    TabItem(
                        index = 0,
                        title = stringResource(id = R.string.home_tab_account),
                        screenContent = {
                            WrapAccount(
                                goHistory = { navController.navigateJustOnce(HISTORY) },
                                goSettings = { navController.navigateJustOnce(SETTINGS) },
                            )
                        }
                    ),
                    TabItem(
                        index = 1,
                        title = stringResource(id = R.string.home_tab_send),
                        screenContent = {
                            WrapSend(
                                goToQrScanner = {
                                    Twig.info { "Opening Qr Scanner Screen" }
                                    navController.navigateJustOnce(SCAN)
                                },
                                goBack = homeGoBack,
                                goSettings = {
                                    navController.navigateJustOnce(SETTINGS)
                                },
                                sendArgumentsWrapper =
                                    SendArgumentsWrapper(
                                        recipientAddress = backStackEntry.savedStateHandle[SEND_RECIPIENT_ADDRESS],
                                        amount = backStackEntry.savedStateHandle[SEND_AMOUNT],
                                        memo = backStackEntry.savedStateHandle[SEND_MEMO]
                                    )
                            )
                        }
                    ),
                    TabItem(
                        index = 2,
                        title = stringResource(id = R.string.home_tab_receive),
                        screenContent = {
                            WrapReceive(
                                onSettings = { navController.navigateJustOnce(SETTINGS) },
                                onAddressDetails = { navController.navigateJustOnce(WALLET_ADDRESS_DETAILS) },
                            )
                        }
                    ),
                    TabItem(
                        index = 3,
                        title = stringResource(id = R.string.home_tab_balances),
                        screenContent = {
                            WrapBalances(
                                goSettings = { navController.navigateJustOnce(SETTINGS) }
                            )
                        }
                    )
                )
            WrapHome(
                tabs = tabs,
                forcePage = forceIndex,
                onPageChange = {
                    homeViewModel.screenIndex.value = it
                },
                goBack = homeGoBack
            )

            if (ConfigurationEntries.IS_APP_UPDATE_CHECK_ENABLED.getValue(RemoteConfig.current)) {
                WrapCheckForUpdate()
            }
        }
        composable(WALLET_ADDRESS_DETAILS) {
            WrapWalletAddresses(
                goBack = {
                    navController.popBackStackJustOnce(WALLET_ADDRESS_DETAILS)
                }
            )
        }
        composable(SETTINGS) {
            WrapSettings(
                goAbout = {
                    navController.navigateJustOnce(ABOUT)
                },
                goBack = {
                    navController.popBackStackJustOnce(SETTINGS)
                },
                goDocumentation = {
                    // TODO [#1084]: Documentation screen
                    // TODO [#1084]: https://github.com/Electric-Coin-Company/zashi-android/issues/1084
                    Toast.makeText(context, context.getString(R.string.not_implemented_yet), Toast.LENGTH_SHORT).show()
                },
                goExportPrivateData = {
                    navController.navigateJustOnce(EXPORT_PRIVATE_DATA)
                },
                goFeedback = {
                    navController.navigateJustOnce(SUPPORT)
                },
                goPrivacyPolicy = {
                    // TODO [#1083]: Privacy Policy screen
                    // TODO [#1083]: https://github.com/Electric-Coin-Company/zashi-android/issues/1083
                    Toast.makeText(context, context.getString(R.string.not_implemented_yet), Toast.LENGTH_SHORT).show()
                },
                goSeedRecovery = {
                    navController.navigateJustOnce(SEED_RECOVERY)
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
                onScanValid = { result ->
                    // At this point we only pass recipient address
                    navController.previousBackStackEntry?.savedStateHandle?.apply {
                        set(SEND_RECIPIENT_ADDRESS, result)
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
    const val WALLET_ADDRESS_DETAILS = "wallet_address_details"
}
