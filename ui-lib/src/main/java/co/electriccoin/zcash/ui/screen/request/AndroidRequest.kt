@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.request

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.screen.request.model.RequestState
import co.electriccoin.zcash.ui.screen.request.view.RequestView
import co.electriccoin.zcash.ui.screen.request.viewmodel.RequestVM
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun WrapRequest(addressType: Int) {
    val requestViewModel = koinViewModel<RequestVM> { parametersOf(addressType) }
    val requestState by requestViewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    BackHandler {
        when (requestState) {
            RequestState.Loading -> {}
            else -> requestViewModel.onBack()
        }
    }

    RequestView(
        state = requestState,
        snackbarHostState = snackbarHostState
    )
}
