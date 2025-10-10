package co.electriccoin.zcash.ui.common.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

class ScreenTimeout {
    private val mutableReferenceCount: MutableStateFlow<Int> = MutableStateFlow(0)

    val referenceCount = mutableReferenceCount.asStateFlow()

    fun disableScreenTimeout() {
        mutableReferenceCount.update { it + 1 }
    }

    fun restoreTimeout() {
        val after = mutableReferenceCount.updateAndGet { it - 1 }
        if (after < 0) {
            error("Restored timeout reference count too many times")
        }
    }
}

@Suppress("CompositionLocalAllowlist")
val LocalScreenTimeout = staticCompositionLocalOf { ScreenTimeout() }

@Composable
fun DisableScreenTimeout() {
    val screenTimeout = LocalScreenTimeout.current
    DisposableEffect(screenTimeout) {
        screenTimeout.disableScreenTimeout()
        onDispose { screenTimeout.restoreTimeout() }
    }
}
