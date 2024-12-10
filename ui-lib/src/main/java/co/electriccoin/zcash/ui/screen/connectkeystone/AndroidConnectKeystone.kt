package co.electriccoin.zcash.ui.screen.connectkeystone

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.screen.connectkeystone.model.ConnectKeystoneState
import co.electriccoin.zcash.ui.screen.connectkeystone.view.ConnectKeystoneView
import co.electriccoin.zcash.ui.screen.scankeystone.ScanKeystoneSignInRequest
import org.koin.compose.koinInject

@Composable
fun AndroidConnectKeystone() {
    val navigationRouter = koinInject<NavigationRouter>()

    BackHandler {
        navigationRouter.back()
    }

    ConnectKeystoneView(
        state =
            ConnectKeystoneState(
                onViewKeystoneTutorialClicked = {
                    // do nothing
                },
                onBackClick = {
                    navigationRouter.back()
                },
                onContinueClick = {
                    navigationRouter.forward(ScanKeystoneSignInRequest)
                },
            )
    )
}
