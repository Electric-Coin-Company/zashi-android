package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.viewmodel.AuthenticationViewModel
import co.electriccoin.zcash.ui.common.viewmodel.CheckUpdateViewModel
import co.electriccoin.zcash.ui.common.viewmodel.HomeViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.common.viewmodel.ZashiMainTopAppBarViewModel
import co.electriccoin.zcash.ui.screen.account.viewmodel.TransactionHistoryViewModel
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
import co.electriccoin.zcash.ui.screen.paymentrequest.viewmodel.PaymentRequestViewModel
import co.electriccoin.zcash.ui.screen.qrcode.viewmodel.QrCodeViewModel
import co.electriccoin.zcash.ui.screen.receive.viewmodel.ReceiveViewModel
import co.electriccoin.zcash.ui.screen.request.viewmodel.RequestViewModel
import co.electriccoin.zcash.ui.screen.restore.viewmodel.RestoreViewModel
import co.electriccoin.zcash.ui.screen.restoresuccess.viewmodel.RestoreSuccessViewModel
import co.electriccoin.zcash.ui.screen.reviewtransaction.ReviewKeystoneTransaction
import co.electriccoin.zcash.ui.screen.reviewtransaction.ReviewKeystoneTransactionViewModel
import co.electriccoin.zcash.ui.screen.scan.ScanNavigationArgs
import co.electriccoin.zcash.ui.screen.scan.viewmodel.ScanViewModel
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
import co.electriccoin.zcash.ui.screen.update.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update.viewmodel.UpdateViewModel
import co.electriccoin.zcash.ui.screen.warning.viewmodel.StorageCheckViewModel
import co.electriccoin.zcash.ui.screen.whatsnew.viewmodel.WhatsNewViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModelOf(::WalletViewModel)
        viewModelOf(::AuthenticationViewModel)
        viewModelOf(::CheckUpdateViewModel)
        viewModelOf(::HomeViewModel)
        viewModelOf(::TransactionHistoryViewModel)
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
        viewModel { (updateInfo: UpdateInfo) ->
            UpdateViewModel(
                application = get(),
                updateInfo = updateInfo,
                appUpdateChecker = get(),
            )
        }
        viewModelOf(::ChooseServerViewModel)
        viewModelOf(::AddressBookViewModel)
        viewModelOf(::SelectRecipientViewModel)
        viewModel { (address: String?) ->
            AddContactViewModel(
                address = address,
                validateContactAddress = get(),
                validateContactName = get(),
                saveContact = get(),
            )
        }
        viewModelOf(::UpdateContactViewModel)
        viewModelOf(::ReceiveViewModel)
        viewModelOf(::QrCodeViewModel)
        viewModelOf(::RequestViewModel)
        viewModelOf(::PaymentRequestViewModel)
        viewModelOf(::IntegrationsViewModel)
        viewModel { (args: ScanNavigationArgs) ->
            ScanViewModel(
                args = args,
                getSynchronizer = get(),
                zip321ParseUriValidationUseCase = get(),
            )
        }
        viewModelOf(::ScanKeystoneSignInRequestViewModel)
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
                decodeUrToZashiAccounts = get()
            )
        }
        viewModel { (args: ReviewKeystoneTransaction) ->
            ReviewKeystoneTransactionViewModel(
                args = args,
                observeContactByAddress = get(),
                getLoadedExchangeRate = get(),
            )
        }
    }
