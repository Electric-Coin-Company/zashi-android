package co.electriccoin.zcash.ui.common.provider

import cash.z.ecc.android.sdk.SdkSynchronizer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.WalletCoordinator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

interface SynchronizerProvider {
    val synchronizer: StateFlow<Synchronizer?>

    val synchronizerTorState: StateFlow<TorState?>

    suspend fun getTorState(): TorState?

    /**
     * Get synchronizer and wait for it to be ready.
     */
    suspend fun getSynchronizer(): Synchronizer

    /**
     * Get synchronizer and wait for it to be ready.
     */
    suspend fun getSdkSynchronizer() = getSynchronizer() as SdkSynchronizer
}

enum class TorState {
    EXPLICITLY_ENABLED,
    EXPLICITLY_DISABLED,
    IMPLICITLY_DISABLED
}

class SynchronizerProviderImpl(
    walletCoordinator: WalletCoordinator
) : SynchronizerProvider {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @OptIn(ExperimentalCoroutinesApi::class)
    override val synchronizer: StateFlow<Synchronizer?> =
        walletCoordinator.synchronizer
            .flatMapLatest { synchronizer ->
                flow {
                    if (synchronizer == null) {
                        emit(null)
                    } else {
                        emit(synchronizer)
                    }
                }
            }
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = null
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    override val synchronizerTorState: StateFlow<TorState?> = synchronizer
        .flatMapLatest {
            it?.torState ?: flowOf(null)
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    override suspend fun getTorState(): TorState = getSynchronizer().getTorState()

    override suspend fun getSynchronizer(): Synchronizer =
        withContext(Dispatchers.IO) {
            synchronizer
                .filterNotNull()
                .first()
        }
}

private suspend fun Synchronizer.getTorState(): TorState {
    return when (flags.first().isTorEnabled) {
        true -> TorState.EXPLICITLY_ENABLED
        false -> TorState.EXPLICITLY_DISABLED
        null -> TorState.IMPLICITLY_DISABLED
    }
}

private val Synchronizer.torState
    get() = combine(this.status, this.flags) { status, flags ->
        when (flags.isTorEnabled) {
            true -> TorState.EXPLICITLY_ENABLED
            false -> TorState.EXPLICITLY_DISABLED
            null -> TorState.IMPLICITLY_DISABLED
        }
    }.distinctUntilChanged()
