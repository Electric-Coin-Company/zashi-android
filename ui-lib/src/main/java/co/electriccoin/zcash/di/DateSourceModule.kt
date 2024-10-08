package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.datasource.LocalAddressBookDataSource
import co.electriccoin.zcash.ui.common.datasource.LocalAddressBookDataSourceImpl
import co.electriccoin.zcash.ui.common.datasource.RemoteAddressBookProvider
import co.electriccoin.zcash.ui.common.datasource.RemoteAddressBookProviderImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataSourceModule =
    module {
        singleOf(::LocalAddressBookDataSourceImpl) bind LocalAddressBookDataSource::class
        singleOf(::RemoteAddressBookProviderImpl) bind RemoteAddressBookProvider::class
    }
