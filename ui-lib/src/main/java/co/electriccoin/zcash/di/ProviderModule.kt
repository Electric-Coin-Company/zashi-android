package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.provider.GetDefaultServersProvider
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.provider.GetZcashCurrencyProvider
import co.electriccoin.zcash.ui.common.provider.GetMonetarySeparatorProvider
import co.electriccoin.zcash.ui.common.provider.LocalAddressBookStorageProvider
import co.electriccoin.zcash.ui.common.provider.LocalAddressBookStorageProviderImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val providerModule =
    module {
        factoryOf(::GetDefaultServersProvider)
        factoryOf(::GetVersionInfoProvider)
        factoryOf(::GetZcashCurrencyProvider)
        factoryOf(::LocalAddressBookStorageProviderImpl) bind LocalAddressBookStorageProvider::class
        factoryOf(::GetMonetarySeparatorProvider)
    }
