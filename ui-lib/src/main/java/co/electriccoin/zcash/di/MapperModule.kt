package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.mapper.TransactionHistoryMapper
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val mapperModule =
    module {
        factoryOf(::TransactionHistoryMapper)
    }
