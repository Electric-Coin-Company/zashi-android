package co.electriccoin.zcash.ui.screen.swap

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.design.util.tryRequestFocus
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun SwapScreen() {
    val vm = koinViewModel<SwapVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    val cancelState by vm.cancelState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current
    var hasBeenAutofocused by rememberSaveable {
        val isSwapFirstScreen =
            navController
                .currentBackStackEntry
                ?.destination
                ?.route == SwapArgs::class.qualifiedName
        mutableStateOf(!isSwapFirstScreen)
    }
    state?.let {
        SwapView(
            state = it,
            onSideEffect = { amountFocusRequester ->
                if (!hasBeenAutofocused) {
                    hasBeenAutofocused = amountFocusRequester.tryRequestFocus() ?: true
                }
            }
        )
    }
    BackHandler(state != null) { state?.onBack?.invoke() }
    SwapCancelView(cancelState)
}

@Serializable
data object SwapArgs
