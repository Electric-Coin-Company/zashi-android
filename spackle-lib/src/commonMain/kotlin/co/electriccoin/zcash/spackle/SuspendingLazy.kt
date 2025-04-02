package co.electriccoin.zcash.spackle

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Implements a coroutines-friendly lazy singleton pattern with an input argument.
 *
 * This class is thread-safe.
 */
class SuspendingLazy<in Input, out Output>(
    private val deferredCreator: suspend ((Input) -> Output)
) {
    private var singletonInstance: Output? = null

    private val mutex = Mutex()

    suspend fun getInstance(input: Input): Output {
        mutex.withLock {
            singletonInstance?.let {
                return it
            }

            val newInstance = deferredCreator(input)
            singletonInstance = newInstance

            return newInstance
        }
    }
}
