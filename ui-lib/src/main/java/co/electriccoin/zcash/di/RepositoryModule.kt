package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.AddressBookRepositoryImpl
import co.electriccoin.zcash.ui.common.repository.BalanceRepository
import co.electriccoin.zcash.ui.common.repository.BalanceRepositoryImpl
import co.electriccoin.zcash.ui.common.repository.BiometricRepository
import co.electriccoin.zcash.ui.common.repository.BiometricRepositoryImpl
import co.electriccoin.zcash.ui.common.repository.ConfigurationRepository
import co.electriccoin.zcash.ui.common.repository.ConfigurationRepositoryImpl
import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepository
import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepositoryImpl
import co.electriccoin.zcash.ui.common.repository.FlexaRepository
import co.electriccoin.zcash.ui.common.repository.FlexaRepositoryImpl
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepositoryImpl
import co.electriccoin.zcash.ui.common.repository.TransactionFilterRepository
import co.electriccoin.zcash.ui.common.repository.TransactionFilterRepositoryImpl
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import co.electriccoin.zcash.ui.common.repository.TransactionRepositoryImpl
import co.electriccoin.zcash.ui.common.repository.WalletRepository
import co.electriccoin.zcash.ui.common.repository.WalletRepositoryImpl
import co.electriccoin.zcash.ui.common.repository.ZashiProposalRepository
import co.electriccoin.zcash.ui.common.repository.ZashiProposalRepositoryImpl
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
        singleOf(::FlexaRepositoryImpl) bind FlexaRepository::class
        singleOf(::BiometricRepositoryImpl) bind BiometricRepository::class
        singleOf(::KeystoneProposalRepositoryImpl) bind KeystoneProposalRepository::class
        singleOf(::TransactionRepositoryImpl) bind TransactionRepository::class
        singleOf(::TransactionFilterRepositoryImpl) bind TransactionFilterRepository::class
        singleOf(::ZashiProposalRepositoryImpl) bind ZashiProposalRepository::class
    }
