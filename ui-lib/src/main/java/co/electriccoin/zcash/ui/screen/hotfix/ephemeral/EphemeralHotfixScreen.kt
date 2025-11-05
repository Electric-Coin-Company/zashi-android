package co.electriccoin.zcash.ui.screen.hotfix.ephemeral

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.design.util.tryRequestFocus
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EphemeralHotfixScreen(args: EphemeralHotfixArgs) {
    val vm = koinViewModel<EphemeralHotfixVM> { parametersOf(args) }
    val state by vm.state.collectAsStateWithLifecycle()
    var hasBeenAutofocused by rememberSaveable { mutableStateOf(false) }
    EphemeralHotfixView(
        state = state,
        onSheetOpen = { focusRequester ->
            if (!hasBeenAutofocused) {
                hasBeenAutofocused = focusRequester.tryRequestFocus() ?: true
            }
        }
    )
}

@Serializable
data class EphemeralHotfixArgs(
    val address: String?
)
