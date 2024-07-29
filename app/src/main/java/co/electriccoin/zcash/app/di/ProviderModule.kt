package co.electriccoin.zcash.app.di

import co.electriccoin.zcash.ui.common.usecase.AvailableServersProvider
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val providerModule =
    module {
        factoryOf(::AvailableServersProvider)
    }
