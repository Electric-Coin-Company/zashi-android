package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.provider.GetDefaultServersProvider
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.provider.GetZcashCurrencyProvider
import co.electriccoin.zcash.ui.common.provider.LocalAddressBookProvider
import co.electriccoin.zcash.ui.common.provider.LocalAddressBookProviderImpl
import co.electriccoin.zcash.ui.common.provider.RemoteAddressBookProvider
import co.electriccoin.zcash.ui.common.provider.RemoteAddressBookProviderImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val providerModule =
    module {
        factoryOf(::GetDefaultServersProvider)
        factoryOf(::GetVersionInfoProvider)
        factoryOf(::GetZcashCurrencyProvider)
        singleOf(::LocalAddressBookProviderImpl) bind LocalAddressBookProvider::class
        singleOf(::RemoteAddressBookProviderImpl) bind RemoteAddressBookProvider::class
    }
