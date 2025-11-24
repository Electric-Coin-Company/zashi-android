package co.electriccoin.zcash.ui.screen.hotfix.enhancement

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.design.util.tryRequestFocus
import co.electriccoin.zcash.ui.screen.hotfix.ephemeral.EphemeralHotfixView
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancementHotfixScreen() {
    val vm = koinViewModel<EnhancementHotfixVM>()
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
data object EnhancementHotfixArgs
