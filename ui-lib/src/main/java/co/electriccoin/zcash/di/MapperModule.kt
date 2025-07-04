package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.mapper.TransactionHistoryMapper
import co.electriccoin.zcash.ui.screen.swap.ExactOutputVMMapper
import co.electriccoin.zcash.ui.screen.swap.ExactInputVMMapper
import co.electriccoin.zcash.ui.screen.swap.quote.SwapQuoteVMMapper
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val mapperModule =
    module {
        factoryOf(::TransactionHistoryMapper)
        factoryOf(::ExactOutputVMMapper)
        factoryOf(::ExactInputVMMapper)
        factoryOf(::SwapQuoteVMMapper)
    }
