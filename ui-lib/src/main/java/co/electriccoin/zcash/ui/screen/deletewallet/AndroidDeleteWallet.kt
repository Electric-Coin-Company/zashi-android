package co.electriccoin.zcash.ui.screen.deletewallet

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.deletewallet.view.DeleteWallet
import kotlinx.coroutines.launch

@Composable
internal fun MainActivity.WrapDeleteWallet(goBack: () -> Unit) {
    val walletViewModel by viewModels<WalletViewModel>()

    val walletRestoringState = walletViewModel.walletRestoringState.collectAsStateWithLifecycle().value

    WrapDeleteWallet(
        this,
        goBack = goBack,
        walletRestoringState = walletRestoringState,
        walletViewModel = walletViewModel,
    )
}

@Composable
internal fun WrapDeleteWallet(
    context: Context,
    goBack: () -> Unit,
    walletRestoringState: WalletRestoringState,
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
                walletViewModel.deleteWalletFlow().collect { isWalletDeleted ->
                    if (isWalletDeleted) {
                        Twig.info { "Wallet deleted successfully" }
                        // The app flows move to the Onboarding screens reactively
                    } else {
                        Twig.error { "Wallet deletion failed" }
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.delete_wallet_failed)
                        )
                    }
                }
            }
        },
        walletRestoringState = walletRestoringState
    )
}
