package co.electriccoin.zcash.ui.screen.signkeystonetransaction

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignKeystoneTransactionScreen() {
    val vm = koinViewModel<SignKeystoneTransactionVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    val bottomSheetState by vm.bottomSheetState.collectAsStateWithLifecycle()
    BackHandler(state != null) { state?.onBack?.invoke() }
    state?.let { SignKeystoneTransactionView(it) }
    SignKeystoneTransactionBottomSheet(state = bottomSheetState)
}

@Serializable
object SignKeystoneTransactionArgs
