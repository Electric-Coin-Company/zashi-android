package co.electriccoin.zcash.ui.screen.connectkeystone

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.screen.connectkeystone.model.ConnectKeystoneState
import co.electriccoin.zcash.ui.screen.connectkeystone.view.ConnectKeystoneView
import co.electriccoin.zcash.ui.screen.scankeystone.ScanKeystoneSignInRequest

@Composable
fun AndroidConnectKeystone() {
    val navController = LocalNavController.current

    BackHandler {
        navController.popBackStack()
    }

    ConnectKeystoneView(
        state =
            ConnectKeystoneState(
                onViewKeystoneTutorialClicked = {
                    // do nothing
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onContinueClick = {
                    navController.navigate(ScanKeystoneSignInRequest.PATH)
                },
            )
    )
}

object ConnectKeystoneArgs {
    const val PATH = "connect_keystone"
}
