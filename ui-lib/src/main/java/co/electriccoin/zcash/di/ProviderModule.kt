package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.provider.ApplicationStateProvider
import co.electriccoin.zcash.ui.common.provider.ApplicationStateProviderImpl
import co.electriccoin.zcash.ui.common.provider.CrashReportingStorageProvider
import co.electriccoin.zcash.ui.common.provider.CrashReportingStorageProviderImpl
import co.electriccoin.zcash.ui.common.provider.ExchangeRateOptInStorageProvider
import co.electriccoin.zcash.ui.common.provider.ExchangeRateOptInStorageProviderImpl
import co.electriccoin.zcash.ui.common.provider.GetDefaultServersProvider
import co.electriccoin.zcash.ui.common.provider.GetMonetarySeparatorProvider
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.provider.GetZcashCurrencyProvider
import co.electriccoin.zcash.ui.common.provider.IsTorExplicitlySetProvider
import co.electriccoin.zcash.ui.common.provider.IsTorExplicitlySetProviderImpl
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProviderImpl
import co.electriccoin.zcash.ui.common.provider.PersistableWalletStorageProvider
import co.electriccoin.zcash.ui.common.provider.PersistableWalletStorageProviderImpl
import co.electriccoin.zcash.ui.common.provider.RestoreTimestampStorageProvider
import co.electriccoin.zcash.ui.common.provider.RestoreTimestampStorageProviderImpl
import co.electriccoin.zcash.ui.common.provider.SelectedAccountUUIDProvider
import co.electriccoin.zcash.ui.common.provider.SelectedAccountUUIDProviderImpl
import co.electriccoin.zcash.ui.common.provider.ShieldFundsInfoProvider
import co.electriccoin.zcash.ui.common.provider.ShieldFundsInfoProviderImpl
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.provider.SynchronizerProviderImpl
import co.electriccoin.zcash.ui.common.provider.WalletBackupConsentStorageProvider
import co.electriccoin.zcash.ui.common.provider.WalletBackupConsentStorageProviderImpl
import co.electriccoin.zcash.ui.common.provider.WalletBackupFlagStorageProvider
import co.electriccoin.zcash.ui.common.provider.WalletBackupFlagStorageProviderImpl
import co.electriccoin.zcash.ui.common.provider.WalletBackupRemindMeCountStorageProvider
import co.electriccoin.zcash.ui.common.provider.WalletBackupRemindMeCountStorageProviderImpl
import co.electriccoin.zcash.ui.common.provider.WalletBackupRemindMeTimestampStorageProvider
import co.electriccoin.zcash.ui.common.provider.WalletBackupRemindMeTimestampStorageProviderImpl
import co.electriccoin.zcash.ui.common.provider.WalletRestoringStateProvider
import co.electriccoin.zcash.ui.common.provider.WalletRestoringStateProviderImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val providerModule =
    module {
        singleOf(::GetDefaultServersProvider)
        singleOf(::GetVersionInfoProvider)
        singleOf(::GetZcashCurrencyProvider)
        singleOf(::GetMonetarySeparatorProvider)
        singleOf(::SelectedAccountUUIDProviderImpl) bind SelectedAccountUUIDProvider::class
        singleOf(::PersistableWalletProviderImpl) bind PersistableWalletProvider::class
        singleOf(::SynchronizerProviderImpl) bind SynchronizerProvider::class
        singleOf(::ApplicationStateProviderImpl) bind ApplicationStateProvider::class
        singleOf(::RestoreTimestampStorageProviderImpl) bind RestoreTimestampStorageProvider::class
        singleOf(::WalletBackupRemindMeCountStorageProviderImpl) bind
            WalletBackupRemindMeCountStorageProvider::class
        singleOf(::WalletBackupRemindMeTimestampStorageProviderImpl) bind
            WalletBackupRemindMeTimestampStorageProvider::class
        singleOf(::WalletBackupFlagStorageProviderImpl) bind WalletBackupFlagStorageProvider::class
        singleOf(::WalletBackupConsentStorageProviderImpl) bind WalletBackupConsentStorageProvider::class
        singleOf(::WalletRestoringStateProviderImpl) bind WalletRestoringStateProvider::class
        singleOf(::CrashReportingStorageProviderImpl) bind CrashReportingStorageProvider::class
        singleOf(::PersistableWalletStorageProviderImpl) bind PersistableWalletStorageProvider::class
        singleOf(::ShieldFundsInfoProviderImpl) bind ShieldFundsInfoProvider::class
        singleOf(::ExchangeRateOptInStorageProviderImpl) bind ExchangeRateOptInStorageProvider::class
        singleOf(::IsTorExplicitlySetProviderImpl) bind IsTorExplicitlySetProvider::class
    }
