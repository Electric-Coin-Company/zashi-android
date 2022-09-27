package co.electriccoin.zcash.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

class ScreenBrightness {
    private val mutableReferenceCount: MutableStateFlow<Int> = MutableStateFlow(0)

    val referenceCount = mutableReferenceCount.asStateFlow()

    fun fullBrightness() {
        mutableReferenceCount.update { it + 1 }
    }

    fun restore() {
        val after = mutableReferenceCount.updateAndGet { it - 1 }

        if (after < 0) {
            error("Released security reference count too many times")
        }
    }
}

val LocalScreenBrightness = compositionLocalOf { ScreenBrightness() }

@Composable
fun BrightenScreen() {
    val screenBrightness = LocalScreenBrightness.current
    DisposableEffect(screenBrightness) {
        screenBrightness.fullBrightness()
        onDispose { screenBrightness.restore() }
    }
}
