package co.electriccoin.zcash.ui.screen.texunsupported

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidTEXUnsupported() {
    val vm = koinViewModel<TEXUnsupportedVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    TEXUnsupportedView(state)
}

@Serializable
data object TEXUnsupportedArgs
