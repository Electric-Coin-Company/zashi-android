package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.AddressBookRepositoryImpl
import co.electriccoin.zcash.ui.common.repository.BalanceRepository
import co.electriccoin.zcash.ui.common.repository.BalanceRepositoryImpl
import co.electriccoin.zcash.ui.common.repository.ConfigurationRepository
import co.electriccoin.zcash.ui.common.repository.ConfigurationRepositoryImpl
import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepository
import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepositoryImpl
import co.electriccoin.zcash.ui.common.repository.WalletRepository
import co.electriccoin.zcash.ui.common.repository.WalletRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule =
    module {
        singleOf(::WalletRepositoryImpl) bind WalletRepository::class
        singleOf(::ConfigurationRepositoryImpl) bind ConfigurationRepository::class
        singleOf(::ExchangeRateRepositoryImpl) bind ExchangeRateRepository::class
        singleOf(::BalanceRepositoryImpl) bind BalanceRepository::class
        singleOf(::AddressBookRepositoryImpl) bind AddressBookRepository::class
    }
