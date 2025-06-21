package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.mapper.TransactionHistoryMapper
import co.electriccoin.zcash.ui.screen.swap.PayMapper
import co.electriccoin.zcash.ui.screen.swap.SwapMapper
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val mapperModule =
    module {
        factoryOf(::TransactionHistoryMapper)
        factoryOf(::PayMapper)
        factoryOf(::SwapMapper)
    }
