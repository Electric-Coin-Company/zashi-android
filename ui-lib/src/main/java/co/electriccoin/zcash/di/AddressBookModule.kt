package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.datasource.LocalAddressBookDataSource
import co.electriccoin.zcash.ui.common.datasource.LocalAddressBookDataSourceImpl
import co.electriccoin.zcash.ui.common.provider.AddressBookKeyStorageProvider
import co.electriccoin.zcash.ui.common.provider.AddressBookKeyStorageProviderImpl
import co.electriccoin.zcash.ui.common.provider.AddressBookProvider
import co.electriccoin.zcash.ui.common.provider.AddressBookProviderImpl
import co.electriccoin.zcash.ui.common.provider.AddressBookStorageProvider
import co.electriccoin.zcash.ui.common.provider.AddressBookStorageProviderImpl
import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.AddressBookRepositoryImpl
import co.electriccoin.zcash.ui.common.serialization.addressbook.AddressBookEncryptor
import co.electriccoin.zcash.ui.common.serialization.addressbook.AddressBookEncryptorImpl
import co.electriccoin.zcash.ui.common.serialization.addressbook.AddressBookSerializer
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val addressBookModule =
    module {
        singleOf(::AddressBookSerializer)
        singleOf(::AddressBookEncryptorImpl) bind AddressBookEncryptor::class

        factoryOf(::AddressBookKeyStorageProviderImpl) bind AddressBookKeyStorageProvider::class
        factoryOf(::AddressBookStorageProviderImpl) bind AddressBookStorageProvider::class
        factoryOf(::AddressBookProviderImpl) bind AddressBookProvider::class
        singleOf(::LocalAddressBookDataSourceImpl) bind LocalAddressBookDataSource::class
        singleOf(::AddressBookRepositoryImpl) bind AddressBookRepository::class
    }
