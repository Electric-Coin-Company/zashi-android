package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.provider.ApplicationStateProvider
import co.electriccoin.zcash.ui.common.provider.ApplicationStateProviderImpl
import co.electriccoin.zcash.ui.common.provider.GetDefaultServersProvider
import co.electriccoin.zcash.ui.common.provider.GetMonetarySeparatorProvider
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.provider.GetZcashCurrencyProvider
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProviderImpl
import co.electriccoin.zcash.ui.common.provider.RestoreTimestampStorageProvider
import co.electriccoin.zcash.ui.common.provider.RestoreTimestampStorageProviderImpl
import co.electriccoin.zcash.ui.common.provider.SelectedAccountUUIDProvider
import co.electriccoin.zcash.ui.common.provider.SelectedAccountUUIDProviderImpl
import co.electriccoin.zcash.ui.common.provider.ShieldFundsRemindMeCountStorageProvider
import co.electriccoin.zcash.ui.common.provider.ShieldFundsRemindMeCountStorageProviderImpl
import co.electriccoin.zcash.ui.common.provider.ShieldFundsRemindMeTimestampStorageProvider
import co.electriccoin.zcash.ui.common.provider.ShieldFundsRemindMeTimestampStorageProviderImpl
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.provider.SynchronizerProviderImpl
import co.electriccoin.zcash.ui.common.provider.WalletBackupFlagStorageProvider
import co.electriccoin.zcash.ui.common.provider.WalletBackupFlagStorageProviderImpl
import co.electriccoin.zcash.ui.common.provider.WalletBackupRemindMeCountStorageProvider
import co.electriccoin.zcash.ui.common.provider.WalletBackupRemindMeCountStorageProviderImpl
import co.electriccoin.zcash.ui.common.provider.WalletBackupRemindMeTimestampStorageProvider
import co.electriccoin.zcash.ui.common.provider.WalletBackupRemindMeTimestampStorageProviderImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val providerModule =
    module {
        factoryOf(::GetDefaultServersProvider)
        factoryOf(::GetVersionInfoProvider)
        factoryOf(::GetZcashCurrencyProvider)
        factoryOf(::GetMonetarySeparatorProvider)
        factoryOf(::SelectedAccountUUIDProviderImpl) bind SelectedAccountUUIDProvider::class
        singleOf(::PersistableWalletProviderImpl) bind PersistableWalletProvider::class
        singleOf(::SynchronizerProviderImpl) bind SynchronizerProvider::class
        singleOf(::ApplicationStateProviderImpl) bind ApplicationStateProvider::class
        factoryOf(::RestoreTimestampStorageProviderImpl) bind RestoreTimestampStorageProvider::class
        factoryOf(::ShieldFundsRemindMeCountStorageProviderImpl) bind
            ShieldFundsRemindMeCountStorageProvider::class
        factoryOf(::ShieldFundsRemindMeTimestampStorageProviderImpl) bind
            ShieldFundsRemindMeTimestampStorageProvider::class
        factoryOf(::WalletBackupRemindMeCountStorageProviderImpl) bind
            WalletBackupRemindMeCountStorageProvider::class
        factoryOf(::WalletBackupRemindMeTimestampStorageProviderImpl) bind
            WalletBackupRemindMeTimestampStorageProvider::class
        factoryOf(::WalletBackupFlagStorageProviderImpl) bind WalletBackupFlagStorageProvider::class
    }
