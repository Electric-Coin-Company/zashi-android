package co.electriccoin.zcash.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
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
import co.electriccoin.zcash.ui.screen.warning.viewmodel.StorageCheckViewModel
import co.electriccoin.zcash.ui.screen.whatsnew.WrapWhatsNew

fun NavGraphBuilder.walletNavGraph(
    activity: ComponentActivity,
    walletViewModel: WalletViewModel,
    storageCheckViewModel: StorageCheckViewModel,
    navigationRouter: NavigationRouter,
) {
    navigation<MainAppGraph>(startDestination = Home) {
        composable<Home> {
            AndroidHome()
            val isEnoughSpace by storageCheckViewModel.isEnoughSpace.collectAsStateWithLifecycle()
            if (isEnoughSpace == false) {
                navigationRouter.forward(NavigationTargets.NOT_ENOUGH_SPACE)
            }
        }
        composable(NavigationTargets.SETTINGS) { WrapSettings() }
        composable<AdvancedSettingsArgs> { AdvancedSettingsScreen() }
        composable<ChooseServerArgs> { ChooseServerScreen() }
        composable<WalletBackup> { AndroidWalletBackup(it.toRoute()) }
        composable<FeedbackArgs> { FeedbackScreen() }
        composable(NavigationTargets.DELETE_WALLET) { WrapDeleteWallet(activity, walletViewModel) }
        composable<AboutArgs> { AboutScreen() }
        composable(NavigationTargets.WHATS_NEW) { WrapWhatsNew() }
        dialogComposable<IntegrationsArgs> { IntegrationsScreen() }
        composable<ExchangeRateSettingsArgs> { ExchangeRateSettingsScreen() }
        composable(NavigationTargets.CRASH_REPORTING_OPT_IN) { AndroidCrashReportingOptIn() }
        composable<ScanKeystoneSignInRequest> { WrapScanKeystoneSignInRequest() }
        composable<ScanKeystonePCZTRequest> { WrapScanKeystonePCZTRequest() }
        composable<SignKeystoneTransactionArgs> { SignKeystoneTransactionScreen() }
        dialogComposable<AccountList> { AndroidAccountList() }
        composable<ScanArgs> { ScanZashiAddressScreen(it.toRoute()) }
        composable(NavigationTargets.EXPORT_PRIVATE_DATA) { WrapExportPrivateData() }
        composable(NavigationTargets.NOT_ENOUGH_SPACE) {
            WrapNotEnoughSpace(
                goPrevious = { navigationRouter.back() },
                goSettings = { navigationRouter.forward(NavigationTargets.SETTINGS) }
            )
        }
        composable<AddressBookArgs> { AddressBookScreen() }
        composable<SelectABRecipientArgs> { SelectABRecipientScreen() }
        composable<AddZashiABContactArgs> { AddZashiABContactScreen(it.toRoute()) }
        composable(
            route = "${NavigationTargets.QR_CODE}/{${NavigationArgs.ADDRESS_TYPE}}",
            arguments = listOf(navArgument(NavigationArgs.ADDRESS_TYPE) { type = NavType.Companion.IntType })
        ) { backStackEntry ->
            val addressType =
                backStackEntry.arguments?.getInt(NavigationArgs.ADDRESS_TYPE) ?: ReceiveAddressType.Unified.ordinal
            QrCodeScreen(addressType)
        }
        composable(
            route = "${NavigationTargets.REQUEST}/{${NavigationArgs.ADDRESS_TYPE}}",
            arguments = listOf(navArgument(NavigationArgs.ADDRESS_TYPE) { type = NavType.Companion.IntType })
        ) { backStackEntry ->
            val addressType =
                backStackEntry.arguments?.getInt(NavigationArgs.ADDRESS_TYPE) ?: ReceiveAddressType.Unified.ordinal
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
