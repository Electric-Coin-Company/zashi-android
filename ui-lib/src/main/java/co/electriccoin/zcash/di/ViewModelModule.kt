package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.viewmodel.AuthenticationViewModel
import co.electriccoin.zcash.ui.common.viewmodel.HomeViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.common.viewmodel.ZashiMainTopAppBarViewModel
import co.electriccoin.zcash.ui.screen.accountlist.viewmodel.AccountListViewModel
import co.electriccoin.zcash.ui.screen.addressbook.viewmodel.AddressBookViewModel
import co.electriccoin.zcash.ui.screen.addressbook.viewmodel.SelectRecipientViewModel
import co.electriccoin.zcash.ui.screen.advancedsettings.viewmodel.AdvancedSettingsViewModel
import co.electriccoin.zcash.ui.screen.chooseserver.ChooseServerViewModel
import co.electriccoin.zcash.ui.screen.contact.viewmodel.AddContactViewModel
import co.electriccoin.zcash.ui.screen.contact.viewmodel.UpdateContactViewModel
import co.electriccoin.zcash.ui.screen.feedback.viewmodel.FeedbackViewModel
import co.electriccoin.zcash.ui.screen.integrations.viewmodel.IntegrationsViewModel
import co.electriccoin.zcash.ui.screen.onboarding.viewmodel.OnboardingViewModel
import co.electriccoin.zcash.ui.screen.qrcode.viewmodel.QrCodeViewModel
import co.electriccoin.zcash.ui.screen.receive.viewmodel.ReceiveViewModel
import co.electriccoin.zcash.ui.screen.request.viewmodel.RequestViewModel
import co.electriccoin.zcash.ui.screen.restore.viewmodel.RestoreViewModel
import co.electriccoin.zcash.ui.screen.restoresuccess.viewmodel.RestoreSuccessViewModel
import co.electriccoin.zcash.ui.screen.reviewtransaction.ReviewTransactionViewModel
import co.electriccoin.zcash.ui.screen.scan.ScanNavigationArgs
import co.electriccoin.zcash.ui.screen.scan.viewmodel.ScanViewModel
import co.electriccoin.zcash.ui.screen.scankeystone.viewmodel.ScanKeystonePCZTViewModel
import co.electriccoin.zcash.ui.screen.scankeystone.viewmodel.ScanKeystoneSignInRequestViewModel
import co.electriccoin.zcash.ui.screen.seed.SeedNavigationArgs
import co.electriccoin.zcash.ui.screen.seed.viewmodel.SeedViewModel
import co.electriccoin.zcash.ui.screen.selectkeystoneaccount.SelectKeystoneAccount
import co.electriccoin.zcash.ui.screen.selectkeystoneaccount.viewmodel.SelectKeystoneAccountViewModel
import co.electriccoin.zcash.ui.screen.send.SendViewModel
import co.electriccoin.zcash.ui.screen.sendconfirmation.viewmodel.CreateTransactionsViewModel
import co.electriccoin.zcash.ui.screen.settings.viewmodel.ScreenBrightnessViewModel
import co.electriccoin.zcash.ui.screen.settings.viewmodel.SettingsViewModel
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.viewmodel.SignKeystoneTransactionViewModel
import co.electriccoin.zcash.ui.screen.support.viewmodel.SupportViewModel
import co.electriccoin.zcash.ui.screen.transactiondetail.TransactionDetail
import co.electriccoin.zcash.ui.screen.transactiondetail.TransactionDetailViewModel
import co.electriccoin.zcash.ui.screen.transactionfilters.viewmodel.TransactionFiltersViewModel
import co.electriccoin.zcash.ui.screen.transactionhistory.TransactionHistoryViewModel
import co.electriccoin.zcash.ui.screen.transactionhistory.widget.TransactionHistoryWidgetViewModel
import co.electriccoin.zcash.ui.screen.transactionnote.TransactionNote
import co.electriccoin.zcash.ui.screen.transactionnote.viewmodel.TransactionNoteViewModel
import co.electriccoin.zcash.ui.screen.transactionprogress.TransactionProgressViewModel
import co.electriccoin.zcash.ui.screen.warning.viewmodel.StorageCheckViewModel
import co.electriccoin.zcash.ui.screen.whatsnew.viewmodel.WhatsNewViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModelOf(::WalletViewModel)
        viewModelOf(::AuthenticationViewModel)
        viewModelOf(::HomeViewModel)
        viewModelOf(::OnboardingViewModel)
        viewModelOf(::StorageCheckViewModel)
        viewModelOf(::RestoreViewModel)
        viewModelOf(::ScreenBrightnessViewModel)
        viewModelOf(::SettingsViewModel)
        viewModelOf(::AdvancedSettingsViewModel)
        viewModelOf(::SupportViewModel)
        viewModelOf(::CreateTransactionsViewModel)
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
        viewModelOf(::IntegrationsViewModel)
        viewModel { (args: ScanNavigationArgs) ->
            ScanViewModel(
                args = args,
                getSynchronizer = get(),
                zip321ParseUriValidationUseCase = get(),
            )
        }
        viewModelOf(::ScanKeystoneSignInRequestViewModel)
        viewModelOf(::ScanKeystonePCZTViewModel)
        viewModelOf(::IntegrationsViewModel)
        viewModelOf(::SendViewModel)
        viewModel { (args: SeedNavigationArgs) ->
            SeedViewModel(
                observePersistableWallet = get(),
                args = args,
                walletRepository = get(),
            )
        }
        viewModelOf(::FeedbackViewModel)
        viewModelOf(::SignKeystoneTransactionViewModel)
        viewModelOf(::AccountListViewModel)
        viewModelOf(::ZashiMainTopAppBarViewModel)
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
    }
