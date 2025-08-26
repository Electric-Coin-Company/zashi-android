package co.electriccoin.zcash.ui.screen.swap.ab

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookView
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@Composable
fun SelectSwapABRecipientScreen(args: SelectABSwapRecipientArgs) {
    val viewModel = koinViewModel<SelectSwapABRecipientVM> { parametersOf(args) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    BackHandler { state.onBack() }
    AddressBookView(state = state)
}

@Serializable
data class SelectABSwapRecipientArgs(
    val requestId: String = UUID.randomUUID().toString()
)
