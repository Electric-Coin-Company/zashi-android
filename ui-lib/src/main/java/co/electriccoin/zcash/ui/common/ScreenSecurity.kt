package co.electriccoin.zcash.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

class ScreenSecurity {
    private val mutableReferenceCount: MutableStateFlow<Int> = MutableStateFlow(0)

    val referenceCount = mutableReferenceCount.asStateFlow()

    fun acquire() {
        mutableReferenceCount.update { it + 1 }
    }

    fun release() {
        val after = mutableReferenceCount.updateAndGet { it - 1 }

        if (after < 0) {
            error("Released security reference count too many times")
        }
    }
}

@Suppress("CompositionLocalAllowlist")
val LocalScreenSecurity = compositionLocalOf { ScreenSecurity() }

@Composable
fun SecureScreen() {
    val screenSecurity = LocalScreenSecurity.current
    DisposableEffect(screenSecurity) {
        screenSecurity.acquire()
        onDispose { screenSecurity.release() }
    }
}
