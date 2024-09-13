package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.provider.GetDefaultServersProvider
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.provider.GetZcashCurrencyProvider
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val providerModule =
    module {
        factoryOf(::GetDefaultServersProvider)
        factoryOf(::GetVersionInfoProvider)
        factoryOf(::GetZcashCurrencyProvider)
    }
