package co.electriccoin.zcash.ui.screen.addressbook

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun SelectABRecipientScreen() {
    val viewModel = koinViewModel<SelectABRecipientVM>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    BackHandler { state.onBack() }
    AddressBookView(state = state)
}

@Serializable
data object SelectABRecipientArgs
