package co.electriccoin.zcash.ui.common.datasource

import co.electriccoin.zcash.ui.common.provider.CMCApiProvider
import co.electriccoin.zcash.ui.common.provider.ResponseWithErrorException
import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException
import java.math.BigDecimal

interface CMCDataSource {
    @Throws(ResponseException::class, ResponseWithErrorException::class, IOException::class)
    suspend fun getExchangeRate(): BigDecimal
}

class ExchangeRateNotFoundException : Exception()

class CMCDataSourceImpl(
    private val cmcApiProvider: CMCApiProvider
) : CMCDataSource {
    override suspend fun getExchangeRate(): BigDecimal {
        return cmcApiProvider.getExchangeRateQuote().data["ZEC"]?.quote?.get("USD")?.price
            ?: throw ExchangeRateNotFoundException()
    }
}

