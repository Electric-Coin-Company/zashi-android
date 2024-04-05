package co.electriccoin.zcash.ui.screen.settings.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import co.electriccoin.zcash.ui.common.compose.ScreenBrightness
import kotlinx.coroutines.flow.MutableStateFlow

class ScreenBrightnessViewModel(application: Application) : AndroidViewModel(application) {
    private val screenBrightness: MutableStateFlow<ScreenBrightness> = MutableStateFlow(ScreenBrightness)

    val screenBrightnessState = screenBrightness.value.referenceSwitch

    fun fullBrightness() {
        screenBrightness.value.fullBrightness()
    }

    fun restoreBrightness() {
        screenBrightness.value.restoreBrightness()
    }
}
