package co.electriccoin.zcash.ui.common.model

import cash.z.ecc.android.sdk.model.BlockHeight
import co.electriccoin.zcash.ui.common.viewmodel.STACKTRACE_LIMIT

/**
 * Represents all kind of Synchronizer errors
 */
sealed interface SynchronizerError {

    val cause: Throwable?

    class Critical(override val cause: Throwable?) : SynchronizerError

    class Processor(override val cause: Throwable?) : SynchronizerError

    class Submission(override val cause: Throwable?) : SynchronizerError

    class Setup(override val cause: Throwable?) : SynchronizerError

    class Chain(val x: BlockHeight, val y: BlockHeight) : SynchronizerError {
        override val cause: Throwable? = null
    }

    fun getStackTrace(limit: Int? = STACKTRACE_LIMIT): String? =
        if (limit != null) {
            cause?.stackTraceToLimitedString(limit)
        } else {
            cause?.stackTraceToString()
        }
}

private fun Throwable.stackTraceToLimitedString(limit: Int) =
    if (stackTraceToString().isNotEmpty()) {
        stackTraceToString().substring(0..(stackTraceToString().length - 1).coerceAtMost(limit))
    } else {
        null
    }