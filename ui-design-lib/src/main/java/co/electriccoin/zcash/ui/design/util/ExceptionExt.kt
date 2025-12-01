package co.electriccoin.zcash.ui.design.util

fun Throwable.getCausesAsSequence(): Sequence<Exception> {
    return sequence {
        var current: Exception? = this@getCausesAsSequence as? Exception
        while (current != null) {
            yield(current)
            current = current.cause as? Exception
        }
    }
}