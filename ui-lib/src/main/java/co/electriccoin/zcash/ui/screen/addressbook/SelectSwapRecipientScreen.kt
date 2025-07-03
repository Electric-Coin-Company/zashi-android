package co.electriccoin.zcash.ui.screen.addressbook

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@Composable
fun SelectSwapRecipientScreen(args: SelectSwapRecipientArgs) {
    val viewModel = koinViewModel<SelectSwapRecipientVM> { parametersOf(args) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    BackHandler { state.onBack() }
    AddressBookView(state = state)
}

@Serializable
data class SelectSwapRecipientArgs(
    val requestId: String = UUID.randomUUID().toString()
)
