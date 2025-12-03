package co.electriccoin.zcash.ui.screen.more

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun MoreScreen() {
    val vm = koinViewModel<MoreVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    BackHandler { state.onBack() }
    MoreView(state = state)
}

@Serializable
data object MoreArgs
