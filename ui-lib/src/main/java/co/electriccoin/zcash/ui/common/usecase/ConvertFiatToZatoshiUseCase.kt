package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepository
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.math.MathContext

class ConvertFiatToZatoshiUseCase(
    private val exchangeRateRepository: ExchangeRateRepository
) {
    operator fun invoke(fiat: BigDecimal?): Zatoshi? {
        if (fiat == null) return null
        return calculate(
            exchangeRateState = exchangeRateRepository.state.value,
            fiat = fiat
        )
    }

    fun observe(fiat: BigDecimal?): Flow<Zatoshi?> {
        if (fiat == null) return flowOf(null)
        return exchangeRateRepository
            .state
            .map {
                calculate(exchangeRateState = it, fiat = fiat)
            }
    }

    private fun calculate(
        exchangeRateState: ExchangeRateState,
        fiat: BigDecimal
    ) = if (exchangeRateState is ExchangeRateState.Data && exchangeRateState.currencyConversion != null) {
        val priceOfZec = BigDecimal(exchangeRateState.currencyConversion.priceOfZec)
        fiat.divide(priceOfZec, MathContext.DECIMAL128).convertZecToZatoshi()
    } else {
        null
    }
}
