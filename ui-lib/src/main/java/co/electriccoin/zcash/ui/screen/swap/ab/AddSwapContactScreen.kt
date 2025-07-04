@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.swap.ab

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.screen.contact.ContactView
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun AddSwapContactScreen(args: AddSwapContactArgs) {
    val vm = koinViewModel<AddSwapContactVM> { parametersOf(args) }
    val state by vm.state.collectAsStateWithLifecycle()
    BackHandler(state != null) { state?.onBack?.invoke() }
    state?.let { ContactView(state = it) }
}

@Serializable
data class AddSwapContactArgs(
    val address: String?,
    val chain: String?
)
