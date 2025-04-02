package co.electriccoin.zcash.ui.screen.restore.date

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun AndroidRestoreBDDate() {
    val vm = koinViewModel<RestoreBDDateViewModel>()
    val state by vm.state.collectAsStateWithLifecycle()
    RestoreBDDateView(state)
    BackHandler { state.onBack() }
}

@Serializable
data object RestoreBDDate
