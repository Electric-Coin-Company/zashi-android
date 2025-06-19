package co.electriccoin.zcash.ui.common.repository

import co.electriccoin.zcash.ui.common.datasource.NearDataSource
import co.electriccoin.zcash.ui.common.model.NearSwapAsset
import co.electriccoin.zcash.ui.common.model.SwapAsset
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.IOException

interface SwapRepository {
    val mode: StateFlow<SwapMode>

    val assets: StateFlow<SwapAssets>

    val selectedAsset: StateFlow<SwapAsset?>

    val slippage: StateFlow<Int>

    fun select(asset: SwapAsset)

    fun setSlippage(amount: Int)

    fun requestRefreshAssets()

    fun changeMode(mode: SwapMode)

    fun clear()
}

enum class SwapMode { SWAP, PAY }

class NearSwapRepository(
    private val nearDataSource: NearDataSource
) : SwapRepository {
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    override val mode = MutableStateFlow(DEFAULT_MODE)

    override val assets =
        MutableStateFlow(
            SwapAssets(
                data = null,
                zecAsset = null,
                isLoading = true,
                type = SwapAssets.Type.NEAR
            )
        )

    override val selectedAsset = MutableStateFlow<NearSwapAsset?>(null)

    override val slippage = MutableStateFlow(DEFAULT_SLIPPAGE)

    private var refreshJob: Job? = null

    override fun select(asset: SwapAsset) {
        check(asset is NearSwapAsset)
        selectedAsset.update { asset }
    }

    override fun setSlippage(amount: Int) = slippage.update { amount }

    override fun requestRefreshAssets() {
        fun findZecSwapAsset(assets: List<SwapAsset>) = assets.find { asset ->
            asset.tokenTicker.lowercase() == "zec" && asset.chainTicker.lowercase() == "zec"
        }

        fun filterSwapAssets(assets: List<SwapAsset>) = assets
            .toMutableList()
            .apply {
                removeIf {
                    val usdPrice = it.usdPrice
                    it.tokenTicker.lowercase() == "zec" || usdPrice == null || usdPrice.toFloat() == 0f
                }
            }
            .toList()

        scope.launch {
            assets.update { it.copy(isLoading = true) }
            try {
                val tokens = nearDataSource.getSupportedTokens()
                val filtered = withContext(Dispatchers.Default) { filterSwapAssets(tokens) }
                val zecAsset = withContext(Dispatchers.Default) { findZecSwapAsset(tokens) }
                assets.update {
                    it.copy(
                        data = filtered,
                        zecAsset = zecAsset,
                        isLoading = false
                    )
                }
            } catch (_: ResponseException) {
                assets.update { it.copy(isLoading = false) }
            } catch (_: IOException) {
                assets.update { it.copy(isLoading = false) }
            }
        }
    }

    override fun changeMode(mode: SwapMode) {
        this.mode.update { mode }
    }

    override fun clear() {
        refreshJob?.cancel()
        refreshJob = null
        selectedAsset.update { null }
        slippage.update { DEFAULT_SLIPPAGE }
        mode.update { DEFAULT_MODE }
    }
}

data class SwapAssets(
    val data: List<SwapAsset>?,
    val zecAsset: SwapAsset?,
    val isLoading: Boolean,
    val type: Type
) {
    enum class Type {
        NEAR
    }
}

private const val DEFAULT_SLIPPAGE = 10 // 1%

private val DEFAULT_MODE = SwapMode.SWAP
