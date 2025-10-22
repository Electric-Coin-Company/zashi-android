package co.electriccoin.zcash.ui.screen.deletewallet

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.deletewallet.view.DeleteWallet
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun WrapDeleteWallet(
    activity: Activity,
    walletViewModel: WalletViewModel,
) {
    val scope = rememberCoroutineScope()
    val navigationRouter = koinInject<NavigationRouter>()
    val snackbarHostState = remember { SnackbarHostState() }
    BackHandler { navigationRouter.back() }
    DeleteWallet(
        snackbarHostState = snackbarHostState,
        onBack = { navigationRouter.back() },
        onConfirm = {
            walletViewModel.deleteWallet(
                onSuccess = {
                    // do nothing
                },
                onError = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = activity.getString(R.string.delete_wallet_failed)
                        )
                    }
                }
            )
        },
    )
}
