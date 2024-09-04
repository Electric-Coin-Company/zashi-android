package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.usecase.GetSynchronizerUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveSynchronizerUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val useCaseModule =
    module {
        singleOf(::ObserveSynchronizerUseCase)
        singleOf(::GetSynchronizerUseCase)
    }
