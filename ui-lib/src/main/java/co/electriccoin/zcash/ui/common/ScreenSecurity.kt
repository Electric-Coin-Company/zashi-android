package co.electriccoin.zcash.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.BuildConfig
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

/**
 * Returns true only if it's used from the automated Android UI testing.
 */
val isRunningTest: Boolean by lazy {
    runCatching {
        Class.forName("androidx.test.espresso.Espresso")
        true
    }.getOrDefault(false).also {
        Twig.debug { "Running in UI test: $it" }
    }
}

/**
 * Decides whether the SecureScreen should be activated depending on [isRunningTest] and Gradle
 * [BuildConfig.IS_SECURE_SCREEN_ENABLED].
 */
val shouldSecureScreen: Boolean by lazy {
    if (isRunningTest) {
        true
    } else {
        BuildConfig.IS_SECURE_SCREEN_ENABLED
    }
}

@Composable
fun SecureScreen() {
    val screenSecurity = LocalScreenSecurity.current
    DisposableEffect(screenSecurity) {
        screenSecurity.acquire()
        onDispose { screenSecurity.release() }
    }
}
