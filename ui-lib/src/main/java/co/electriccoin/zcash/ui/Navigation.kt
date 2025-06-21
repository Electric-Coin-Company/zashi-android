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
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.spackle.getSerializableCompat
import co.electriccoin.zcash.ui.NavigationArgs.ADDRESS_TYPE
import co.electriccoin.zcash.ui.NavigationTargets.ABOUT
import co.electriccoin.zcash.ui.NavigationTargets.ADVANCED_SETTINGS
import co.electriccoin.zcash.ui.NavigationTargets.CHOOSE_SERVER
import co.electriccoin.zcash.ui.NavigationTargets.CRASH_REPORTING_OPT_IN
import co.electriccoin.zcash.ui.NavigationTargets.DELETE_WALLET
import co.electriccoin.zcash.ui.NavigationTargets.EXPORT_PRIVATE_DATA
import co.electriccoin.zcash.ui.NavigationTargets.NOT_ENOUGH_SPACE
import co.electriccoin.zcash.ui.NavigationTargets.QR_CODE
import co.electriccoin.zcash.ui.NavigationTargets.REQUEST
import co.electriccoin.zcash.ui.NavigationTargets.SETTINGS
import co.electriccoin.zcash.ui.NavigationTargets.SUPPORT
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
import co.electriccoin.zcash.ui.screen.about.WrapAbout
import co.electriccoin.zcash.ui.screen.accountlist.AccountList
import co.electriccoin.zcash.ui.screen.accountlist.AndroidAccountList
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookArgs
import co.electriccoin.zcash.ui.screen.addressbook.WrapAddressBook
import co.electriccoin.zcash.ui.screen.advancedsettings.WrapAdvancedSettings
import co.electriccoin.zcash.ui.screen.authentication.AuthenticationUseCase
import co.electriccoin.zcash.ui.screen.authentication.WrapAuthentication
import co.electriccoin.zcash.ui.screen.balances.spendable.AndroidSpendableBalance
import co.electriccoin.zcash.ui.screen.balances.spendable.SpendableBalance
import co.electriccoin.zcash.ui.screen.chooseserver.WrapChooseServer
import co.electriccoin.zcash.ui.screen.connectkeystone.AndroidConnectKeystone
import co.electriccoin.zcash.ui.screen.connectkeystone.ConnectKeystone
import co.electriccoin.zcash.ui.screen.contact.AddContactArgs
import co.electriccoin.zcash.ui.screen.contact.UpdateContactArgs
import co.electriccoin.zcash.ui.screen.contact.WrapAddContact
import co.electriccoin.zcash.ui.screen.contact.WrapUpdateContact
import co.electriccoin.zcash.ui.screen.crashreporting.AndroidCrashReportingOptIn
import co.electriccoin.zcash.ui.screen.deletewallet.WrapDeleteWallet
import co.electriccoin.zcash.ui.screen.error.AndroidErrorBottomSheet
import co.electriccoin.zcash.ui.screen.error.AndroidErrorDialog
import co.electriccoin.zcash.ui.screen.error.ErrorBottomSheet
import co.electriccoin.zcash.ui.screen.error.ErrorDialog
import co.electriccoin.zcash.ui.screen.exchangerate.optin.AndroidExchangeRateOptIn
import co.electriccoin.zcash.ui.screen.exchangerate.optin.ExchangeRateOptIn
import co.electriccoin.zcash.ui.screen.exchangerate.settings.AndroidExchangeRateSettings
import co.electriccoin.zcash.ui.screen.exchangerate.settings.ExchangeRateSettings
import co.electriccoin.zcash.ui.screen.exportdata.WrapExportPrivateData
import co.electriccoin.zcash.ui.screen.feedback.WrapFeedback
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
import co.electriccoin.zcash.ui.screen.integrations.AndroidDialogIntegrations
import co.electriccoin.zcash.ui.screen.integrations.AndroidIntegrations
import co.electriccoin.zcash.ui.screen.integrations.DialogIntegrations
import co.electriccoin.zcash.ui.screen.integrations.Integrations
import co.electriccoin.zcash.ui.screen.qrcode.WrapQrCode
import co.electriccoin.zcash.ui.screen.receive.AndroidReceive
import co.electriccoin.zcash.ui.screen.receive.Receive
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveAddressType
import co.electriccoin.zcash.ui.screen.request.WrapRequest
import co.electriccoin.zcash.ui.screen.restore.info.AndroidSeedInfo
import co.electriccoin.zcash.ui.screen.restore.info.SeedInfo
import co.electriccoin.zcash.ui.screen.reviewtransaction.AndroidReviewTransaction
import co.electriccoin.zcash.ui.screen.reviewtransaction.ReviewTransaction
import co.electriccoin.zcash.ui.screen.scan.Scan
import co.electriccoin.zcash.ui.screen.scan.WrapScanValidator
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
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.AndroidSignKeystoneTransaction
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.SignKeystoneTransaction
import co.electriccoin.zcash.ui.screen.swap.SwapAmount
import co.electriccoin.zcash.ui.screen.swap.SwapScreen
import co.electriccoin.zcash.ui.screen.swap.near.NearInfoArgs
import co.electriccoin.zcash.ui.screen.swap.near.NearInfoScreen
import co.electriccoin.zcash.ui.screen.swap.picker.SwapAssetPicker
import co.electriccoin.zcash.ui.screen.swap.picker.SwapAssetPickerScreen
import co.electriccoin.zcash.ui.screen.swap.slippage.SwapSlippageArgs
import co.electriccoin.zcash.ui.screen.swap.slippage.SwapSlippageScreen
import co.electriccoin.zcash.ui.screen.taxexport.AndroidTaxExport
import co.electriccoin.zcash.ui.screen.taxexport.TaxExport
import co.electriccoin.zcash.ui.screen.transactiondetail.AndroidTransactionDetail
import co.electriccoin.zcash.ui.screen.transactiondetail.TransactionDetail
import co.electriccoin.zcash.ui.screen.transactionfilters.AndroidTransactionFiltersList
import co.electriccoin.zcash.ui.screen.transactionfilters.TransactionFilters
import co.electriccoin.zcash.ui.screen.transactionhistory.AndroidTransactionHistory
import co.electriccoin.zcash.ui.screen.transactionhistory.TransactionHistory
import co.electriccoin.zcash.ui.screen.transactionnote.AndroidTransactionNote
import co.electriccoin.zcash.ui.screen.transactionnote.TransactionNote
import co.electriccoin.zcash.ui.screen.transactionprogress.AndroidTransactionProgress
import co.electriccoin.zcash.ui.screen.transactionprogress.TransactionProgress
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
        composable<Home> {
            NavigationHome(navController)
        }
        composable(SETTINGS) {
            WrapSettings()
        }
        composable(ADVANCED_SETTINGS) {
            WrapAdvancedSettings(
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
                        setCheckedProperty = setDeleteWalletAuthentication
                    )
                }

                exportPrivateDataAuthentication -> {
                    ShowSystemAuthentication(
                        navHostController = navController,
                        protectedDestination = EXPORT_PRIVATE_DATA,
                        protectedUseCase = AuthenticationUseCase.ExportPrivateData,
                        setCheckedProperty = setExportPrivateDataAuthentication
                    )
                }
            }
        }
        composable(CHOOSE_SERVER) {
            WrapChooseServer()
        }
        composable<WalletBackup> {
            AndroidWalletBackup(it.toRoute())
        }
        composable(SUPPORT) {
            // Pop back stack won't be right if we deep link into support
            WrapFeedback()
        }
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
        composable(ABOUT) {
            WrapAbout(
                goBack = { navController.popBackStackJustOnce(ABOUT) },
            )
        }
        composable(WHATS_NEW) {
            WrapWhatsNew()
        }
        composable<Integrations> {
            AndroidIntegrations()
        }
        dialog<DialogIntegrations> {
            AndroidDialogIntegrations()
        }
        composable<ExchangeRateOptIn> {
            AndroidExchangeRateOptIn()
        }
        composable<ExchangeRateSettings> {
            AndroidExchangeRateSettings()
        }
        composable(CRASH_REPORTING_OPT_IN) {
            AndroidCrashReportingOptIn()
        }
        composable<ScanKeystoneSignInRequest> {
            WrapScanKeystoneSignInRequest()
        }
        composable<ScanKeystonePCZTRequest> {
            WrapScanKeystonePCZTRequest()
        }
        composable<SignKeystoneTransaction> {
            AndroidSignKeystoneTransaction()
        }
        dialog<AccountList>(
            dialogProperties =
                DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                )
        ) {
            AndroidAccountList()
        }
        composable<Scan> {
            WrapScanValidator(it.toRoute())
        }
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
        composable(
            route = AddressBookArgs.ROUTE,
            arguments =
                listOf(
                    navArgument(AddressBookArgs.MODE) {
                        defaultValue = AddressBookArgs.DEFAULT
                        type = NavType.EnumType(AddressBookArgs::class.java)
                    }
                )
        ) { backStackEntry ->
            val args =
                backStackEntry.arguments
                    ?.getSerializableCompat<AddressBookArgs>(AddressBookArgs.MODE) ?: AddressBookArgs.DEFAULT

            WrapAddressBook(args)
        }
        composable(
            route = AddContactArgs.ROUTE,
            arguments =
                listOf(
                    navArgument(AddContactArgs.ADDRESS) {
                        nullable = true
                        defaultValue = null
                        type = NavType.StringType
                    }
                )
        ) { backStackEntry ->
            val address = backStackEntry.arguments?.getString(AddContactArgs.ADDRESS)
            WrapAddContact(address)
        }
        composable(
            route = UpdateContactArgs.ROUTE,
            arguments = listOf(navArgument(UpdateContactArgs.CONTACT_ADDRESS) { type = NavType.StringType })
        ) { backStackEntry ->
            val contactAddress = backStackEntry.arguments?.getString(UpdateContactArgs.CONTACT_ADDRESS).orEmpty()
            WrapUpdateContact(contactAddress)
        }
        composable(
            route = "$QR_CODE/{$ADDRESS_TYPE}",
            arguments = listOf(navArgument(ADDRESS_TYPE) { type = NavType.IntType })
        ) { backStackEntry ->
            val addressType = backStackEntry.arguments?.getInt(ADDRESS_TYPE) ?: ReceiveAddressType.Unified.ordinal
            WrapQrCode(addressType)
        }
        composable(
            route = "$REQUEST/{$ADDRESS_TYPE}",
            arguments = listOf(navArgument(ADDRESS_TYPE) { type = NavType.IntType })
        ) { backStackEntry ->
            val addressType = backStackEntry.arguments?.getInt(ADDRESS_TYPE) ?: ReceiveAddressType.Unified.ordinal
            WrapRequest(addressType)
        }
        composable<ConnectKeystone> {
            AndroidConnectKeystone()
        }
        composable<SelectKeystoneAccount> {
            AndroidSelectKeystoneAccount(it.toRoute())
        }
        composable<ReviewTransaction> {
            AndroidReviewTransaction()
        }
        composable<TransactionProgress> {
            AndroidTransactionProgress(it.toRoute())
        }
        composable<TransactionHistory> {
            AndroidTransactionHistory()
        }
        dialog<TransactionFilters>(
            dialogProperties =
                DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
        ) {
            AndroidTransactionFiltersList()
        }
        composable<TransactionDetail> {
            AndroidTransactionDetail(it.toRoute())
        }
        dialog<TransactionNote>(
            dialogProperties =
                DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                )
        ) {
            AndroidTransactionNote(it.toRoute())
        }
        composable<TaxExport> {
            AndroidTaxExport()
        }
        composable<Receive> {
            AndroidReceive()
        }
        composable<Send> {
            WrapSend(it.toRoute())
        }
        dialog<SeedInfo>(
            dialogProperties =
                DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                )
        ) {
            AndroidSeedInfo()
        }
        composable<WalletBackupDetail> {
            AndroidWalletBackupDetail(it.toRoute())
        }
        dialog<SeedBackupInfo>(
            dialogProperties =
                DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
        ) {
            AndroidWalletBackupInfo()
        }
        dialog<ShieldFundsInfo>(
            dialogProperties =
                DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
        ) {
            AndroidShieldFundsInfo()
        }
        dialog<WalletDisconnectedInfo>(
            dialogProperties =
                DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
        ) {
            AndroidWalletDisconnectedInfo()
        }
        dialog<WalletRestoringInfo>(
            dialogProperties =
                DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
        ) {
            AndroidWalletRestoringInfo()
        }
        dialog<WalletSyncingInfo>(
            dialogProperties =
                DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
        ) {
            AndroidWalletSyncingInfo()
        }
        dialog<WalletUpdatingInfo>(
            dialogProperties =
                DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
        ) {
            AndroidWalletUpdatingInfo()
        }
        dialog<ErrorDialog>(
            dialogProperties =
                DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
        ) {
            AndroidErrorDialog()
        }
        dialog<ErrorBottomSheet>(
            dialogProperties =
                DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
        ) {
            AndroidErrorBottomSheet()
        }
        dialog<SpendableBalance>(
            dialogProperties =
                DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
        ) {
            AndroidSpendableBalance()
        }
        composable<CrashReportOptIn> { AndroidCrashReportOptIn() }
        composable<ThirdPartyScan> { AndroidThirdPartyScan() }
        dialog<SwapAssetPicker>(
            dialogProperties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) { SwapAssetPickerScreen() }
        composable<SwapAmount> { SwapScreen() }
        dialog<SwapSlippageArgs>(
            dialogProperties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) { SwapSlippageScreen(it.toRoute()) }
        dialog<NearInfoArgs>(
            dialogProperties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) { NearInfoScreen() }
    }
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
    protectedDestination: String,
    protectedUseCase: AuthenticationUseCase,
    setCheckedProperty: (Boolean) -> Unit,
) {
    WrapAuthentication(
        goSupport = {
            setCheckedProperty(false)
            navHostController.navigateJustOnce(SUPPORT)
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
    const val ABOUT = "about"
    const val ADVANCED_SETTINGS = "advanced_settings"
    const val DELETE_WALLET = "delete_wallet"
    const val EXPORT_PRIVATE_DATA = "export_private_data"
    const val CHOOSE_SERVER = "choose_server"
    const val NOT_ENOUGH_SPACE = "not_enough_space"
    const val QR_CODE = "qr_code"
    const val REQUEST = "request"
    const val SETTINGS = "settings"
    const val SUPPORT = "support"
    const val WHATS_NEW = "whats_new"
    const val CRASH_REPORTING_OPT_IN = "crash_reporting_opt_in"
}

object NavigationArgs {
    const val ADDRESS_TYPE = "addressType"
}
