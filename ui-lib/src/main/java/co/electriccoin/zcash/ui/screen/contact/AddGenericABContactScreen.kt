@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.contact

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun AddGenericABContactScreen(args: AddGenericABContactArgs) {
    val vm = koinViewModel<AddGenericABContactVM> { parametersOf(args) }
    val state by vm.state.collectAsStateWithLifecycle()
    BackHandler(state != null) { state?.onBack?.invoke() }
    val addressFocusRequester = remember { FocusRequester() }
    val nameFocusRequester = remember { FocusRequester() }
    var hasBeenAutofocused by rememberSaveable { mutableStateOf(false) }
    state?.let {
        ABContactView(
            state = it,
            addressFocusRequester = addressFocusRequester,
            nameFocusRequester = nameFocusRequester
        )
        LaunchedEffect(Unit) {
            if (!hasBeenAutofocused) {
                if (args.address == null) {
                    addressFocusRequester.requestFocus()
                } else {
                    nameFocusRequester.requestFocus()
                }
                hasBeenAutofocused = true
            }
        }
    }
}

@Serializable
data class AddGenericABContactArgs(
    val address: String?,
    val chain: String?
)
