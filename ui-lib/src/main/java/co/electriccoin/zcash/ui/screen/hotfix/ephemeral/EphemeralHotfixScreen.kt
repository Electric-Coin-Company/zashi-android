package co.electriccoin.zcash.ui.screen.hotfix.ephemeral

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EphemeralHotfixScreen(args: EphemeralHotfixArgs) {
    val vm = koinViewModel<EphemeralHotfixVM> { parametersOf(args) }
    val state by vm.state.collectAsStateWithLifecycle()
    EphemeralHotfixView(state)
}

@Serializable
data class EphemeralHotfixArgs(val address: String?)
