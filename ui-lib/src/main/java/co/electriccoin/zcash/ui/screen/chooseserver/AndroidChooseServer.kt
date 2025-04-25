@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.chooseserver

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WrapChooseServer() {
    val navController = LocalNavController.current
    val viewModel = koinViewModel<ChooseServerViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    BackHandler {
        if (viewModel.canGoBack()) {
            navController.popBackStack()
        }
    }

    ChooseServerView(
        state = state,
        onBack = {
            if (viewModel.canGoBack()) {
                navController.popBackStack()
            }
        },
    )
}
