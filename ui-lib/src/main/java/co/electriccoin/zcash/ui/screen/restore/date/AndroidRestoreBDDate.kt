package co.electriccoin.zcash.ui.screen.restore.date

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
fun AndroidRestoreBDDate() {
    val vm = koinViewModel<RestoreBDDateViewModel>()
    val state by vm.state.collectAsStateWithLifecycle()
    val dialogState by vm.dialogState.collectAsStateWithLifecycle()
    RestoreBDDateView(state)
    BackHandler { state.onBack() }
    RestoreSeedDialog(dialogState)
}

@Serializable
data object RestoreBDDate
