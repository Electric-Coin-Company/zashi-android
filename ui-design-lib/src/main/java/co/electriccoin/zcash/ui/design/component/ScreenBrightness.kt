package co.electriccoin.zcash.ui.design.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ScreenBrightnessState { FULL, NORMAL }

class ScreenBrightness {
    private val mutableSwitch: MutableStateFlow<ScreenBrightnessState> = MutableStateFlow(ScreenBrightnessState.NORMAL)

    val referenceSwitch = mutableSwitch.asStateFlow()

    fun fullBrightness() = mutableSwitch.update { ScreenBrightnessState.FULL }

    fun restoreBrightness() = mutableSwitch.update { ScreenBrightnessState.NORMAL }
}

@Suppress("CompositionLocalAllowlist")
val LocalScreenBrightness = staticCompositionLocalOf { ScreenBrightness() }

@Composable
fun BrightenScreen() {
    val screenBrightness = LocalScreenBrightness.current
    DisposableEffect(screenBrightness) {
        screenBrightness.fullBrightness()
        onDispose { screenBrightness.restoreBrightness() }
    }
}

@Composable
fun RestoreScreenBrightness() {
    val screenBrightness = LocalScreenBrightness.current
    screenBrightness.restoreBrightness()
}
