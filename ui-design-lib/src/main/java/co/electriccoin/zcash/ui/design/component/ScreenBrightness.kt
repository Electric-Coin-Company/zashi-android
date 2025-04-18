package co.electriccoin.zcash.ui.design.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed class ScreenBrightnessState {
    fun getChange(): ScreenBrightnessState =
        when (this) {
            NORMAL -> FULL
            FULL -> NORMAL
        }

    data object FULL : ScreenBrightnessState()

    data object NORMAL : ScreenBrightnessState()
}

object ScreenBrightness {
    private val mutableSwitch: MutableStateFlow<ScreenBrightnessState> = MutableStateFlow(ScreenBrightnessState.NORMAL)

    val referenceSwitch = mutableSwitch.asStateFlow()

    fun fullBrightness() = mutableSwitch.update { ScreenBrightnessState.FULL }

    fun restoreBrightness() = mutableSwitch.update { ScreenBrightnessState.NORMAL }
}

@Suppress("CompositionLocalAllowlist")
val LocalScreenBrightness = compositionLocalOf { ScreenBrightness }

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
