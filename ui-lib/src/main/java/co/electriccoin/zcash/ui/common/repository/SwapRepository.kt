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
import kotlinx.io.IOException

interface SwapRepository {
    val assets: StateFlow<SwapAssets>
    val selectedAsset: StateFlow<SwapAsset?>

    fun select(asset: SwapAsset)

    fun requestRefreshAssets()

    fun clear()
}

class NearSwapRepository(
    private val nearDataSource: NearDataSource
) : SwapRepository {
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    override val assets =
        MutableStateFlow(
            SwapAssets(
                data = null,
                isLoading = true,
                type = SwapAssets.Type.NEAR
            )
        )

    override val selectedAsset = MutableStateFlow<NearSwapAsset?>(null)

    private var refreshJob: Job? = null

    override fun select(asset: SwapAsset) {
        check(asset is NearSwapAsset)
        selectedAsset.update { asset }
    }

    override fun requestRefreshAssets() {
        scope.launch {
            assets.update { it.copy(isLoading = true) }
            try {
                val tokens = nearDataSource.getSupportedTokens()
                assets.update { it.copy(data = tokens, isLoading = false) }
            } catch (_: ResponseException) {
                assets.update { it.copy(isLoading = false) }
            } catch (_: IOException) {
                assets.update { it.copy(isLoading = false) }
            }
        }
    }

    override fun clear() {
        refreshJob?.cancel()
        refreshJob = null
        selectedAsset.update { null }
    }
}

data class SwapAssets(
    val data: List<SwapAsset>?,
    val isLoading: Boolean,
    val type: Type
) {
    enum class Type {
        NEAR
    }
}
