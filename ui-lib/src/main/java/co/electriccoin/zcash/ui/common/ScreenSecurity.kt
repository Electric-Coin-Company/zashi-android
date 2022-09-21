package co.electriccoin.zcash.ui.common

import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class ScreenSecurity(var secureCounter: MutableStateFlow<Int>) {
    fun accessSecure() {
        secureCounter.update { it + 1 }
    }

    fun releaseSecure() {
        secureCounter.update { it - 1 }
    }

    suspend fun isSecure(scope: CoroutineScope) = secureCounter.map { it > 0 }.stateIn(scope)
}

val LocalScreenSecurity = compositionLocalOf { ScreenSecurity(MutableStateFlow(0)) }