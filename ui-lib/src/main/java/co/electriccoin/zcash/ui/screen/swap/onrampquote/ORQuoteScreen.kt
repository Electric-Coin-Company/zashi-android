package co.electriccoin.zcash.ui.screen.swap.onrampquote

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun ORQuoteScreen() {
    val vm = koinViewModel<ORQuoteVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    BackHandler { state?.onBack?.invoke() }
    state?.let { ORQuoteView(it) }
}

@Serializable
data object ORQuoteArgs
