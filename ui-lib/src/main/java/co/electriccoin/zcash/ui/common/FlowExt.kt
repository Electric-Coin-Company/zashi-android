package co.electriccoin.zcash.ui.common

import android.os.SystemClock
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

fun <T> Flow<T>.throttle(waitMillis: Int): Flow<T> = flow {
    coroutineScope {
        val context = coroutineContext
        var nextEmitMillis = 0L
        var delayEmit: Deferred<Unit>? = null
        var lastEmittedValue: T? = null

        collect {
            val current = SystemClock.uptimeMillis()
            val value = it

            if (lastEmittedValue == null) {
                nextEmitMillis = current + waitMillis
                emit(value)
                lastEmittedValue = value
                return@collect
            }

            if (nextEmitMillis < current) {
                nextEmitMillis = current + waitMillis
                emit(value)
                lastEmittedValue = value
                delayEmit?.cancel()
            } else {
                delayEmit?.cancel()
                delayEmit = async(Dispatchers.IO) {
                    delay(waitMillis.toLong())
                    lastEmittedValue = value
                    withContext(context) {
                        emit(value)
                    }
                }
            }
        }
    }
}
