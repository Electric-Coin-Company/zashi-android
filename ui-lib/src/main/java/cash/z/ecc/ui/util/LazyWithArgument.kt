package cash.z.ecc.ui.util

/**
 * Implements a lazy singleton pattern with an input argument.
 *
 * This class is thread-safe.
 */
class LazyWithArgument<in Input, out Output>(private val deferredCreator: ((Input) -> Output)) {
    @Volatile
    private var singletonInstance: Output? = null

    private val intrinsicLock = Any()

    fun getInstance(input: Input): Output {
        /*
         * Double-checked idiom for lazy initialization, Effective Java 2nd edition page 283.
         */

        var localSingletonInstance = singletonInstance
        if (null == localSingletonInstance) {
            synchronized(intrinsicLock) {
                localSingletonInstance = singletonInstance

                if (null == localSingletonInstance) {
                    localSingletonInstance = deferredCreator(input)
                    singletonInstance = localSingletonInstance
                }
            }
        }

        return localSingletonInstance!!
    }
}
