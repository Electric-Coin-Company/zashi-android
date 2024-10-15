package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.provider.AddressBookProvider
import co.electriccoin.zcash.ui.common.provider.AddressBookProviderImpl
import co.electriccoin.zcash.ui.common.provider.AddressBookStorageProvider
import co.electriccoin.zcash.ui.common.provider.AddressBookStorageProviderImpl
import co.electriccoin.zcash.ui.common.provider.GetDefaultServersProvider
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.provider.GetZcashCurrencyProvider
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val providerModule =
    module {
        factoryOf(::GetDefaultServersProvider)
        factoryOf(::GetVersionInfoProvider)
        factoryOf(::GetZcashCurrencyProvider)
        factoryOf(::AddressBookStorageProviderImpl) bind AddressBookStorageProvider::class
        factoryOf(::AddressBookProviderImpl) bind AddressBookProvider::class
    }
