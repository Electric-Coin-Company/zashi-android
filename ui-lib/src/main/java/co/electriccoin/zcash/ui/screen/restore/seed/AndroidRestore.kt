package co.electriccoin.zcash.ui.screen.restore.seed

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun AndroidRestoreSeed() {
    val vm = koinViewModel<RestoreSeedViewModel>()
    val state by vm.state.collectAsStateWithLifecycle()
    val suggestionsState = vm.suggestionsState.collectAsStateWithLifecycle().value
    if (state != null && suggestionsState != null) {
        state?.let { RestoreSeedView(it, suggestionsState) }
    }

    BackHandler { state?.onBack?.invoke() }
}

@Serializable
data object RestoreSeed
