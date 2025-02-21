package co.electriccoin.zcash.crash.android.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val crashProviderModule =
    module {
        factoryOf(::CrashReportersProvider)
    }
