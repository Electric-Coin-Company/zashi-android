package co.electriccoin.zcash.ui.common

import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.lifecycleScope
import cash.z.ecc.android.sdk.ext.collectWith
import co.electriccoin.zcash.spackle.EmulatorWtfUtil
import co.electriccoin.zcash.spackle.FirebaseTestLabUtil
import kotlinx.coroutines.flow.map

@Composable
fun ComponentActivity.BindCompLocalProvider(content: @Composable () -> Unit) {
    val screenSecurity = ScreenSecurity()
    observeScreenSecurityFlag(screenSecurity)

    val screenBrightness = ScreenBrightness()
    observeScreenBrightnessFlag(screenBrightness)

    val screenTimeout = ScreenTimeout()
    observeScreenTimeoutFlag(screenTimeout)
    CompositionLocalProvider(
        LocalScreenSecurity provides screenSecurity,
        LocalScreenBrightness provides screenBrightness,
        LocalScreenTimeout provides screenTimeout,
        content = content
    )
}

private fun ComponentActivity.observeScreenSecurityFlag(screenSecurity: ScreenSecurity) {
    screenSecurity.referenceCount.map { it > 0 }.collectWith(lifecycleScope) { isSecure ->
        val isTest =
            FirebaseTestLabUtil.isFirebaseTestLab(applicationContext) ||
                EmulatorWtfUtil.isEmulatorWtf(applicationContext)

        if (isSecure && !isTest) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}

private fun ComponentActivity.observeScreenBrightnessFlag(screenBrightness: ScreenBrightness) {
    screenBrightness.referenceCount.map { it > 0 }.collectWith(lifecycleScope) { maxBrightness ->
        if (maxBrightness) {
            window.attributes =
                window.attributes.apply {
                    this.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
                }
        } else {
            window.attributes =
                window.attributes.apply {
                    this.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                }
        }
    }
}

private fun ComponentActivity.observeScreenTimeoutFlag(screenTimeout: ScreenTimeout) {
    screenTimeout.referenceCount.map { it > 0 }.collectWith(lifecycleScope) { disableTimeout ->
        if (disableTimeout) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
