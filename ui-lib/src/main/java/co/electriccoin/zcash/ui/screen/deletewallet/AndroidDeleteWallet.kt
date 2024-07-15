package co.electriccoin.zcash.ui.screen.deletewallet

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.deletewallet.view.DeleteWallet
import kotlinx.coroutines.launch

@Composable
internal fun MainActivity.WrapDeleteWallet(goBack: () -> Unit) {
    val walletViewModel = koinActivityViewModel<WalletViewModel>()

    val walletState = walletViewModel.walletStateInformation.collectAsStateWithLifecycle().value

    WrapDeleteWallet(
        activity = this,
        goBack = goBack,
        topAppBarSubTitleState = walletState,
        walletViewModel = walletViewModel,
    )
}

@Composable
internal fun WrapDeleteWallet(
    activity: Activity,
    goBack: () -> Unit,
    topAppBarSubTitleState: TopAppBarSubTitleState,
    walletViewModel: WalletViewModel,
) {
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    BackHandler {
        goBack()
    }

    DeleteWallet(
        snackbarHostState = snackbarHostState,
        onBack = goBack,
        onConfirm = {
            scope.launch {
                walletViewModel.deleteWalletFlow(activity).collect { isWalletDeleted ->
                    if (isWalletDeleted) {
                        Twig.info { "Wallet deleted successfully" }
                        // The app flows move to the Onboarding screens reactively
                    } else {
                        Twig.error { "Wallet deletion failed" }
                        snackbarHostState.showSnackbar(
                            message = activity.getString(R.string.delete_wallet_failed)
                        )
                    }
                }
            }
        },
        topAppBarSubTitleState = topAppBarSubTitleState,
    )
}
