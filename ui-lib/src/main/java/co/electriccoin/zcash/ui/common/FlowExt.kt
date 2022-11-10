package co.electriccoin.zcash.ui.common

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
fun <T> Flow<T>.throttle(
    duration: Duration,
    timeSource: TimeSource = TimeSource.Monotonic
): Flow<T> = flow {
    coroutineScope {
        val context = coroutineContext
        val mutex = Mutex()

        var timeMark = timeSource.markNow()
        var delayEmit: Deferred<Unit>? = null
        var firstValue = true
        var valueToEmit: T
        collect { value ->
            if (firstValue) {
                firstValue = false
                emit(value)
                timeMark = timeSource.markNow()
                return@collect
            }
            delayEmit?.cancel()
            valueToEmit = value

            if (timeMark.elapsedNow() >= duration) {
                mutex.withLock {
                    emit(valueToEmit)
                    timeMark = timeSource.markNow()
                }
            } else {
                delayEmit = async(Dispatchers.Default) {
                    mutex.withLock {
                        delay(duration)
                        withContext(context) {
                            emit(valueToEmit)
                        }
                        timeMark = timeSource.markNow()
                    }
                }
            }
        }
    }
}
