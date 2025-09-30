package co.electriccoin.zcash.ui.design

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Stable
class SheetStateManager {
    private var sheetState: SheetState? = null

    fun onSheetOpened(sheetState: SheetState) {
        this.sheetState = sheetState
    }

    fun onSheetDisposed(sheetState: SheetState) {
        if (this.sheetState === sheetState) {
            this.sheetState = null
        }
    }

    suspend fun hide() {
        try {
            withTimeoutOrNull(.5.seconds) {
                sheetState?.hide()
            }
        } catch (_: Exception) {
            // ignore
        }
    }
}

@Composable
fun rememberSheetStateManager() = remember { SheetStateManager() }

@Suppress("CompositionLocalAllowlist")
val LocalSheetStateManager =
    compositionLocalOf<SheetStateManager> {
        error("Sheet state manager not provided")
    }
