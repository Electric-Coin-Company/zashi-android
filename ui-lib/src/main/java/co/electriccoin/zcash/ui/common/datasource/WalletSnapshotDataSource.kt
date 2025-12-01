package co.electriccoin.zcash.ui.common.datasource

import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.provider.WalletRestoringStateProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

interface WalletSnapshotDataSource {
    fun observe(): StateFlow<WalletSnapshot?>
}

class WalletSnapshotDataSourceImpl(
    synchronizerProvider: SynchronizerProvider,
    walletRestoringStateProvider: WalletRestoringStateProvider,
) : WalletSnapshotDataSource {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @OptIn(ExperimentalCoroutinesApi::class)
    val flow =
        synchronizerProvider
            .synchronizer
            .flatMapLatest { synchronizer ->
                if (synchronizer == null) {
                    flowOf(null)
                } else {
                    combine(
                        synchronizer.status,
                        synchronizer.progress,
                        synchronizerProvider.error,
                        synchronizer.areFundsSpendable,
                        walletRestoringStateProvider.observe()
                    ) { status, progress, error, isSpendable, restoringState ->
                        WalletSnapshot(
                            status = status,
                            progress = progress,
                            synchronizerError = error,
                            isSpendable = isSpendable,
                            restoringState = restoringState,
                        )
                    }
                }
            }.stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    override fun observe(): StateFlow<WalletSnapshot?> = flow
}
