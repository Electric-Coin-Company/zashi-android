package co.electriccoin.zcash.ui.common.repository

import co.electriccoin.zcash.ui.common.datasource.NearDataSource
import co.electriccoin.zcash.ui.common.model.NearTokenChain
import co.electriccoin.zcash.ui.common.model.SwapTokenChain
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
    val tokenChains: StateFlow<SwapTokenChains>
    val selectedTokenChain: StateFlow<SwapTokenChain?>

    fun selectTokenChain(swapTokenChain: SwapTokenChain)

    fun requestRefreshTokenChains()

    fun clear()
}

class NearSwapRepository(
    private val nearDataSource: NearDataSource
) : SwapRepository {
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    override val tokenChains =
        MutableStateFlow(
            SwapTokenChains(
                data = null,
                isLoading = true,
                type = SwapTokenChains.Type.NEAR
            )
        )

    override val selectedTokenChain = MutableStateFlow<NearTokenChain?>(null)

    private var refreshJob: Job? = null

    override fun selectTokenChain(swapTokenChain: SwapTokenChain) {
        check(swapTokenChain is NearTokenChain)
        selectedTokenChain.update { swapTokenChain }
    }

    override fun requestRefreshTokenChains() {
        scope.launch {
            tokenChains.update { it.copy(isLoading = true) }
            try {
                val tokens = nearDataSource.getSupportedTokens()
                tokenChains.update { it.copy(data = tokens, isLoading = false) }
            } catch (_: ResponseException) {
                tokenChains.update { it.copy(isLoading = false) }
            } catch (_: IOException) {
                tokenChains.update { it.copy(isLoading = false) }
            }
        }
    }

    override fun clear() {
        refreshJob?.cancel()
        refreshJob = null
        selectedTokenChain.update { null }
    }
}

data class SwapTokenChains(
    val data: List<SwapTokenChain>?,
    val isLoading: Boolean,
    val type: Type
) {
    enum class Type {
        NEAR
    }
}
