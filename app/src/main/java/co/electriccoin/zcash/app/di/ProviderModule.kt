package co.electriccoin.zcash.app.di

import co.electriccoin.zcash.ui.common.provider.GetDefaultServersProvider
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val providerModule =
    module {
        factoryOf(::GetDefaultServersProvider)
    }
