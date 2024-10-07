package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.datasource.AddressBookDataSource
import co.electriccoin.zcash.ui.common.datasource.AddressBookDataSourceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataSourceModule =
    module {
        singleOf(::AddressBookDataSourceImpl) bind AddressBookDataSource::class
    }
