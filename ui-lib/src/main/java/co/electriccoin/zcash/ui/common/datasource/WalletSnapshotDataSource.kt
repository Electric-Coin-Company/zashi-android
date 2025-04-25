package co.electriccoin.zcash.ui.common.datasource

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.provider.WalletRestoringStateProvider
import co.electriccoin.zcash.ui.common.viewmodel.SynchronizerError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.callbackFlow
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
                        synchronizer.toCommonError(),
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

    private fun Synchronizer.toCommonError(): Flow<SynchronizerError?> =
        callbackFlow {
            // just for initial default value emit
            trySend(null)

            onCriticalErrorHandler = {
                Twig.error { "WALLET - Error Critical: $it" }
                trySend(SynchronizerError.Critical(it))
                false
            }
            onProcessorErrorHandler = {
                Twig.error { "WALLET - Error Processor: $it" }
                trySend(SynchronizerError.Processor(it))
                false
            }
            onSubmissionErrorHandler = {
                Twig.error { "WALLET - Error Submission: $it" }
                trySend(SynchronizerError.Submission(it))
                false
            }
            onSetupErrorHandler = {
                Twig.error { "WALLET - Error Setup: $it" }
                trySend(SynchronizerError.Setup(it))
                false
            }
            onChainErrorHandler = { x, y ->
                Twig.error { "WALLET - Error Chain: $x, $y" }
                trySend(SynchronizerError.Chain(x, y))
            }

            awaitClose {
                // nothing to close here
            }
        }
}
