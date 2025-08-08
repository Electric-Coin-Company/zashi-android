package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarVM
import co.electriccoin.zcash.ui.common.viewmodel.AuthenticationViewModel
import co.electriccoin.zcash.ui.common.viewmodel.OldHomeViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.accountlist.viewmodel.AccountListViewModel
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookVM
import co.electriccoin.zcash.ui.screen.addressbook.SelectRecipientVM
import co.electriccoin.zcash.ui.screen.advancedsettings.AdvancedSettingsVM
import co.electriccoin.zcash.ui.screen.balances.BalanceWidgetViewModel
import co.electriccoin.zcash.ui.screen.balances.spendable.SpendableBalanceViewModel
import co.electriccoin.zcash.ui.screen.chooseserver.ChooseServerViewModel
import co.electriccoin.zcash.ui.screen.contact.AddGenericABContactVM
import co.electriccoin.zcash.ui.screen.contact.AddZashiABContactVM
import co.electriccoin.zcash.ui.screen.contact.UpdateGenericABContactVM
import co.electriccoin.zcash.ui.screen.crashreporting.viewmodel.CrashReportingViewModel
import co.electriccoin.zcash.ui.screen.error.ErrorViewModel
import co.electriccoin.zcash.ui.screen.exchangerate.optin.ExchangeRateOptInVM
import co.electriccoin.zcash.ui.screen.exchangerate.settings.ExchangeRateSettingsVM
import co.electriccoin.zcash.ui.screen.feedback.viewmodel.FeedbackViewModel
import co.electriccoin.zcash.ui.screen.flexa.FlexaViewModel
import co.electriccoin.zcash.ui.screen.home.HomeViewModel
import co.electriccoin.zcash.ui.screen.home.backup.WalletBackupDetailViewModel
import co.electriccoin.zcash.ui.screen.home.backup.WalletBackupInfoViewModel
import co.electriccoin.zcash.ui.screen.home.reporting.CrashReportOptInViewModel
import co.electriccoin.zcash.ui.screen.home.restoring.WalletRestoringInfoViewModel
import co.electriccoin.zcash.ui.screen.home.shieldfunds.ShieldFundsInfoViewModel
import co.electriccoin.zcash.ui.screen.integrations.IntegrationsVM
import co.electriccoin.zcash.ui.screen.qrcode.viewmodel.QrCodeViewModel
import co.electriccoin.zcash.ui.screen.receive.ReceiveVM
import co.electriccoin.zcash.ui.screen.request.viewmodel.RequestViewModel
import co.electriccoin.zcash.ui.screen.restore.date.RestoreBDDateViewModel
import co.electriccoin.zcash.ui.screen.restore.estimation.RestoreBDEstimationViewModel
import co.electriccoin.zcash.ui.screen.restore.height.RestoreBDHeightViewModel
import co.electriccoin.zcash.ui.screen.restore.seed.RestoreSeedViewModel
import co.electriccoin.zcash.ui.screen.restoresuccess.RestoreSuccessViewModel
import co.electriccoin.zcash.ui.screen.reviewtransaction.ReviewTransactionViewModel
import co.electriccoin.zcash.ui.screen.scan.ScanViewModel
import co.electriccoin.zcash.ui.screen.scan.thirdparty.ThirdPartyScanViewModel
import co.electriccoin.zcash.ui.screen.scankeystone.viewmodel.ScanKeystonePCZTViewModel
import co.electriccoin.zcash.ui.screen.scankeystone.viewmodel.ScanKeystoneSignInRequestViewModel
import co.electriccoin.zcash.ui.screen.selectkeystoneaccount.viewmodel.SelectKeystoneAccountViewModel
import co.electriccoin.zcash.ui.screen.send.SendViewModel
import co.electriccoin.zcash.ui.screen.settings.SettingsViewModel
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.viewmodel.SignKeystoneTransactionViewModel
import co.electriccoin.zcash.ui.screen.support.viewmodel.SupportViewModel
import co.electriccoin.zcash.ui.screen.swap.SwapVM
import co.electriccoin.zcash.ui.screen.swap.ab.AddSwapABContactVM
import co.electriccoin.zcash.ui.screen.swap.ab.SelectSwapABRecipientVM
import co.electriccoin.zcash.ui.screen.swap.picker.SwapAssetPickerVM
import co.electriccoin.zcash.ui.screen.swap.picker.SwapBlockchainPickerVM
import co.electriccoin.zcash.ui.screen.swap.quote.SwapQuoteVM
import co.electriccoin.zcash.ui.screen.swap.scan.ScanSwapAddressVM
import co.electriccoin.zcash.ui.screen.swap.slippage.SwapSlippageVM
import co.electriccoin.zcash.ui.screen.taxexport.TaxExportViewModel
import co.electriccoin.zcash.ui.screen.tor.optin.TorOptInVM
import co.electriccoin.zcash.ui.screen.tor.settings.TorSettingsVM
import co.electriccoin.zcash.ui.screen.transactiondetail.TransactionDetailVM
import co.electriccoin.zcash.ui.screen.transactionfilters.viewmodel.TransactionFiltersViewModel
import co.electriccoin.zcash.ui.screen.transactionhistory.TransactionHistoryViewModel
import co.electriccoin.zcash.ui.screen.transactionhistory.widget.TransactionHistoryWidgetViewModel
import co.electriccoin.zcash.ui.screen.transactionnote.viewmodel.TransactionNoteViewModel
import co.electriccoin.zcash.ui.screen.transactionprogress.TransactionProgressVM
import co.electriccoin.zcash.ui.screen.walletbackup.WalletBackupViewModel
import co.electriccoin.zcash.ui.screen.warning.viewmodel.StorageCheckViewModel
import co.electriccoin.zcash.ui.screen.whatsnew.viewmodel.WhatsNewViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModelOf(::WalletViewModel)
        viewModelOf(::AuthenticationViewModel)
        viewModelOf(::OldHomeViewModel)
        viewModelOf(::StorageCheckViewModel)
        viewModelOf(::RestoreSeedViewModel)
        viewModelOf(::SettingsViewModel)
        viewModelOf(::AdvancedSettingsVM)
        viewModelOf(::SupportViewModel)
        viewModelOf(::RestoreSuccessViewModel)
        viewModelOf(::WhatsNewViewModel)
        viewModelOf(::ChooseServerViewModel)
        viewModelOf(::ReceiveVM)
        viewModelOf(::QrCodeViewModel)
        viewModelOf(::RequestViewModel)
        viewModelOf(::ScanViewModel)
        viewModelOf(::ScanKeystoneSignInRequestViewModel)
        viewModelOf(::ScanKeystonePCZTViewModel)
        viewModelOf(::IntegrationsVM)
        viewModelOf(::FlexaViewModel)
        viewModelOf(::SendViewModel)
        viewModelOf(::WalletBackupViewModel)
        viewModelOf(::FeedbackViewModel)
        viewModelOf(::SignKeystoneTransactionViewModel)
        viewModelOf(::AccountListViewModel)
        viewModelOf(::ZashiTopAppBarVM)
        viewModelOf(::SelectKeystoneAccountViewModel)
        viewModelOf(::ReviewTransactionViewModel)
        viewModelOf(::TransactionFiltersViewModel)
        viewModelOf(::TransactionProgressVM)
        viewModelOf(::TransactionHistoryWidgetViewModel)
        viewModelOf(::TransactionHistoryViewModel)
        viewModelOf(::TransactionDetailVM)
        viewModelOf(::AddressBookVM)
        viewModelOf(::SelectRecipientVM)
        viewModelOf(::TransactionNoteViewModel)
        viewModelOf(::TaxExportViewModel)
        viewModelOf(::CrashReportingViewModel)
        viewModelOf(::BalanceWidgetViewModel)
        viewModelOf(::HomeViewModel)
        viewModelOf(::RestoreBDHeightViewModel)
        viewModelOf(::RestoreBDDateViewModel)
        viewModelOf(::RestoreBDEstimationViewModel)
        viewModelOf(::ShieldFundsInfoViewModel)
        viewModelOf(::WalletBackupInfoViewModel)
        viewModelOf(::ExchangeRateSettingsVM)
        viewModelOf(::WalletBackupDetailViewModel)
        viewModelOf(::ErrorViewModel)
        viewModelOf(::SpendableBalanceViewModel)
        viewModelOf(::CrashReportOptInViewModel)
        viewModelOf(::WalletRestoringInfoViewModel)
        viewModelOf(::ThirdPartyScanViewModel)
        viewModelOf(::TorSettingsVM)
        viewModelOf(::TorOptInVM)
        viewModelOf(::ExchangeRateOptInVM)
        viewModelOf(::SwapAssetPickerVM)
        viewModelOf(::SwapSlippageVM)
        viewModelOf(::SwapVM)
        viewModelOf(::SwapQuoteVM)
        viewModelOf(::ScanSwapAddressVM)
        viewModelOf(::SelectSwapABRecipientVM)
        viewModelOf(::SwapBlockchainPickerVM)
        viewModelOf(::AddZashiABContactVM)
        viewModelOf(::AddSwapABContactVM)
        viewModelOf(::AddGenericABContactVM)
        viewModelOf(::UpdateGenericABContactVM)
    }
