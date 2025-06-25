package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.mapper.TransactionHistoryMapper
import co.electriccoin.zcash.ui.screen.swap.NearPayMapper
import co.electriccoin.zcash.ui.screen.swap.NearSwapMapper
import co.electriccoin.zcash.ui.screen.swap.quote.NearSwapQuoteSuccessMapper
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val mapperModule =
    module {
        factoryOf(::TransactionHistoryMapper)
        factoryOf(::NearPayMapper)
        factoryOf(::NearSwapMapper)
        factoryOf(::NearSwapQuoteSuccessMapper)
    }
