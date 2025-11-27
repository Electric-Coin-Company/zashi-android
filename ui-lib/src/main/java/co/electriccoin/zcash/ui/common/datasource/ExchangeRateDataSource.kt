package co.electriccoin.zcash.ui.common.datasource

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.FiatCurrencyConversion
import cash.z.ecc.android.sdk.model.ObserveFiatCurrencyResult
import co.electriccoin.zcash.ui.BuildConfig
import co.electriccoin.zcash.ui.common.model.ZecSwapAsset
import co.electriccoin.zcash.ui.common.provider.CMCApiProvider
import co.electriccoin.zcash.ui.common.provider.NearApiProvider
import co.electriccoin.zcash.ui.common.provider.ResponseWithErrorException
import co.electriccoin.zcash.ui.common.provider.SwapAssetProvider
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.io.IOException

interface ExchangeRateDataSource {

    /**
     * Returns exchange rate from CMC if [BuildConfig.ZCASH_CMC_KEY] is available, otherwise calls NEAR instead.
     */
    @Throws(
        ExchangeRateUnavailable::class,
        ResponseException::class,
        ResponseWithErrorException::class,
        IOException::class
    )
    suspend fun getExchangeRate(): FiatCurrencyConversion

    fun observeSynchronizerRoute(): Flow<ObserveFiatCurrencyResult>
}

/**
 * Thrown when exchange rate is not found or server was down.
 */
class ExchangeRateUnavailable(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

class ExchangeRateDataSourceImpl(
    private val cmcApiProvider: CMCApiProvider,
    private val synchronizerProvider: SynchronizerProvider,
    private val nearApiProvider: NearApiProvider,
    private val swapAssetProvider: SwapAssetProvider,
) : ExchangeRateDataSource {

    override suspend fun getExchangeRate(): FiatCurrencyConversion {
        suspend fun getCMCExchangeRate(apiKey: String): FiatCurrencyConversion {
            val exchangeRate = try {
                cmcApiProvider.getExchangeRateQuote(apiKey).data["ZEC"]?.quote?.get("USD")?.price
            } catch (e: ResponseException) {
                if (e.response.status.value in 500..599) throw ExchangeRateUnavailable(cause = e) else throw e
            }
            val price = exchangeRate ?: throw ExchangeRateUnavailable(message = "Exchange rate not found in response")
            return FiatCurrencyConversion(timestamp = Clock.System.now(), priceOfZec = price.toDouble())
        }

        suspend fun getNEARExchangeRate(): FiatCurrencyConversion = withContext(Dispatchers.Default) {
            val zecSwapAset = try {
                nearApiProvider
                    .getSupportedTokens()
                    .asSequence()
                    .map {
                        swapAssetProvider.get(
                            tokenTicker = it.symbol,
                            chainTicker = it.blockchain,
                            usdPrice = it.price,
                            assetId = it.assetId,
                            decimals = it.decimals
                        )
                    }
                    .find { it is ZecSwapAsset }
            } catch (e: ResponseException) {
                if (e.response.status.value in 500..599) throw ExchangeRateUnavailable(cause = e) else throw e
            }
            val price = zecSwapAset?.usdPrice
                ?: throw ExchangeRateUnavailable(message = "Exchange rate not found in response")
            FiatCurrencyConversion(timestamp = Clock.System.now(), priceOfZec = price.toDouble())
        }

        val cmcKey = BuildConfig.ZCASH_CMC_KEY.takeIf { it.isNotBlank() }

        return if (cmcKey != null) {
            getCMCExchangeRate(cmcKey)
        } else {
            getNEARExchangeRate()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeSynchronizerRoute(): Flow<ObserveFiatCurrencyResult> {
        return channelFlow {
            val exchangeRate =
                synchronizerProvider
                    .synchronizer
                    .flatMapLatest { synchronizer ->
                        synchronizer?.exchangeRateUsd ?: flowOf(ObserveFiatCurrencyResult())
                    }.stateIn(this)

            launch {
                synchronizerProvider
                    .synchronizer
                    .flatMapLatest { it?.status ?: flowOf(null) }
                    .flatMapLatest {
                        when (it) {
                            null -> flowOf(ObserveFiatCurrencyResult())
                            Synchronizer.Status.STOPPED,
                            Synchronizer.Status.INITIALIZING -> emptyFlow()

                            else -> exchangeRate
                        }
                    }
                    .collect { send(it) }
            }

            awaitClose()
        }
    }
}

