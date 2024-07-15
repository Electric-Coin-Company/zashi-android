package co.electriccoin.zcash.app.di

import co.electriccoin.zcash.ui.common.viewmodel.AuthenticationViewModel
import co.electriccoin.zcash.ui.common.viewmodel.CheckUpdateViewModel
import co.electriccoin.zcash.ui.common.viewmodel.HomeViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.account.viewmodel.TransactionHistoryViewModel
import co.electriccoin.zcash.ui.screen.onboarding.viewmodel.OnboardingViewModel
import co.electriccoin.zcash.ui.screen.restore.viewmodel.RestoreViewModel
import co.electriccoin.zcash.ui.screen.sendconfirmation.viewmodel.CreateTransactionsViewModel
import co.electriccoin.zcash.ui.screen.settings.viewmodel.ScreenBrightnessViewModel
import co.electriccoin.zcash.ui.screen.settings.viewmodel.SettingsViewModel
import co.electriccoin.zcash.ui.screen.support.viewmodel.SupportViewModel
import co.electriccoin.zcash.ui.screen.warning.viewmodel.StorageCheckViewModel
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
        viewModelOf(::SupportViewModel)
        viewModelOf(::CreateTransactionsViewModel)
    }
