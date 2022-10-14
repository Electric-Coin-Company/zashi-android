package co.electriccoin.zcash.ui.common

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
fun <T> Flow<T>.throttle(
    waitMillis: Duration,
    timeSource: TimeSource = TimeSource.Monotonic
): Flow<T> = flow {
    coroutineScope {
        val context = coroutineContext
        var timeMark = timeSource.markNow()
        var delayEmit: Deferred<Unit>? = null
        var lastEmittedValue: T? = null

        collect { value ->
            if (lastEmittedValue == null) {
                emit(value)
                lastEmittedValue = value
                timeMark = timeSource.markNow()
                return@collect
            }

            if (timeMark.elapsedNow() >= waitMillis) {
                emit(value)
                lastEmittedValue = value
                delayEmit?.cancel()
                timeMark = timeSource.markNow()
            } else {
                delayEmit?.cancel()
                delayEmit = async(Dispatchers.IO) {
                    delay(waitMillis)
                    lastEmittedValue = value
                    withContext(context) {
                        emit(value)
                    }
                    timeMark = timeSource.markNow()
                }
            }
        }
    }
}
