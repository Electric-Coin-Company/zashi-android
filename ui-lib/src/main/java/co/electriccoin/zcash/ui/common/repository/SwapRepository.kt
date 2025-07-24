package co.electriccoin.zcash.ui.common.repository

import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.SwapDataSource
import co.electriccoin.zcash.ui.common.model.NearSwapAsset
import co.electriccoin.zcash.ui.common.model.NearSwapQuote
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.model.SwapMode
import co.electriccoin.zcash.ui.common.model.SwapQuote
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import kotlin.time.Duration.Companion.seconds

interface SwapRepository {

    val mode: StateFlow<SwapMode>

    val assets: StateFlow<SwapAssetsData>

    val selectedAsset: StateFlow<SwapAsset?>

    val slippage: StateFlow<BigDecimal>

    val quote: StateFlow<SwapQuoteData?>

    fun select(asset: SwapAsset?)

    fun setSlippage(amount: BigDecimal)

    fun requestRefreshAssets()

    fun changeMode(mode: SwapMode)

    fun requestQuote(amount: BigDecimal, address: String)

    fun clear()

    fun clearQuote()
}

sealed interface SwapQuoteData {
    data class Success(val quote: SwapQuote) : SwapQuoteData
    data class Error(val exception: Exception) : SwapQuoteData
    data object Loading : SwapQuoteData
}

data class SwapAssetsData(
    val data: List<SwapAsset>?,
    val zecAsset: SwapAsset?,
    val isLoading: Boolean,
    val error: Error?,
    val type: Type
) {
    enum class Type { NEAR }

    enum class Error { UNEXPECTED_ERROR, SERVICE_UNAVAILABLE }
}

class NearSwapRepository(
    private val swapDataSource: SwapDataSource,
    private val accountDataSource: AccountDataSource,
) : SwapRepository {
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    override val mode = MutableStateFlow(DEFAULT_MODE)

    override val assets =
        MutableStateFlow(
            SwapAssetsData(
                data = null,
                zecAsset = null,
                isLoading = false,
                error = null,
                type = SwapAssetsData.Type.NEAR
            )
        )

    override val selectedAsset = MutableStateFlow<NearSwapAsset?>(null)

    override val slippage = MutableStateFlow(DEFAULT_SLIPPAGE)

    override val quote = MutableStateFlow<SwapQuoteData?>(null)

    private var refreshJob: Job? = null

    private var requestQuoteJob: Job? = null

    override fun select(asset: SwapAsset?) {
        if (asset != null) {
            check(asset is NearSwapAsset)
            selectedAsset.update { asset }
        } else {
            selectedAsset.update { null }
        }
    }

    override fun setSlippage(amount: BigDecimal) = slippage.update { amount }

    override fun requestRefreshAssets() {
        refreshJob?.cancel()
        refreshJob = scope.launch {
            while (true) {
                refreshAssetsInternal()
                delay(30.seconds)
            }
        }
    }

    private suspend fun refreshAssetsInternal() {
        suspend fun findZecSwapAsset(assets: List<SwapAsset>) = withContext(Dispatchers.Default) {
            assets.find { asset ->
                asset.tokenTicker.lowercase() == "zec" && asset.chainTicker.lowercase() == "zec"
            }
        }

        suspend fun filterSwapAssets(assets: List<SwapAsset>) = withContext(Dispatchers.Default) {
            assets
                .toMutableList()
                .apply {
                    removeIf {
                        val usdPrice = it.usdPrice
                        it.tokenTicker.lowercase() == "zec" || usdPrice == null || usdPrice.toFloat() == 0f
                    }
                }.toList()
        }

        assets.update { it.copy(isLoading = true) }
        try {
            val tokens = swapDataSource.getSupportedTokens()
            val filtered = filterSwapAssets(tokens)
            val zecAsset = findZecSwapAsset(tokens)
            assets.update {
                it.copy(
                    data = filtered,
                    zecAsset = zecAsset,
                    error = null,
                    isLoading = false
                )
            }

            if (selectedAsset.value == null) {
                val usdc = filtered.find { it.tokenTicker.lowercase() == "usdc" && it.chainTicker == "eth" }
                if (usdc is NearSwapAsset) {
                    selectedAsset.update { usdc }
                }
            }
        } catch (e: ResponseException) {
            assets.update {
                it.copy(
                    isLoading = false,
                    error = when {
                        it.data != null -> null
                        e.response.status == HttpStatusCode.ServiceUnavailable ->
                            SwapAssetsData.Error.SERVICE_UNAVAILABLE

                        else -> SwapAssetsData.Error.UNEXPECTED_ERROR
                    }
                )
            }
        } catch (_: Exception) {
            assets.update {
                it.copy(
                    isLoading = false,
                    error = if (it.data != null) null else SwapAssetsData.Error.UNEXPECTED_ERROR
                )
            }
        }
    }

    override fun changeMode(mode: SwapMode) {
        this.mode.update { mode }
    }

    override fun requestQuote(amount: BigDecimal, address: String) {
        requestQuoteJob = scope.launch {
            quote.update { SwapQuoteData.Loading }
            val originAsset = assets.value.zecAsset ?: return@launch
            val destinationAsset = selectedAsset.value ?: return@launch
            try {
                val selectedAccount = accountDataSource.getSelectedAccount()
                val quoteDto = swapDataSource.requestQuote(
                    swapMode = mode.value,
                    amount = amount,
                    originAddress = selectedAccount.transparent.address.address,
                    originAsset = originAsset,
                    destinationAddress = address,
                    destinationAsset = destinationAsset,
                    slippage = slippage.value,
                )
                quote.update { SwapQuoteData.Success(quote = NearSwapQuote(quoteDto)) }
            } catch (e: Exception) {
                quote.update { SwapQuoteData.Error(e) }
            }
        }
    }

    override fun clear() {
        assets.update {
            SwapAssetsData(
                data = null,
                zecAsset = null,
                isLoading = false,
                error = null,
                type = SwapAssetsData.Type.NEAR
            )
        }
        refreshJob?.cancel()
        refreshJob = null
        selectedAsset.update { null }
        slippage.update { DEFAULT_SLIPPAGE }
        mode.update { DEFAULT_MODE }
        clearQuote()
    }

    override fun clearQuote() {
        requestQuoteJob?.cancel()
        requestQuoteJob = null
        quote.update { null }
    }
}

private val DEFAULT_SLIPPAGE = BigDecimal("1")

private val DEFAULT_MODE = SwapMode.SWAP
