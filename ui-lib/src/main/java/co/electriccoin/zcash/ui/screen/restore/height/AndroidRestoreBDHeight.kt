package co.electriccoin.zcash.ui.screen.restore.height

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.screen.restore.RestoreSeedDialog
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidRestoreBDHeight() {
    val vm = koinViewModel<RestoreBDHeightViewModel>()
    val state by vm.state.collectAsStateWithLifecycle()
    val dialogState by vm.dialogState.collectAsStateWithLifecycle()
    RestoreBDHeightView(state)

    BackHandler {
        state.onBack()
    }

    RestoreSeedDialog(dialogState)
}

@Serializable
data object RestoreBDHeight
