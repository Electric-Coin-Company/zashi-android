package co.electriccoin.zcash.ui.screen.whatsnew

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.design.util.LocalNavController
import co.electriccoin.zcash.ui.screen.whatsnew.view.WhatsNewView
import co.electriccoin.zcash.ui.screen.whatsnew.viewmodel.WhatsNewViewModel

@Composable
fun WrapWhatsNew() {
    val navController = LocalNavController.current
    val viewModel = koinActivityViewModel<WhatsNewViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle().value ?: return

    BackHandler {
        navController.popBackStack()
    }

    WhatsNewView(
        state = state,
        onBack = { navController.popBackStack() }
    )
}
