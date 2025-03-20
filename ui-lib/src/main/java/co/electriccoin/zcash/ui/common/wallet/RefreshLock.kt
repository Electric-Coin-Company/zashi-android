package co.electriccoin.zcash.ui.common.wallet

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

internal class RefreshLock(
    timestampToObserve: Flow<Instant?>,
    private val lockDuration: Duration,
) : TimestampFlowLock {
    private val mutex = Mutex()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state: Flow<Boolean> =
        timestampToObserve
            .flatMapLatest { lastRefresh ->
                flow {
                    if (lastRefresh == null) {
                        emitWithLock(false)
                        return@flow
                    }
                    val elapsed = Clock.System.now() - lastRefresh
                    if (elapsed > lockDuration) {
                        emitWithLock(true)
                    } else {
                        emitWithLock(false)
                        delay(lockDuration - elapsed)
                        emitWithLock(true)
                    }
                }
            }.distinctUntilChanged()

    private suspend fun <T> FlowCollector<T>.emitWithLock(value: T) =
        mutex.withLock {
            emit(value)
        }
}
