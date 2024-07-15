package co.electriccoin.zcash.app.di

import cash.z.ecc.sdk.usecase.ObserveSynchronizerUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val useCaseModule = module {
    singleOf(::ObserveSynchronizerUseCase)
}
