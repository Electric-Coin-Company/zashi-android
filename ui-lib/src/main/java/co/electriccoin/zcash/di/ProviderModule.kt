package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.provider.AddressBookKeyStorageProvider
import co.electriccoin.zcash.ui.common.provider.AddressBookKeyStorageProviderImpl
import co.electriccoin.zcash.ui.common.provider.AddressBookProvider
import co.electriccoin.zcash.ui.common.provider.AddressBookProviderImpl
import co.electriccoin.zcash.ui.common.provider.AddressBookStorageProvider
import co.electriccoin.zcash.ui.common.provider.AddressBookStorageProviderImpl
import co.electriccoin.zcash.ui.common.provider.ApplicationStateProvider
import co.electriccoin.zcash.ui.common.provider.ApplicationStateProviderImpl
import co.electriccoin.zcash.ui.common.provider.GetDefaultServersProvider
import co.electriccoin.zcash.ui.common.provider.GetMonetarySeparatorProvider
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.provider.GetZcashCurrencyProvider
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProviderImpl
import co.electriccoin.zcash.ui.common.provider.SelectedAccountUUIDProvider
import co.electriccoin.zcash.ui.common.provider.SelectedAccountUUIDProviderImpl
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.provider.SynchronizerProviderImpl
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
        factoryOf(::AddressBookStorageProviderImpl) bind AddressBookStorageProvider::class
        factoryOf(::AddressBookProviderImpl) bind AddressBookProvider::class
        factoryOf(::AddressBookKeyStorageProviderImpl) bind AddressBookKeyStorageProvider::class
        factoryOf(::SelectedAccountUUIDProviderImpl) bind SelectedAccountUUIDProvider::class
        singleOf(::PersistableWalletProviderImpl) bind PersistableWalletProvider::class
        singleOf(::SynchronizerProviderImpl) bind SynchronizerProvider::class
        singleOf(::ApplicationStateProviderImpl) bind ApplicationStateProvider::class
    }
