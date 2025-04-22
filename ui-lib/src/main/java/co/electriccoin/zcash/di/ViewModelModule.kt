package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarViewModel
import co.electriccoin.zcash.ui.common.viewmodel.AuthenticationViewModel
import co.electriccoin.zcash.ui.common.viewmodel.OldHomeViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.accountlist.viewmodel.AccountListViewModel
import co.electriccoin.zcash.ui.screen.addressbook.viewmodel.AddressBookViewModel
import co.electriccoin.zcash.ui.screen.addressbook.viewmodel.SelectRecipientViewModel
import co.electriccoin.zcash.ui.screen.advancedsettings.AdvancedSettingsViewModel
import co.electriccoin.zcash.ui.screen.balances.BalanceWidgetViewModel
import co.electriccoin.zcash.ui.screen.balances.action.BalanceActionViewModel
import co.electriccoin.zcash.ui.screen.chooseserver.ChooseServerViewModel
import co.electriccoin.zcash.ui.screen.contact.viewmodel.AddContactViewModel
import co.electriccoin.zcash.ui.screen.contact.viewmodel.UpdateContactViewModel
import co.electriccoin.zcash.ui.screen.crashreporting.viewmodel.CrashReportingViewModel
import co.electriccoin.zcash.ui.screen.error.ErrorViewModel
import co.electriccoin.zcash.ui.screen.exchangerate.optin.ExchangeRateOptInViewModel
import co.electriccoin.zcash.ui.screen.exchangerate.settings.ExchangeRateSettingsViewModel
import co.electriccoin.zcash.ui.screen.feedback.viewmodel.FeedbackViewModel
import co.electriccoin.zcash.ui.screen.flexa.FlexaViewModel
import co.electriccoin.zcash.ui.screen.home.HomeViewModel
import co.electriccoin.zcash.ui.screen.home.backup.WalletBackupDetailViewModel
import co.electriccoin.zcash.ui.screen.home.backup.WalletBackupInfoViewModel
import co.electriccoin.zcash.ui.screen.home.reporting.CrashReportOptInViewModel
import co.electriccoin.zcash.ui.screen.home.shieldfunds.ShieldFundsInfoViewModel
import co.electriccoin.zcash.ui.screen.integrations.IntegrationsViewModel
import co.electriccoin.zcash.ui.screen.qrcode.viewmodel.QrCodeViewModel
import co.electriccoin.zcash.ui.screen.receive.viewmodel.ReceiveViewModel
import co.electriccoin.zcash.ui.screen.request.viewmodel.RequestViewModel
import co.electriccoin.zcash.ui.screen.restore.date.RestoreBDDateViewModel
import co.electriccoin.zcash.ui.screen.restore.estimation.RestoreBDEstimationViewModel
import co.electriccoin.zcash.ui.screen.restore.height.RestoreBDHeightViewModel
import co.electriccoin.zcash.ui.screen.restore.seed.RestoreSeedViewModel
import co.electriccoin.zcash.ui.screen.restoresuccess.RestoreSuccessViewModel
import co.electriccoin.zcash.ui.screen.reviewtransaction.ReviewTransactionViewModel
import co.electriccoin.zcash.ui.screen.scan.Scan
import co.electriccoin.zcash.ui.screen.scan.viewmodel.ScanViewModel
import co.electriccoin.zcash.ui.screen.scankeystone.viewmodel.ScanKeystonePCZTViewModel
import co.electriccoin.zcash.ui.screen.scankeystone.viewmodel.ScanKeystoneSignInRequestViewModel
import co.electriccoin.zcash.ui.screen.selectkeystoneaccount.SelectKeystoneAccount
import co.electriccoin.zcash.ui.screen.selectkeystoneaccount.viewmodel.SelectKeystoneAccountViewModel
import co.electriccoin.zcash.ui.screen.send.SendViewModel
import co.electriccoin.zcash.ui.screen.settings.viewmodel.ScreenBrightnessViewModel
import co.electriccoin.zcash.ui.screen.settings.viewmodel.SettingsViewModel
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.viewmodel.SignKeystoneTransactionViewModel
import co.electriccoin.zcash.ui.screen.support.viewmodel.SupportViewModel
import co.electriccoin.zcash.ui.screen.taxexport.TaxExportViewModel
import co.electriccoin.zcash.ui.screen.transactiondetail.TransactionDetail
import co.electriccoin.zcash.ui.screen.transactiondetail.TransactionDetailViewModel
import co.electriccoin.zcash.ui.screen.transactionfilters.viewmodel.TransactionFiltersViewModel
import co.electriccoin.zcash.ui.screen.transactionhistory.TransactionHistoryViewModel
import co.electriccoin.zcash.ui.screen.transactionhistory.widget.TransactionHistoryWidgetViewModel
import co.electriccoin.zcash.ui.screen.transactionnote.TransactionNote
import co.electriccoin.zcash.ui.screen.transactionnote.viewmodel.TransactionNoteViewModel
import co.electriccoin.zcash.ui.screen.transactionprogress.TransactionProgressViewModel
import co.electriccoin.zcash.ui.screen.walletbackup.WalletBackupViewModel
import co.electriccoin.zcash.ui.screen.warning.viewmodel.StorageCheckViewModel
import co.electriccoin.zcash.ui.screen.whatsnew.viewmodel.WhatsNewViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModelOf(::WalletViewModel)
        viewModelOf(::AuthenticationViewModel)
        viewModelOf(::OldHomeViewModel)
        viewModelOf(::StorageCheckViewModel)
        viewModelOf(::RestoreSeedViewModel)
        viewModelOf(::ScreenBrightnessViewModel)
        viewModelOf(::SettingsViewModel)
        viewModelOf(::AdvancedSettingsViewModel)
        viewModelOf(::SupportViewModel)
        viewModelOf(::RestoreSuccessViewModel)
        viewModelOf(::WhatsNewViewModel)
        viewModelOf(::ChooseServerViewModel)
        viewModel { (address: String?) ->
            AddContactViewModel(
                address = address,
                validateContactAddress = get(),
                validateContactName = get(),
                saveContact = get(),
                navigationRouter = get()
            )
        }
        viewModelOf(::UpdateContactViewModel)
        viewModelOf(::ReceiveViewModel)
        viewModelOf(::QrCodeViewModel)
        viewModelOf(::RequestViewModel)
        viewModel { (args: Scan) ->
            ScanViewModel(
                args = args,
                getSynchronizer = get(),
                zip321ParseUriValidationUseCase = get(),
                onAddressScanned = get(),
                zip321Scanned = get()
            )
        }
        viewModelOf(::ScanKeystoneSignInRequestViewModel)
        viewModelOf(::ScanKeystonePCZTViewModel)
        viewModelOf(::IntegrationsViewModel)
        viewModelOf(::FlexaViewModel)
        viewModelOf(::SendViewModel)
        viewModelOf(::WalletBackupViewModel)
        viewModelOf(::FeedbackViewModel)
        viewModelOf(::SignKeystoneTransactionViewModel)
        viewModelOf(::AccountListViewModel)
        viewModelOf(::ZashiTopAppBarViewModel)
        viewModel { (args: SelectKeystoneAccount) ->
            SelectKeystoneAccountViewModel(
                args = args,
                createKeystoneAccount = get(),
                deriveKeystoneAccountUnifiedAddress = get(),
                parseKeystoneUrToZashiAccounts = get(),
                navigationRouter = get()
            )
        }
        viewModelOf(::ReviewTransactionViewModel)
        viewModelOf(::TransactionFiltersViewModel)
        viewModelOf(::TransactionProgressViewModel)
        viewModelOf(::TransactionHistoryWidgetViewModel)
        viewModelOf(::TransactionHistoryViewModel)
        viewModel { (transactionDetail: TransactionDetail) ->
            TransactionDetailViewModel(
                transactionDetail = transactionDetail,
                getTransactionDetailById = get(),
                copyToClipboard = get(),
                navigationRouter = get(),
                sendTransactionAgain = get(),
                flipTransactionBookmark = get(),
                markTxMemoAsRead = get()
            )
        }
        viewModelOf(::AddressBookViewModel)
        viewModelOf(::SelectRecipientViewModel)
        viewModel { (transactionNote: TransactionNote) ->
            TransactionNoteViewModel(
                transactionNote = transactionNote,
                navigationRouter = get(),
                getTransactionNote = get(),
                createOrUpdateTransactionNote = get(),
                deleteTransactionNote = get(),
            )
        }
        viewModelOf(::TaxExportViewModel)
        viewModelOf(::CrashReportingViewModel)
        viewModelOf(::BalanceWidgetViewModel)
        viewModelOf(::HomeViewModel)
        viewModelOf(::RestoreBDHeightViewModel)
        viewModelOf(::RestoreBDDateViewModel)
        viewModelOf(::RestoreBDEstimationViewModel)
        viewModelOf(::ShieldFundsInfoViewModel)
        viewModelOf(::WalletBackupInfoViewModel)
        viewModelOf(::ExchangeRateOptInViewModel)
        viewModelOf(::ExchangeRateSettingsViewModel)
        viewModelOf(::WalletBackupDetailViewModel)
        viewModelOf(::ErrorViewModel)
        viewModelOf(::BalanceActionViewModel)
        viewModelOf(::CrashReportOptInViewModel)
    }
