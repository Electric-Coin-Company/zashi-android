package co.electriccoin.zcash.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.ZecSend
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.spackle.getSerializableCompat
import co.electriccoin.zcash.ui.NavigationArgs.ADDRESS_TYPE
import co.electriccoin.zcash.ui.NavigationArgs.ZIP321_URI
import co.electriccoin.zcash.ui.NavigationArguments.MULTIPLE_SUBMISSION_CLEAR_FORM
import co.electriccoin.zcash.ui.NavigationArguments.SEND_CONFIRM_AMOUNT
import co.electriccoin.zcash.ui.NavigationArguments.SEND_CONFIRM_INITIAL_STAGE
import co.electriccoin.zcash.ui.NavigationArguments.SEND_CONFIRM_MEMO
import co.electriccoin.zcash.ui.NavigationArguments.SEND_CONFIRM_PROPOSAL
import co.electriccoin.zcash.ui.NavigationArguments.SEND_CONFIRM_RECIPIENT_ADDRESS
import co.electriccoin.zcash.ui.NavigationArguments.SEND_SCAN_RECIPIENT_ADDRESS
import co.electriccoin.zcash.ui.NavigationArguments.SEND_SCAN_ZIP_321_URI
import co.electriccoin.zcash.ui.NavigationTargets.ABOUT
import co.electriccoin.zcash.ui.NavigationTargets.ADVANCED_SETTINGS
import co.electriccoin.zcash.ui.NavigationTargets.CHOOSE_SERVER
import co.electriccoin.zcash.ui.NavigationTargets.DELETE_WALLET
import co.electriccoin.zcash.ui.NavigationTargets.EXCHANGE_RATE_OPT_IN
import co.electriccoin.zcash.ui.NavigationTargets.EXPORT_PRIVATE_DATA
import co.electriccoin.zcash.ui.NavigationTargets.HOME
import co.electriccoin.zcash.ui.NavigationTargets.INTEGRATIONS
import co.electriccoin.zcash.ui.NavigationTargets.NOT_ENOUGH_SPACE
import co.electriccoin.zcash.ui.NavigationTargets.PAYMENT_REQUEST
import co.electriccoin.zcash.ui.NavigationTargets.QR_CODE
import co.electriccoin.zcash.ui.NavigationTargets.REQUEST
import co.electriccoin.zcash.ui.NavigationTargets.SEED_RECOVERY
import co.electriccoin.zcash.ui.NavigationTargets.SEND_CONFIRMATION
import co.electriccoin.zcash.ui.NavigationTargets.SETTINGS
import co.electriccoin.zcash.ui.NavigationTargets.SETTINGS_EXCHANGE_RATE_OPT_IN
import co.electriccoin.zcash.ui.NavigationTargets.SUPPORT
import co.electriccoin.zcash.ui.NavigationTargets.WHATS_NEW
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.model.SerializableAddress
import co.electriccoin.zcash.ui.configuration.ConfigurationEntries
import co.electriccoin.zcash.ui.configuration.RemoteConfig
import co.electriccoin.zcash.ui.design.animation.ScreenAnimation.enterTransition
import co.electriccoin.zcash.ui.design.animation.ScreenAnimation.exitTransition
import co.electriccoin.zcash.ui.design.animation.ScreenAnimation.popEnterTransition
import co.electriccoin.zcash.ui.design.animation.ScreenAnimation.popExitTransition
import co.electriccoin.zcash.ui.screen.about.WrapAbout
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookArgs
import co.electriccoin.zcash.ui.screen.addressbook.WrapAddressBook
import co.electriccoin.zcash.ui.screen.advancedsettings.WrapAdvancedSettings
import co.electriccoin.zcash.ui.screen.authentication.AuthenticationUseCase
import co.electriccoin.zcash.ui.screen.authentication.WrapAuthentication
import co.electriccoin.zcash.ui.screen.chooseserver.WrapChooseServer
import co.electriccoin.zcash.ui.screen.contact.AddContactArgs
import co.electriccoin.zcash.ui.screen.contact.UpdateContactArgs
import co.electriccoin.zcash.ui.screen.contact.WrapAddContact
import co.electriccoin.zcash.ui.screen.contact.WrapUpdateContact
import co.electriccoin.zcash.ui.screen.deletewallet.WrapDeleteWallet
import co.electriccoin.zcash.ui.screen.disconnected.WrapDisconnected
import co.electriccoin.zcash.ui.screen.exchangerate.optin.AndroidExchangeRateOptIn
import co.electriccoin.zcash.ui.screen.exchangerate.settings.AndroidSettingsExchangeRateOptIn
import co.electriccoin.zcash.ui.screen.exportdata.WrapExportPrivateData
import co.electriccoin.zcash.ui.screen.home.WrapHome
import co.electriccoin.zcash.ui.screen.integrations.WrapIntegrations
import co.electriccoin.zcash.ui.screen.paymentrequest.WrapPaymentRequest
import co.electriccoin.zcash.ui.screen.qrcode.WrapQrCode
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveAddressType
import co.electriccoin.zcash.ui.screen.request.WrapRequest
import co.electriccoin.zcash.ui.screen.scan.ScanNavigationArgs
import co.electriccoin.zcash.ui.screen.scan.WrapScanValidator
import co.electriccoin.zcash.ui.screen.seedrecovery.WrapSeedRecovery
import co.electriccoin.zcash.ui.screen.send.ext.toSerializableAddress
import co.electriccoin.zcash.ui.screen.send.model.SendArguments
import co.electriccoin.zcash.ui.screen.sendconfirmation.WrapSendConfirmation
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SendConfirmationArguments
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SendConfirmationStage
import co.electriccoin.zcash.ui.screen.settings.WrapSettings
import co.electriccoin.zcash.ui.screen.support.WrapSupport
import co.electriccoin.zcash.ui.screen.update.WrapCheckForUpdate
import co.electriccoin.zcash.ui.screen.warning.WrapNotEnoughSpace
import co.electriccoin.zcash.ui.screen.whatsnew.WrapWhatsNew
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

// TODO [#1297]: Consider: Navigation passing complex data arguments different way
// TODO [#1297]: https://github.com/Electric-Coin-Company/zashi-android/issues/1297

@Composable
@Suppress("LongMethod")
internal fun MainActivity.Navigation() {
    val navController = LocalNavController.current

    // Helper properties for triggering the system security UI from callbacks
    val (exportPrivateDataAuthentication, setExportPrivateDataAuthentication) =
        rememberSaveable { mutableStateOf(false) }
    val (seedRecoveryAuthentication, setSeedRecoveryAuthentication) =
        rememberSaveable { mutableStateOf(false) }
    val (deleteWalletAuthentication, setDeleteWalletAuthentication) =
        rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        walletViewModel.navigationCommand.collect {
            navController.navigateJustOnce(it)
        }
    }

    LaunchedEffect(Unit) {
        walletViewModel.backNavigationCommand.collect {
            navController.popBackStack()
        }
    }

    NavHost(
        navController = navController,
        startDestination = HOME,
        enterTransition = { enterTransition() },
        exitTransition = { exitTransition() },
        popEnterTransition = { popEnterTransition() },
        popExitTransition = { popExitTransition() }
    ) {
        composable(HOME) { backStack ->
            NavigationHome(navController, backStack)
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
                goSeedRecovery = {
                    navController.checkProtectedDestination(
                        scope = lifecycleScope,
                        propertyToCheck = authenticationViewModel.isSeedAuthenticationRequired,
                        setCheckedProperty = setSeedRecoveryAuthentication,
                        unProtectedDestination = SEED_RECOVERY
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

                seedRecoveryAuthentication -> {
                    ShowSystemAuthentication(
                        navHostController = navController,
                        protectedDestination = SEED_RECOVERY,
                        protectedUseCase = AuthenticationUseCase.SeedRecovery,
                        setCheckedProperty = setSeedRecoveryAuthentication
                    )
                }
            }
        }
        composable(CHOOSE_SERVER) {
            WrapChooseServer()
        }
        composable(SEED_RECOVERY) {
            WrapSeedRecovery(
                goBack = {
                    setSeedRecoveryAuthentication(false)
                    navController.popBackStackJustOnce(SEED_RECOVERY)
                },
                onDone = {
                    setSeedRecoveryAuthentication(false)
                    navController.popBackStackJustOnce(SEED_RECOVERY)
                },
            )
        }
        composable(SUPPORT) {
            // Pop back stack won't be right if we deep link into support
            WrapSupport(goBack = { navController.popBackStackJustOnce(SUPPORT) })
        }
        composable(DELETE_WALLET) {
            WrapDeleteWallet(
                goBack = {
                    setDeleteWalletAuthentication(false)
                    navController.popBackStackJustOnce(DELETE_WALLET)
                }
            )
        }
        composable(ABOUT) {
            WrapAbout(
                goBack = { navController.popBackStackJustOnce(ABOUT) },
                goWhatsNew = { navController.navigateJustOnce(WHATS_NEW) }
            )
        }
        composable(WHATS_NEW) {
            WrapWhatsNew()
        }
        composable(INTEGRATIONS) {
            WrapIntegrations()
        }
        composable(EXCHANGE_RATE_OPT_IN) {
            AndroidExchangeRateOptIn()
        }
        composable(SETTINGS_EXCHANGE_RATE_OPT_IN) {
            AndroidSettingsExchangeRateOptIn()
        }
        composable(
            route = ScanNavigationArgs.ROUTE,
            arguments =
                listOf(
                    navArgument(ScanNavigationArgs.KEY) {
                        type = NavType.EnumType(ScanNavigationArgs::class.java)
                        defaultValue = ScanNavigationArgs.DEFAULT
                    }
                )
        ) { backStackEntry ->
            val mode =
                backStackEntry.arguments
                    ?.getSerializableCompat<ScanNavigationArgs>(ScanNavigationArgs.KEY) ?: ScanNavigationArgs.DEFAULT

            WrapScanValidator(args = mode)
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
        composable(route = SEND_CONFIRMATION) {
            navController.previousBackStackEntry?.let { backStackEntry ->
                WrapSendConfirmation(
                    goBack = { clearForm ->
                        navController.previousBackStackEntry?.savedStateHandle?.apply {
                            set(MULTIPLE_SUBMISSION_CLEAR_FORM, clearForm)
                        }
                        navController.popBackStackJustOnce(SEND_CONFIRMATION)
                    },
                    goHome = { navController.navigateJustOnce(HOME) },
                    goSupport = { navController.navigateJustOnce(SUPPORT) },
                    arguments = SendConfirmationArguments.fromSavedStateHandle(backStackEntry.savedStateHandle)
                )
            }
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
        composable(PAYMENT_REQUEST) {
            navController.previousBackStackEntry?.let { backStackEntry ->
                WrapPaymentRequest(
                    arguments = SendConfirmationArguments.fromSavedStateHandle(backStackEntry.savedStateHandle)
                )
            }
        }
    }
}

/**
 * This is the Home screens sub-navigation. We could consider creating a separate sub-navigation graph.
 */
@Composable
private fun MainActivity.NavigationHome(
    navController: NavHostController,
    backStack: NavBackStackEntry
) {
    WrapHome(
        goScan = { navController.navigateJustOnce(ScanNavigationArgs(ScanNavigationArgs.DEFAULT)) },
        goSendConfirmation = { zecSend ->
            navController.currentBackStackEntry?.savedStateHandle?.let { handle ->
                fillInHandleForConfirmation(handle, zecSend, SendConfirmationStage.Confirmation)
            }
            navController.navigateJustOnce(SEND_CONFIRMATION)
        },
        goPaymentRequest = { zecSend ->
            navController.currentBackStackEntry?.savedStateHandle?.apply {
                this[SEND_CONFIRM_RECIPIENT_ADDRESS] =
                    Json.encodeToString(SerializableAddress.serializer(), zecSend.destination.toSerializableAddress())
                this[SEND_CONFIRM_AMOUNT] = zecSend.amount.value
                this[SEND_CONFIRM_MEMO] = zecSend.memo.value
                this[SEND_CONFIRM_PROPOSAL] = zecSend.proposal?.toByteArray()
            }
            navController.navigateJustOnce(PAYMENT_REQUEST)
        },
        goSettings = { navController.navigateJustOnce(SETTINGS) },
        goMultiTrxSubmissionFailure = {
            // Ultimately we could approach reworking the MultipleTrxFailure screen into a separate
            // navigation endpoint
            navController.currentBackStackEntry?.savedStateHandle?.let { handle ->
                fillInHandleForConfirmation(handle, null, SendConfirmationStage.MultipleTrxFailure)
            }
            navController.navigateJustOnce(SEND_CONFIRMATION)
        },
        sendArguments =
            SendArguments(
                recipientAddress =
                    backStack.savedStateHandle.get<String>(SEND_SCAN_RECIPIENT_ADDRESS)?.let {
                        Json.decodeFromString<SerializableAddress>(it).toRecipient()
                    },
                zip321Uri = backStack.savedStateHandle.get<String>(SEND_SCAN_ZIP_321_URI),
                clearForm = backStack.savedStateHandle.get<Boolean>(MULTIPLE_SUBMISSION_CLEAR_FORM) ?: false
            ).also {
                // Remove Send screen arguments passed from the Scan or MultipleSubmissionFailure screens if
                // some exist after we use them
                backStack.savedStateHandle.remove<String>(SEND_SCAN_RECIPIENT_ADDRESS)
                backStack.savedStateHandle.remove<String>(SEND_SCAN_ZIP_321_URI)
                backStack.savedStateHandle.remove<Boolean>(MULTIPLE_SUBMISSION_CLEAR_FORM)
            },
    )

    val isEnoughSpace by storageCheckViewModel.isEnoughSpace.collectAsStateWithLifecycle()

    val sdkStatus = walletViewModel.walletSnapshot.collectAsStateWithLifecycle().value?.status

    if (isEnoughSpace == false) {
        Twig.info { "Not enough free space" }
        navController.navigateJustOnce(NOT_ENOUGH_SPACE)
    } else if (Synchronizer.Status.DISCONNECTED == sdkStatus) {
        Twig.info { "Disconnected state received from Synchronizer" }
        WrapDisconnected(
            goChooseServer = {
                navController.navigateJustOnce(CHOOSE_SERVER)
            },
            onIgnore = {
                // Keep the current navigation location
            }
        )
    } else if (ConfigurationEntries.IS_APP_UPDATE_CHECK_ENABLED.getValue(RemoteConfig.current)) {
        WrapCheckForUpdate()
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
        onFailed = {
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

private fun fillInHandleForConfirmation(
    handle: SavedStateHandle,
    zecSend: ZecSend?,
    initialStage: SendConfirmationStage
) {
    if (zecSend != null) {
        handle[SEND_CONFIRM_RECIPIENT_ADDRESS] =
            Json.encodeToString(
                serializer = SerializableAddress.serializer(),
                value = zecSend.destination.toSerializableAddress()
            )
        handle[SEND_CONFIRM_AMOUNT] = zecSend.amount.value
        handle[SEND_CONFIRM_MEMO] = zecSend.memo.value
        handle[SEND_CONFIRM_PROPOSAL] = zecSend.proposal?.toByteArray()
    }
    handle[SEND_CONFIRM_INITIAL_STAGE] = initialStage.toStringName()
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
fun NavHostController.popBackStackJustOnce(currentRouteToBePopped: String) {
    if (currentDestination?.route != currentRouteToBePopped) {
        return
    }
    popBackStack()
}

object NavigationArguments {
    const val SEND_SCAN_RECIPIENT_ADDRESS = "send_scan_recipient_address"
    const val SEND_SCAN_ZIP_321_URI = "send_scan_zip_321_uri"

    const val SEND_CONFIRM_RECIPIENT_ADDRESS = "send_confirm_recipient_address"
    const val SEND_CONFIRM_AMOUNT = "send_confirm_amount"
    const val SEND_CONFIRM_MEMO = "send_confirm_memo"
    const val SEND_CONFIRM_PROPOSAL = "send_confirm_proposal"
    const val SEND_CONFIRM_INITIAL_STAGE = "send_confirm_initial_stage"

    const val MULTIPLE_SUBMISSION_CLEAR_FORM = "multiple_submission_clear_form"

    const val PAYMENT_REQUEST_RECIPIENT_ADDRESS = "payment_request_recipient_address"
    const val PAYMENT_REQUEST_AMOUNT = "payment_request_amount"
    const val PAYMENT_REQUEST_MEMO = "payment_request_memo"
    const val PAYMENT_REQUEST_PROPOSAL = "payment_request_proposal"
}

object NavigationTargets {
    const val ABOUT = "about"
    const val ADVANCED_SETTINGS = "advanced_settings"
    const val DELETE_WALLET = "delete_wallet"
    const val EXCHANGE_RATE_OPT_IN = "exchange_rate_opt_in"
    const val EXPORT_PRIVATE_DATA = "export_private_data"
    const val HOME = "home"
    const val CHOOSE_SERVER = "choose_server"
    const val INTEGRATIONS = "integrations"
    const val NOT_ENOUGH_SPACE = "not_enough_space"
    const val PAYMENT_REQUEST = "payment_request"
    const val QR_CODE = "qr_code"
    const val REQUEST = "request"
    const val SEED_RECOVERY = "seed_recovery"
    const val SEND_CONFIRMATION = "send_confirmation"
    const val SETTINGS = "settings"
    const val SETTINGS_EXCHANGE_RATE_OPT_IN = "settings_exchange_rate_opt_in"
    const val SUPPORT = "support"
    const val WHATS_NEW = "whats_new"
}

object NavigationArgs {
    const val ADDRESS_TYPE = "addressType"
    const val ZIP321_URI = "zip321Uri"
}
