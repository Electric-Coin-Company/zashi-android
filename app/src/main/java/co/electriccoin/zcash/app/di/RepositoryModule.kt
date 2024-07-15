package co.electriccoin.zcash.app.di

import cash.z.ecc.sdk.repository.WalletRepository
import cash.z.ecc.sdk.repository.WalletRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::WalletRepositoryImpl) bind WalletRepository::class
}
