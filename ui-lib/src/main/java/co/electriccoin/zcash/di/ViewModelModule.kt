package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarVM
import co.electriccoin.zcash.ui.common.viewmodel.AuthenticationViewModel
import co.electriccoin.zcash.ui.common.viewmodel.OldHomeViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.accountlist.viewmodel.AccountListViewModel
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookVM
import co.electriccoin.zcash.ui.screen.addressbook.SelectRecipientVM
import co.electriccoin.zcash.ui.screen.advancedsettings.AdvancedSettingsVM
import co.electriccoin.zcash.ui.screen.balances.BalanceWidgetVM
import co.electriccoin.zcash.ui.screen.balances.spendable.SpendableBalanceVM
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
import co.electriccoin.zcash.ui.screen.home.HomeVM
import co.electriccoin.zcash.ui.screen.home.backup.WalletBackupDetailViewModel
import co.electriccoin.zcash.ui.screen.home.backup.WalletBackupInfoViewModel
import co.electriccoin.zcash.ui.screen.home.reporting.CrashReportOptInViewModel
import co.electriccoin.zcash.ui.screen.home.restoring.WalletRestoringInfoViewModel
import co.electriccoin.zcash.ui.screen.home.shieldfunds.ShieldFundsInfoViewModel
import co.electriccoin.zcash.ui.screen.integrations.IntegrationsVM
import co.electriccoin.zcash.ui.screen.pay.PayVM
import co.electriccoin.zcash.ui.screen.qrcode.viewmodel.QrCodeViewModel
import co.electriccoin.zcash.ui.screen.receive.ReceiveVM
import co.electriccoin.zcash.ui.screen.request.viewmodel.RequestViewModel
import co.electriccoin.zcash.ui.screen.restore.date.RestoreBDDateViewModel
import co.electriccoin.zcash.ui.screen.restore.estimation.RestoreBDEstimationViewModel
import co.electriccoin.zcash.ui.screen.restore.height.RestoreBDHeightViewModel
import co.electriccoin.zcash.ui.screen.restore.seed.RestoreSeedViewModel
import co.electriccoin.zcash.ui.screen.restoresuccess.RestoreSuccessViewModel
import co.electriccoin.zcash.ui.screen.reviewtransaction.ReviewTransactionVM
import co.electriccoin.zcash.ui.screen.scan.ScanGenericAddressVM
import co.electriccoin.zcash.ui.screen.scan.ScanZashiAddressVM
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
import co.electriccoin.zcash.ui.screen.swap.detail.SwapDetailVM
import co.electriccoin.zcash.ui.screen.swap.info.SwapRefundAddressInfoVM
import co.electriccoin.zcash.ui.screen.swap.orconfirmation.ORSwapConfirmationVM
import co.electriccoin.zcash.ui.screen.swap.picker.SwapAssetPickerVM
import co.electriccoin.zcash.ui.screen.swap.picker.SwapBlockchainPickerVM
import co.electriccoin.zcash.ui.screen.swap.quote.SwapQuoteVM
import co.electriccoin.zcash.ui.screen.swap.slippage.SwapSlippageVM
import co.electriccoin.zcash.ui.screen.taxexport.TaxExportViewModel
import co.electriccoin.zcash.ui.screen.tor.optin.TorOptInVM
import co.electriccoin.zcash.ui.screen.tor.settings.TorSettingsVM
import co.electriccoin.zcash.ui.screen.transactiondetail.TransactionDetailVM
import co.electriccoin.zcash.ui.screen.transactionfilters.viewmodel.TransactionFiltersVM
import co.electriccoin.zcash.ui.screen.transactionhistory.TransactionHistoryVM
import co.electriccoin.zcash.ui.screen.transactionhistory.widget.TransactionHistoryWidgetVM
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
        viewModelOf(::ScanZashiAddressVM)
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
        viewModelOf(::ReviewTransactionVM)
        viewModelOf(::TransactionFiltersVM)
        viewModelOf(::TransactionProgressVM)
        viewModelOf(::TransactionHistoryWidgetVM)
        viewModelOf(::TransactionHistoryVM)
        viewModelOf(::TransactionDetailVM)
        viewModelOf(::AddressBookVM)
        viewModelOf(::SelectRecipientVM)
        viewModelOf(::TransactionNoteViewModel)
        viewModelOf(::TaxExportViewModel)
        viewModelOf(::CrashReportingViewModel)
        viewModelOf(::BalanceWidgetVM)
        viewModelOf(::HomeVM)
        viewModelOf(::RestoreBDHeightViewModel)
        viewModelOf(::RestoreBDDateViewModel)
        viewModelOf(::RestoreBDEstimationViewModel)
        viewModelOf(::ShieldFundsInfoViewModel)
        viewModelOf(::WalletBackupInfoViewModel)
        viewModelOf(::ExchangeRateSettingsVM)
        viewModelOf(::WalletBackupDetailViewModel)
        viewModelOf(::ErrorViewModel)
        viewModelOf(::SpendableBalanceVM)
        viewModelOf(::CrashReportOptInViewModel)
        viewModelOf(::WalletRestoringInfoViewModel)
        viewModelOf(::ThirdPartyScanViewModel)
        viewModelOf(::TorSettingsVM)
        viewModelOf(::TorOptInVM)
        viewModelOf(::ExchangeRateOptInVM)
        viewModelOf(::SwapAssetPickerVM)
        viewModelOf(::SwapSlippageVM)
        viewModelOf(::SwapVM)
        viewModelOf(::PayVM)
        viewModelOf(::SwapQuoteVM)
        viewModelOf(::ScanGenericAddressVM)
        viewModelOf(::SelectSwapABRecipientVM)
        viewModelOf(::SwapBlockchainPickerVM)
        viewModelOf(::AddZashiABContactVM)
        viewModelOf(::AddSwapABContactVM)
        viewModelOf(::AddGenericABContactVM)
        viewModelOf(::UpdateGenericABContactVM)
        viewModelOf(::ORSwapConfirmationVM)
        viewModelOf(::SwapDetailVM)
        viewModelOf(::SwapRefundAddressInfoVM)
    }
