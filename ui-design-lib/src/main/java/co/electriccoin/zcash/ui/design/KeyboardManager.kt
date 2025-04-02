package co.electriccoin.zcash.ui.design

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

@Stable
class KeyboardManager(
    isOpen: Boolean,
    private val softwareKeyboardController: SoftwareKeyboardController?
) {
    private var targetState = MutableStateFlow(isOpen)

    var isOpen by mutableStateOf(isOpen)
        private set

    suspend fun close() {
        if (targetState.value) {
            withTimeoutOrNull(.5.seconds) {
                softwareKeyboardController?.hide()
                targetState.filter { !it }.first()
            }
        }
    }

    fun onKeyboardOpened() {
        targetState.update { true }
        isOpen = true
    }

    fun onKeyboardClosed() {
        targetState.update { false }
        isOpen = false
    }
}

@Composable
fun rememberKeyboardManager(): KeyboardManager {
    val isKeyboardOpen by rememberKeyboardState()
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val keyboardManager = remember { KeyboardManager(isKeyboardOpen, softwareKeyboardController) }
    LaunchedEffect(isKeyboardOpen) {
        if (isKeyboardOpen) {
            keyboardManager.onKeyboardOpened()
        } else {
            keyboardManager.onKeyboardClosed()
        }
    }
    return keyboardManager
}

@Composable
private fun rememberKeyboardState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

@Suppress("CompositionLocalAllowlist")
val LocalKeyboardManager =
    compositionLocalOf<KeyboardManager> {
        error("Keyboard manager not provided")
    }
