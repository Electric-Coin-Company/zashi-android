package co.electriccoin.zcash.ui.util

import co.electriccoin.zcash.spackle.Twig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

interface CloseableScopeHolder : Closeable {
    val scope: CoroutineScope
}

class CloseableScopeHolderImpl(
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
) : CloseableScopeHolder {
    constructor(coroutineContext: CoroutineContext) : this(CoroutineScope(coroutineContext + SupervisorJob()))

    override fun close() {
        try {
            scope.cancel()
        } catch (e: IllegalStateException) {
            Twig.error(e) { "Failed to close scope" }
        }
    }
}

/**
 * A function to call during koin element lifecycle close action.
 */
fun <T> closeableCallback(t: T?) {
    (t as? Closeable)?.close()
}
