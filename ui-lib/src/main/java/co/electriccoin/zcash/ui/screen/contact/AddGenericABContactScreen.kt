@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.contact

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.design.util.tryRequestFocus
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun AddGenericABContactScreen(args: AddGenericABContactArgs) {
    val vm = koinViewModel<AddGenericABContactVM> { parametersOf(args) }
    val state by vm.state.collectAsStateWithLifecycle()
    BackHandler(state != null) { state?.onBack?.invoke() }
    var hasBeenAutofocused by rememberSaveable { mutableStateOf(false) }
    state?.let {
        ABContactView(
            state = it,
            onSideEffect = { nameFocusRequester, addressFocusRequester ->
                if (!hasBeenAutofocused) {
                    if (args.address == null) {
                        addressFocusRequester.tryRequestFocus() ?: true
                    } else {
                        nameFocusRequester.tryRequestFocus() ?: true
                    }
                    hasBeenAutofocused = true
                }
            }
        )
    }
}

@Serializable
data class AddGenericABContactArgs(
    val address: String?
)
