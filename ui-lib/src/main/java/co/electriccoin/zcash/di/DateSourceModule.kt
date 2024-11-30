package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.AccountDataSourceImpl
import co.electriccoin.zcash.ui.common.datasource.LocalAddressBookDataSource
import co.electriccoin.zcash.ui.common.datasource.LocalAddressBookDataSourceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataSourceModule =
    module {
        singleOf(::LocalAddressBookDataSourceImpl) bind LocalAddressBookDataSource::class
        singleOf(::AccountDataSourceImpl) bind AccountDataSource::class
        // singleOf(::RemoteAddressBookDataSourceImpl) bind RemoteAddressBookDataSource::class
    }
