package co.electriccoin.zcash.ui.common.repository

import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.SwapDataSource
import co.electriccoin.zcash.ui.common.datasource.TokenNotFoundException
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.model.SwapMode
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_INPUT
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_OUTPUT
import co.electriccoin.zcash.ui.common.model.SwapQuote
import co.electriccoin.zcash.ui.common.model.SwapQuoteStatus
import co.electriccoin.zcash.ui.common.model.SwapStatus
import co.electriccoin.zcash.ui.common.model.ZecSwapAsset
import co.electriccoin.zcash.ui.common.provider.SimpleSwapAssetProvider
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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import kotlin.time.Duration.Companion.seconds

interface SwapRepository {
    val assets: StateFlow<SwapAssetsData>

    val selectedAsset: StateFlow<SwapAsset?>

    val slippage: StateFlow<BigDecimal>

    val quote: StateFlow<SwapQuoteData?>

    fun select(asset: SwapAsset?)

    fun setSlippage(amount: BigDecimal)

    fun requestRefreshAssets()

    fun observeSwapStatus(depositAddress: String): Flow<SwapQuoteStatusData>

    suspend fun getSwapStatus(depositAddress: String): SwapQuoteStatusData =
        observeSwapStatus(depositAddress).first { !it.isLoading }

    fun requestExactInputQuote(amount: BigDecimal, address: String, refundAddress: String)

    fun requestExactOutputQuote(amount: BigDecimal, address: String, refundAddress: String)

    fun requestExactInputIntoZec(amount: BigDecimal, refundAddress: String, destinationAddress: String)

    fun clear()

    fun clearQuote()
}

sealed interface SwapQuoteData {
    data class Success(
        val quote: SwapQuote
    ) : SwapQuoteData

    data class Error(
        val mode: SwapMode,
        val exception: Exception
    ) : SwapQuoteData

    data object Loading : SwapQuoteData
}

data class SwapAssetsData(
    val data: List<SwapAsset>?,
    val zecAsset: SwapAsset?,
    val isLoading: Boolean,
    val error: Exception?,
)

data class SwapQuoteStatusData(
    val status: SwapQuoteStatus?,
    val isLoading: Boolean,
    val error: Exception?,
) {
    val originAsset = status?.quote?.originAsset
    val destinationAsset = status?.quote?.destinationAsset
}

@Suppress("TooManyFunctions")
class SwapRepositoryImpl(
    private val swapDataSource: SwapDataSource,
    private val metadataRepository: MetadataRepository,
    private val simpleSwapAssetProvider: SimpleSwapAssetProvider
) : SwapRepository {
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

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
                assets.find { asset -> asset is ZecSwapAsset }
            }

        suspend fun filterSwapAssets(assets: List<SwapAsset>) =
            withContext(Dispatchers.Default) {
                assets
                    .toMutableList()
                    .apply {
                        removeIf {
                            val usdPrice = it.usdPrice
                            it is ZecSwapAsset || usdPrice == null || usdPrice.toFloat() == 0f
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
                val assetToSelect =
                    metadataRepository
                        .observeLastUsedAssetHistory()
                        .filterNotNull()
                        .first()
                        .firstOrNull() ?: simpleSwapAssetProvider
                        .get(tokenTicker = "usdc", chainTicker = "near")
                val foundAssetToSelect =
                    filtered
                        .find {
                            it.tokenTicker.lowercase() == assetToSelect.tokenTicker.lowercase() &&
                                it.chainTicker.lowercase() == assetToSelect.chainTicker.lowercase()
                        }

                if (foundAssetToSelect != null) {
                    selectedAsset.update { foundAssetToSelect }
                }
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

    @Suppress("TooGenericExceptionCaught", "LoopWithTooManyJumpStatements")
    override fun observeSwapStatus(depositAddress: String): Flow<SwapQuoteStatusData> {
        return channelFlow {
            val data =
                MutableStateFlow(
                    SwapQuoteStatusData(
                        status = null,
                        isLoading = true,
                        error = null
                    )
                )

            launch {
                data.collect { send(it) }
            }

            launch {
                val supportedTokens =
                    try {
                        swapDataSource.getSupportedTokens()
                    } catch (e: Exception) {
                        data.update { it.copy(isLoading = false, error = e) }
                        return@launch
                    }

                while (true) {
                    try {
                        val result = swapDataSource.checkSwapStatus(depositAddress, supportedTokens)
                        metadataRepository.updateSwap(
                            depositAddress = depositAddress,
                            amountOutFormatted = result.amountOutFormatted,
                            status = result.status,
                            mode = result.mode,
                            origin = result.quote.originAsset,
                            destination = result.quote.destinationAsset
                        )
                        data.update {
                            it.copy(
                                status = result,
                                isLoading = false,
                                error = null
                            )
                        }
                        if (result.status in listOf(SwapStatus.SUCCESS, SwapStatus.REFUNDED)) {
                            break
                        }
                    } catch (e: TokenNotFoundException) {
                        data.update {
                            it.copy(
                                isLoading = false,
                                error = e
                            )
                        }
                        break
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

    override fun requestExactInputQuote(amount: BigDecimal, address: String, refundAddress: String) {
        requestSwapFromZecQuote(
            amount = amount,
            address = address,
            mode = EXACT_INPUT,
            refundAddress = refundAddress
        )
    }

    override fun requestExactOutputQuote(amount: BigDecimal, address: String, refundAddress: String) {
        requestSwapFromZecQuote(
            amount = amount,
            address = address,
            mode = EXACT_OUTPUT,
            refundAddress = refundAddress
        )
    }

    @Suppress("TooGenericExceptionCaught")
    override fun requestExactInputIntoZec(amount: BigDecimal, refundAddress: String, destinationAddress: String) {
        requestQuoteJob =
            scope.launch {
                quote.update { SwapQuoteData.Loading }
                val originAsset = selectedAsset.value ?: return@launch
                val destinationAsset = assets.value.zecAsset ?: return@launch
                try {
                    val result =
                        swapDataSource.requestQuote(
                            swapMode = EXACT_INPUT,
                            amount = amount,
                            refundAddress = refundAddress,
                            originAsset = originAsset,
                            destinationAddress = destinationAddress,
                            destinationAsset = destinationAsset,
                            slippage = slippage.value,
                            affiliateAddress = "electriccoinco.near"
                        )
                    quote.update { SwapQuoteData.Success(quote = result) }
                } catch (e: Exception) {
                    quote.update { SwapQuoteData.Error(EXACT_INPUT, e) }
                }
            }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun requestSwapFromZecQuote(amount: BigDecimal, address: String, mode: SwapMode, refundAddress: String) {
        requestQuoteJob =
            scope.launch {
                quote.update { SwapQuoteData.Loading }
                val originAsset = assets.value.zecAsset ?: return@launch
                val destinationAsset = selectedAsset.value ?: return@launch
                try {
                    val result =
                        swapDataSource.requestQuote(
                            swapMode = mode,
                            amount = amount,
                            refundAddress = refundAddress,
                            originAsset = originAsset,
                            destinationAddress = address,
                            destinationAsset = destinationAsset,
                            slippage = slippage.value,
                            affiliateAddress =
                                when (mode) {
                                    EXACT_INPUT -> "electriccoinco.near"
                                    EXACT_OUTPUT -> "crosspay.near"
                                }
                        )
                    quote.update { SwapQuoteData.Success(quote = result) }
                } catch (e: Exception) {
                    quote.update { SwapQuoteData.Error(SwapMode.EXACT_OUTPUT, e) }
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
        clearQuote()
    }

    override fun clearQuote() {
        requestQuoteJob?.cancel()
        requestQuoteJob = null
        quote.update { null }
    }
}

private val DEFAULT_SLIPPAGE = BigDecimal("1")
