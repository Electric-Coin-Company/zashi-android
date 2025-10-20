package co.electriccoin.zcash.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.NavigationArgs.ADDRESS_TYPE
import co.electriccoin.zcash.ui.NavigationTargets.CRASH_REPORTING_OPT_IN
import co.electriccoin.zcash.ui.NavigationTargets.DELETE_WALLET
import co.electriccoin.zcash.ui.NavigationTargets.EXPORT_PRIVATE_DATA
import co.electriccoin.zcash.ui.NavigationTargets.NOT_ENOUGH_SPACE
import co.electriccoin.zcash.ui.NavigationTargets.QR_CODE
import co.electriccoin.zcash.ui.NavigationTargets.REQUEST
import co.electriccoin.zcash.ui.NavigationTargets.SETTINGS
import co.electriccoin.zcash.ui.NavigationTargets.WHATS_NEW
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.provider.ApplicationStateProvider
import co.electriccoin.zcash.ui.common.usecase.GetHomeMessageUseCase
import co.electriccoin.zcash.ui.design.LocalKeyboardManager
import co.electriccoin.zcash.ui.design.LocalSheetStateManager
import co.electriccoin.zcash.ui.design.animation.ScreenAnimation.enterTransition
import co.electriccoin.zcash.ui.design.animation.ScreenAnimation.exitTransition
import co.electriccoin.zcash.ui.design.animation.ScreenAnimation.popEnterTransition
import co.electriccoin.zcash.ui.design.animation.ScreenAnimation.popExitTransition
import co.electriccoin.zcash.ui.screen.about.AboutArgs
import co.electriccoin.zcash.ui.screen.about.AboutScreen
import co.electriccoin.zcash.ui.screen.accountlist.AccountList
import co.electriccoin.zcash.ui.screen.accountlist.AndroidAccountList
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookArgs
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookScreen
import co.electriccoin.zcash.ui.screen.addressbook.SelectABRecipientArgs
import co.electriccoin.zcash.ui.screen.addressbook.SelectABRecipientScreen
import co.electriccoin.zcash.ui.screen.advancedsettings.AdvancedSettingsArgs
import co.electriccoin.zcash.ui.screen.advancedsettings.AdvancedSettingsScreen
import co.electriccoin.zcash.ui.screen.authentication.AuthenticationUseCase
import co.electriccoin.zcash.ui.screen.authentication.WrapAuthentication
import co.electriccoin.zcash.ui.screen.balances.spendable.SpendableBalanceArgs
import co.electriccoin.zcash.ui.screen.balances.spendable.SpendableBalanceScreen
import co.electriccoin.zcash.ui.screen.chooseserver.ChooseServerArgs
import co.electriccoin.zcash.ui.screen.chooseserver.ChooseServerScreen
import co.electriccoin.zcash.ui.screen.connectkeystone.AndroidConnectKeystone
import co.electriccoin.zcash.ui.screen.connectkeystone.ConnectKeystone
import co.electriccoin.zcash.ui.screen.contact.AddGenericABContactArgs
import co.electriccoin.zcash.ui.screen.contact.AddGenericABContactScreen
import co.electriccoin.zcash.ui.screen.contact.AddZashiABContactArgs
import co.electriccoin.zcash.ui.screen.contact.AddZashiABContactScreen
import co.electriccoin.zcash.ui.screen.contact.UpdateGenericABContactArgs
import co.electriccoin.zcash.ui.screen.contact.UpdateGenericABContactScreen
import co.electriccoin.zcash.ui.screen.crashreporting.AndroidCrashReportingOptIn
import co.electriccoin.zcash.ui.screen.deletewallet.WrapDeleteWallet
import co.electriccoin.zcash.ui.screen.error.AndroidErrorBottomSheet
import co.electriccoin.zcash.ui.screen.error.AndroidErrorDialog
import co.electriccoin.zcash.ui.screen.error.ErrorBottomSheet
import co.electriccoin.zcash.ui.screen.error.ErrorDialog
import co.electriccoin.zcash.ui.screen.exchangerate.optin.ExchangeRateOptInArgs
import co.electriccoin.zcash.ui.screen.exchangerate.optin.ExchangeRateOptInScreen
import co.electriccoin.zcash.ui.screen.exchangerate.settings.ExchangeRateSettingsArgs
import co.electriccoin.zcash.ui.screen.exchangerate.settings.ExchangeRateSettingsScreen
import co.electriccoin.zcash.ui.screen.exportdata.WrapExportPrivateData
import co.electriccoin.zcash.ui.screen.feedback.FeedbackArgs
import co.electriccoin.zcash.ui.screen.feedback.FeedbackScreen
import co.electriccoin.zcash.ui.screen.flexa.FlexaViewModel
import co.electriccoin.zcash.ui.screen.home.AndroidHome
import co.electriccoin.zcash.ui.screen.home.Home
import co.electriccoin.zcash.ui.screen.home.backup.AndroidWalletBackupDetail
import co.electriccoin.zcash.ui.screen.home.backup.AndroidWalletBackupInfo
import co.electriccoin.zcash.ui.screen.home.backup.SeedBackupInfo
import co.electriccoin.zcash.ui.screen.home.backup.WalletBackupDetail
import co.electriccoin.zcash.ui.screen.home.disconnected.AndroidWalletDisconnectedInfo
import co.electriccoin.zcash.ui.screen.home.disconnected.WalletDisconnectedInfo
import co.electriccoin.zcash.ui.screen.home.reporting.AndroidCrashReportOptIn
import co.electriccoin.zcash.ui.screen.home.reporting.CrashReportOptIn
import co.electriccoin.zcash.ui.screen.home.restoring.AndroidWalletRestoringInfo
import co.electriccoin.zcash.ui.screen.home.restoring.WalletRestoringInfo
import co.electriccoin.zcash.ui.screen.home.shieldfunds.AndroidShieldFundsInfo
import co.electriccoin.zcash.ui.screen.home.shieldfunds.ShieldFundsInfo
import co.electriccoin.zcash.ui.screen.home.syncing.AndroidWalletSyncingInfo
import co.electriccoin.zcash.ui.screen.home.syncing.WalletSyncingInfo
import co.electriccoin.zcash.ui.screen.home.updating.AndroidWalletUpdatingInfo
import co.electriccoin.zcash.ui.screen.home.updating.WalletUpdatingInfo
import co.electriccoin.zcash.ui.screen.integrations.IntegrationsArgs
import co.electriccoin.zcash.ui.screen.integrations.IntegrationsScreen
import co.electriccoin.zcash.ui.screen.pay.PayArgs
import co.electriccoin.zcash.ui.screen.pay.PayScreen
import co.electriccoin.zcash.ui.screen.pay.info.PayInfoArgs
import co.electriccoin.zcash.ui.screen.pay.info.PayInfoScreen
import co.electriccoin.zcash.ui.screen.qrcode.QrCodeScreen
import co.electriccoin.zcash.ui.screen.receive.ReceiveAddressType
import co.electriccoin.zcash.ui.screen.receive.ReceiveArgs
import co.electriccoin.zcash.ui.screen.receive.ReceiveScreen
import co.electriccoin.zcash.ui.screen.receive.info.ShieldedAddressInfoArgs
import co.electriccoin.zcash.ui.screen.receive.info.ShieldedAddressInfoScreen
import co.electriccoin.zcash.ui.screen.receive.info.TransparentAddressInfoArgs
import co.electriccoin.zcash.ui.screen.receive.info.TransparentAddressInfoScreen
import co.electriccoin.zcash.ui.screen.request.RequestScreen
import co.electriccoin.zcash.ui.screen.restore.info.AndroidSeedInfo
import co.electriccoin.zcash.ui.screen.restore.info.SeedInfo
import co.electriccoin.zcash.ui.screen.reviewtransaction.AndroidReviewTransaction
import co.electriccoin.zcash.ui.screen.reviewtransaction.ReviewTransactionArgs
import co.electriccoin.zcash.ui.screen.scan.ScanArgs
import co.electriccoin.zcash.ui.screen.scan.ScanGenericAddressArgs
import co.electriccoin.zcash.ui.screen.scan.ScanGenericAddressScreen
import co.electriccoin.zcash.ui.screen.scan.ScanZashiAddressScreen
import co.electriccoin.zcash.ui.screen.scan.thirdparty.AndroidThirdPartyScan
import co.electriccoin.zcash.ui.screen.scan.thirdparty.ThirdPartyScan
import co.electriccoin.zcash.ui.screen.scankeystone.ScanKeystonePCZTRequest
import co.electriccoin.zcash.ui.screen.scankeystone.ScanKeystoneSignInRequest
import co.electriccoin.zcash.ui.screen.scankeystone.WrapScanKeystonePCZTRequest
import co.electriccoin.zcash.ui.screen.scankeystone.WrapScanKeystoneSignInRequest
import co.electriccoin.zcash.ui.screen.selectkeystoneaccount.AndroidSelectKeystoneAccount
import co.electriccoin.zcash.ui.screen.selectkeystoneaccount.SelectKeystoneAccount
import co.electriccoin.zcash.ui.screen.send.Send
import co.electriccoin.zcash.ui.screen.send.WrapSend
import co.electriccoin.zcash.ui.screen.settings.WrapSettings
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.SignKeystoneTransactionArgs
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.SignKeystoneTransactionScreen
import co.electriccoin.zcash.ui.screen.swap.SwapArgs
import co.electriccoin.zcash.ui.screen.swap.SwapScreen
import co.electriccoin.zcash.ui.screen.swap.ab.AddSwapABContactArgs
import co.electriccoin.zcash.ui.screen.swap.ab.AddSwapABContactScreen
import co.electriccoin.zcash.ui.screen.swap.ab.SelectABSwapRecipientArgs
import co.electriccoin.zcash.ui.screen.swap.ab.SelectSwapABRecipientScreen
import co.electriccoin.zcash.ui.screen.swap.detail.SwapDetailArgs
import co.electriccoin.zcash.ui.screen.swap.detail.SwapDetailScreen
import co.electriccoin.zcash.ui.screen.swap.info.SwapInfoArgs
import co.electriccoin.zcash.ui.screen.swap.info.SwapInfoScreen
import co.electriccoin.zcash.ui.screen.swap.info.SwapRefundAddressInfoArgs
import co.electriccoin.zcash.ui.screen.swap.info.SwapRefundAddressInfoScreen
import co.electriccoin.zcash.ui.screen.swap.orconfirmation.ORSwapConfirmationArgs
import co.electriccoin.zcash.ui.screen.swap.orconfirmation.ORSwapConfirmationScreen
import co.electriccoin.zcash.ui.screen.swap.picker.SwapAssetPickerArgs
import co.electriccoin.zcash.ui.screen.swap.picker.SwapAssetPickerScreen
import co.electriccoin.zcash.ui.screen.swap.picker.SwapBlockchainPickerArgs
import co.electriccoin.zcash.ui.screen.swap.picker.SwapBlockchainPickerScreen
import co.electriccoin.zcash.ui.screen.swap.quote.SwapQuoteArgs
import co.electriccoin.zcash.ui.screen.swap.quote.SwapQuoteScreen
import co.electriccoin.zcash.ui.screen.swap.slippage.SwapSlippageArgs
import co.electriccoin.zcash.ui.screen.swap.slippage.SwapSlippageScreen
import co.electriccoin.zcash.ui.screen.taxexport.AndroidTaxExport
import co.electriccoin.zcash.ui.screen.taxexport.TaxExport
import co.electriccoin.zcash.ui.screen.tor.optin.TorOptInArgs
import co.electriccoin.zcash.ui.screen.tor.optin.TorOptInScreen
import co.electriccoin.zcash.ui.screen.tor.settings.TorSettingsArgs
import co.electriccoin.zcash.ui.screen.tor.settings.TorSettingsScreen
import co.electriccoin.zcash.ui.screen.transactiondetail.TransactionDetailArgs
import co.electriccoin.zcash.ui.screen.transactiondetail.TransactionDetailScreen
import co.electriccoin.zcash.ui.screen.transactionfilters.TransactionFiltersArgs
import co.electriccoin.zcash.ui.screen.transactionfilters.TransactionFiltersScreen
import co.electriccoin.zcash.ui.screen.transactionhistory.ActivityHistoryArgs
import co.electriccoin.zcash.ui.screen.transactionhistory.ActivityHistoryScreen
import co.electriccoin.zcash.ui.screen.transactionnote.AndroidTransactionNote
import co.electriccoin.zcash.ui.screen.transactionnote.TransactionNote
import co.electriccoin.zcash.ui.screen.transactionprogress.TransactionProgressArgs
import co.electriccoin.zcash.ui.screen.transactionprogress.TransactionProgressScreen
import co.electriccoin.zcash.ui.screen.walletbackup.AndroidWalletBackup
import co.electriccoin.zcash.ui.screen.walletbackup.WalletBackup
import co.electriccoin.zcash.ui.screen.warning.WrapNotEnoughSpace
import co.electriccoin.zcash.ui.screen.whatsnew.WrapWhatsNew
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

// TODO [#1297]: Consider: Navigation passing complex data arguments different way
// TODO [#1297]: https://github.com/Electric-Coin-Company/zashi-android/issues/1297
@Composable
@Suppress("LongMethod", "CyclomaticComplexMethod")
internal fun MainActivity.Navigation() {
    val navController = LocalNavController.current
    val keyboardManager = LocalKeyboardManager.current
    val flexaViewModel = koinViewModel<FlexaViewModel>()
    val navigationRouter = koinInject<NavigationRouter>()
    val sheetStateManager = LocalSheetStateManager.current
    val applicationStateProvider = koinInject<ApplicationStateProvider>()

    // Helper properties for triggering the system security UI from callbacks
    val (exportPrivateDataAuthentication, setExportPrivateDataAuthentication) =
        rememberSaveable { mutableStateOf(false) }
    val (deleteWalletAuthentication, setDeleteWalletAuthentication) =
        rememberSaveable { mutableStateOf(false) }

    val getHomeMessage = koinInject<GetHomeMessageUseCase>()
    // hook up for collection
    getHomeMessage.observe().collectAsStateWithLifecycle()

    val navigator: Navigator =
        remember(
            navController,
            flexaViewModel,
            keyboardManager,
            sheetStateManager,
            applicationStateProvider
        ) {
            NavigatorImpl(
                activity = this@Navigation,
                navController = navController,
                flexaViewModel = flexaViewModel,
                keyboardManager = keyboardManager,
                sheetStateManager = sheetStateManager,
                applicationStateProvider = applicationStateProvider
            )
        }

    LaunchedEffect(Unit) {
        navigationRouter.observePipeline().collect {
            navigator.executeCommand(it)
        }
    }

    NavHost(
        navController = navController,
        startDestination = Home,
        enterTransition = { enterTransition() },
        exitTransition = { exitTransition() },
        popEnterTransition = { popEnterTransition() },
        popExitTransition = { popExitTransition() }
    ) {
        composable<Home> { NavigationHome(navController) }
        composable(SETTINGS) { WrapSettings() }
        composable<AdvancedSettingsArgs> {
            AdvancedSettingsScreen(
                goExportPrivateData = {
                    navController.checkProtectedDestination(
                        scope = lifecycleScope,
                        propertyToCheck = authenticationViewModel.isExportPrivateDataAuthenticationRequired,
                        setCheckedProperty = setExportPrivateDataAuthentication,
                        unProtectedDestination = EXPORT_PRIVATE_DATA
                    )
                },
                goDeleteWallet = {
                    navController.checkProtectedDestination(
                        scope = lifecycleScope,
                        propertyToCheck = authenticationViewModel.isDeleteWalletAuthenticationRequired,
                        setCheckedProperty = setDeleteWalletAuthentication,
                        unProtectedDestination = DELETE_WALLET
                    )
                },
            )

            when {
                deleteWalletAuthentication -> {
                    ShowSystemAuthentication(
                        navHostController = navController,
                        protectedDestination = DELETE_WALLET,
                        protectedUseCase = AuthenticationUseCase.DeleteWallet,
                        setCheckedProperty = setDeleteWalletAuthentication,
                        navigationRouter = navigationRouter
                    )
                }

                exportPrivateDataAuthentication -> {
                    ShowSystemAuthentication(
                        navHostController = navController,
                        protectedDestination = EXPORT_PRIVATE_DATA,
                        protectedUseCase = AuthenticationUseCase.ExportPrivateData,
                        setCheckedProperty = setExportPrivateDataAuthentication,
                        navigationRouter = navigationRouter
                    )
                }
            }
        }
        composable<ChooseServerArgs> { ChooseServerScreen() }
        composable<WalletBackup> { AndroidWalletBackup(it.toRoute()) }
        composable<FeedbackArgs> { FeedbackScreen() }
        composable(DELETE_WALLET) {
            WrapDeleteWallet(
                goBack = {
                    setDeleteWalletAuthentication(false)
                    navController.popBackStackJustOnce(DELETE_WALLET)
                },
                onConfirm = {
                    setDeleteWalletAuthentication(false)
                    navController.popBackStackJustOnce(DELETE_WALLET)
                }
            )
        }
        composable<AboutArgs> { AboutScreen() }
        composable(WHATS_NEW) { WrapWhatsNew() }
        dialogComposable<IntegrationsArgs> { IntegrationsScreen() }
        composable<ExchangeRateSettingsArgs> { ExchangeRateSettingsScreen() }
        composable(CRASH_REPORTING_OPT_IN) { AndroidCrashReportingOptIn() }
        composable<ScanKeystoneSignInRequest> { WrapScanKeystoneSignInRequest() }
        composable<ScanKeystonePCZTRequest> { WrapScanKeystonePCZTRequest() }
        composable<SignKeystoneTransactionArgs> { SignKeystoneTransactionScreen() }
        dialogComposable<AccountList> { AndroidAccountList() }
        composable<ScanArgs> { ScanZashiAddressScreen(it.toRoute()) }
        composable(EXPORT_PRIVATE_DATA) {
            WrapExportPrivateData(
                goBack = {
                    setExportPrivateDataAuthentication(false)
                    navController.popBackStackJustOnce(EXPORT_PRIVATE_DATA)
                },
                onConfirm = {
                    setExportPrivateDataAuthentication(false)
                    navController.popBackStackJustOnce(EXPORT_PRIVATE_DATA)
                }
            )
        }
        composable(NOT_ENOUGH_SPACE) {
            WrapNotEnoughSpace(
                goPrevious = { navController.popBackStackJustOnce(NOT_ENOUGH_SPACE) },
                goSettings = { navController.navigateJustOnce(SETTINGS) }
            )
        }
        composable<AddressBookArgs> { AddressBookScreen() }
        composable<SelectABRecipientArgs> { SelectABRecipientScreen() }
        composable<AddZashiABContactArgs> { AddZashiABContactScreen(it.toRoute()) }
        composable(
            route = "$QR_CODE/{$ADDRESS_TYPE}",
            arguments = listOf(navArgument(ADDRESS_TYPE) { type = NavType.IntType })
        ) { backStackEntry ->
            val addressType = backStackEntry.arguments?.getInt(ADDRESS_TYPE) ?: ReceiveAddressType.Unified.ordinal
            QrCodeScreen(addressType)
        }
        composable(
            route = "$REQUEST/{$ADDRESS_TYPE}",
            arguments = listOf(navArgument(ADDRESS_TYPE) { type = NavType.IntType })
        ) { backStackEntry ->
            val addressType = backStackEntry.arguments?.getInt(ADDRESS_TYPE) ?: ReceiveAddressType.Unified.ordinal
            RequestScreen(addressType)
        }
        composable<ConnectKeystone> { AndroidConnectKeystone() }
        composable<SelectKeystoneAccount> { AndroidSelectKeystoneAccount(it.toRoute()) }
        composable<ReviewTransactionArgs> { AndroidReviewTransaction() }
        composable<TransactionProgressArgs> { TransactionProgressScreen(it.toRoute()) }
        composable<ActivityHistoryArgs> { ActivityHistoryScreen() }
        dialogComposable<TransactionFiltersArgs> { TransactionFiltersScreen() }
        composable<TransactionDetailArgs> { TransactionDetailScreen(it.toRoute()) }
        dialogComposable<TransactionNote> { AndroidTransactionNote(it.toRoute()) }
        composable<TaxExport> { AndroidTaxExport() }
        composable<ReceiveArgs> { ReceiveScreen() }
        composable<Send> { WrapSend(it.toRoute()) }
        dialogComposable<SeedInfo> { AndroidSeedInfo() }
        composable<WalletBackupDetail> { AndroidWalletBackupDetail(it.toRoute()) }
        dialogComposable<SeedBackupInfo> { AndroidWalletBackupInfo() }
        dialogComposable<ShieldFundsInfo> { AndroidShieldFundsInfo() }
        dialogComposable<WalletDisconnectedInfo> { AndroidWalletDisconnectedInfo() }
        dialogComposable<WalletRestoringInfo> { AndroidWalletRestoringInfo() }
        dialogComposable<WalletSyncingInfo> { AndroidWalletSyncingInfo() }
        dialogComposable<WalletUpdatingInfo> { AndroidWalletUpdatingInfo() }
        dialogComposable<ErrorDialog> { AndroidErrorDialog() }
        dialogComposable<ErrorBottomSheet> { AndroidErrorBottomSheet() }
        dialogComposable<SpendableBalanceArgs> { SpendableBalanceScreen() }
        composable<CrashReportOptIn> { AndroidCrashReportOptIn() }
        composable<ThirdPartyScan> { AndroidThirdPartyScan() }
        dialogComposable<SwapAssetPickerArgs> { SwapAssetPickerScreen(it.toRoute()) }
        dialogComposable<SwapBlockchainPickerArgs> { SwapBlockchainPickerScreen(it.toRoute()) }
        composable<SwapArgs> { SwapScreen() }
        dialogComposable<SwapSlippageArgs> { SwapSlippageScreen(it.toRoute()) }
        dialogComposable<SwapInfoArgs> { SwapInfoScreen() }
        dialogComposable<SwapQuoteArgs> { SwapQuoteScreen() }
        composable<ScanGenericAddressArgs> { ScanGenericAddressScreen(it.toRoute()) }
        composable<SelectABSwapRecipientArgs> { SelectSwapABRecipientScreen(it.toRoute()) }
        composable<AddSwapABContactArgs> { AddSwapABContactScreen(it.toRoute()) }
        composable<AddGenericABContactArgs> { AddGenericABContactScreen(it.toRoute()) }
        composable<UpdateGenericABContactArgs> { UpdateGenericABContactScreen(it.toRoute()) }
        composable<TorSettingsArgs> { TorSettingsScreen() }
        composable<TorOptInArgs> { TorOptInScreen() }
        dialogComposable<ShieldedAddressInfoArgs> { ShieldedAddressInfoScreen() }
        dialogComposable<TransparentAddressInfoArgs> { TransparentAddressInfoScreen() }
        composable<ExchangeRateOptInArgs> { ExchangeRateOptInScreen() }
        composable<PayArgs> { PayScreen() }
        dialogComposable<PayInfoArgs> { PayInfoScreen() }
        composable<ORSwapConfirmationArgs> { ORSwapConfirmationScreen() }
        composable<SwapDetailArgs> { SwapDetailScreen(it.toRoute()) }
        dialogComposable<SwapRefundAddressInfoArgs> { SwapRefundAddressInfoScreen() }
    }
}

private inline fun <reified T : Any> NavGraphBuilder.dialogComposable(
    noinline content: @Composable (NavBackStackEntry) -> Unit
) {
    this.dialog<T>(
        typeMap = emptyMap(),
        deepLinks = emptyList(),
        dialogProperties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        content = content
    )
}

/**
 * This is the Home screens sub-navigation. We could consider creating a separate sub-navigation graph.
 */
@Composable
private fun MainActivity.NavigationHome(navController: NavHostController) {
    AndroidHome()
    val isEnoughSpace by storageCheckViewModel.isEnoughSpace.collectAsStateWithLifecycle()
    if (isEnoughSpace == false) {
        Twig.info { "Not enough free space" }
        navController.navigateJustOnce(NOT_ENOUGH_SPACE)
    }
}

@Composable
private fun MainActivity.ShowSystemAuthentication(
    navHostController: NavHostController,
    navigationRouter: NavigationRouter,
    protectedDestination: String,
    protectedUseCase: AuthenticationUseCase,
    setCheckedProperty: (Boolean) -> Unit,
) {
    WrapAuthentication(
        goSupport = {
            setCheckedProperty(false)
            navigationRouter.forward(FeedbackArgs)
        },
        onSuccess = {
            navHostController.navigateJustOnce(protectedDestination)
        },
        onCancel = {
            setCheckedProperty(false)
        },
        onFail = {
            // No action needed
        },
        useCase = protectedUseCase
    )
}

/**
 * Check and trigger authentication if required, navigate to the destination otherwise
 */
private fun NavHostController.checkProtectedDestination(
    scope: LifecycleCoroutineScope,
    propertyToCheck: StateFlow<Boolean?>,
    setCheckedProperty: (Boolean) -> Unit,
    unProtectedDestination: String
) {
    scope.launch {
        propertyToCheck
            .filterNotNull()
            .collect { isProtected ->
                if (isProtected) {
                    setCheckedProperty(true)
                } else {
                    navigateJustOnce(unProtectedDestination)
                }
            }
    }
}

fun NavHostController.navigateJustOnce(
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
fun NavHostController.popBackStackJustOnce(currentRouteToBePopped: String) {
    if (currentDestination?.route != currentRouteToBePopped) {
        return
    }
    popBackStack()
}

object NavigationTargets {
    const val DELETE_WALLET = "delete_wallet"
    const val EXPORT_PRIVATE_DATA = "export_private_data"
    const val NOT_ENOUGH_SPACE = "not_enough_space"
    const val QR_CODE = "qr_code"
    const val REQUEST = "request"
    const val SETTINGS = "settings"
    const val WHATS_NEW = "whats_new"
    const val CRASH_REPORTING_OPT_IN = "crash_reporting_opt_in"
}

object NavigationArgs {
    const val ADDRESS_TYPE = "addressType"
}
