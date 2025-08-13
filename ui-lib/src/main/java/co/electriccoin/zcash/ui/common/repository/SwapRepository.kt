package co.electriccoin.zcash.ui.common.repository

import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.SwapDataSource
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.model.SwapMode
import co.electriccoin.zcash.ui.common.model.SwapQuote
import co.electriccoin.zcash.ui.common.model.SwapQuoteStatus
import co.electriccoin.zcash.ui.common.model.SwapStatus
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
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

    fun observeSwapStatus(depositAddress: String): Flow<SwapQuoteStatusData>

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
    val error: Exception?,
)

data class SwapQuoteStatusData(
    val data: SwapQuoteStatus?,
    val destinationAsset: SwapAsset?,
    val isLoading: Boolean,
    val error: Exception?,
)

class SwapRepositoryImpl(
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
            )
        )

    override val selectedAsset = MutableStateFlow<SwapAsset?>(null)

    override val slippage = MutableStateFlow(DEFAULT_SLIPPAGE)

    override val quote = MutableStateFlow<SwapQuoteData?>(null)

    private var refreshJob: Job? = null

    private var requestQuoteJob: Job? = null

    override fun select(asset: SwapAsset?) = selectedAsset.update { asset }

    override fun setSlippage(amount: BigDecimal) = slippage.update { amount }

    override fun requestRefreshAssets() {
        refreshJob?.cancel()
        refreshJob =
            scope.launch {
                while (true) {
                    refreshAssetsInternal()
                    delay(30.seconds)
                }
            }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun refreshAssetsInternal() {
        suspend fun findZecSwapAsset(assets: List<SwapAsset>) =
            withContext(Dispatchers.Default) {
                assets.find { asset ->
                    asset.tokenTicker.lowercase() == "zec" && asset.chainTicker.lowercase() == "zec"
                }
            }

        suspend fun filterSwapAssets(assets: List<SwapAsset>) =
            withContext(Dispatchers.Default) {
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
                val usdc = filtered.find { it.tokenTicker.lowercase() == "usdc" && it.chainTicker == "near" }
                selectedAsset.update { usdc }
            }
        } catch (e: ResponseException) {
            assets.update { assets ->
                assets.copy(
                    isLoading = false,
                    error = e.takeIf { assets.data == null }
                )
            }
        } catch (e: Exception) {
            assets.update { assets ->
                assets.copy(
                    isLoading = false,
                    error = e.takeIf { assets.data == null }
                )
            }
        }
    }

    override fun changeMode(mode: SwapMode) {
        this.mode.update { mode }
    }

    override fun observeSwapStatus(depositAddress: String): Flow<SwapQuoteStatusData> {
        return channelFlow {
            val data = MutableStateFlow(
                SwapQuoteStatusData(
                    data = null,
                    isLoading = true,
                    destinationAsset = null,
                    error = null
                )
            )

            launch {
                data.collect { send(it) }
            }

            launch {
                val supportedTokens = try {
                    swapDataSource.getSupportedTokens()
                } catch (e: Exception) {
                    data.update { it.copy(isLoading = false, error = e) }
                    return@launch
                }

                while (true) {
                    try {
                        val result = swapDataSource.checkSwapStatus(depositAddress)
                        val destinationAsset = supportedTokens.find { it.assetId == result.destinationAssetId }

                        if (destinationAsset == null) {
                            data.update {
                                it.copy(
                                    data = result,
                                    isLoading = false,
                                    destinationAsset = null,
                                    error = IllegalStateException("No destination asset found")
                                )
                            }
                            break
                        } else {
                            data.update {
                                it.copy(
                                    data = result,
                                    isLoading = false,
                                    destinationAsset = destinationAsset,
                                    error = null
                                )
                            }
                            if (result.status in listOf(SwapStatus.SUCCESS, SwapStatus.REFUNDED, SwapStatus.FAILED)) {
                                break
                            }
                        }
                    } catch (e: Exception) {
                        data.update { it.copy(isLoading = false, error = e) }
                        break
                    }
                    delay(30.seconds)
                }
            }

            awaitClose {
                // do nothing
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override fun requestQuote(amount: BigDecimal, address: String) {
        requestQuoteJob =
            scope.launch {
                quote.update { SwapQuoteData.Loading }
                val originAsset = assets.value.zecAsset ?: return@launch
                val destinationAsset = selectedAsset.value ?: return@launch
                try {
                    val selectedAccount = accountDataSource.getSelectedAccount()
                    val result =
                        swapDataSource.requestQuote(
                            swapMode = mode.value,
                            amount = amount,
                            originAddress = selectedAccount.transparent.address.address,
                            originAsset = originAsset,
                            destinationAddress = address,
                            destinationAsset = destinationAsset,
                            slippage = slippage.value,
                        )
                    quote.update { SwapQuoteData.Success(quote = result) }
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

private val DEFAULT_SLIPPAGE = BigDecimal("0.5")

private val DEFAULT_MODE = SwapMode.EXACT_INPUT
