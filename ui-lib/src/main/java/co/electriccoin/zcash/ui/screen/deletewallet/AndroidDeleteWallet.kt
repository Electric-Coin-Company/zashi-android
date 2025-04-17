package co.electriccoin.zcash.ui.screen.deletewallet

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.deletewallet.view.DeleteWallet
import kotlinx.coroutines.launch

@Composable
internal fun MainActivity.WrapDeleteWallet(
    goBack: () -> Unit,
    onConfirm: () -> Unit,
) {
    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    WrapDeleteWallet(
        activity = this,
        goBack = goBack,
        onConfirm = onConfirm,
        walletViewModel = walletViewModel
    )
}

@Composable
internal fun WrapDeleteWallet(
    activity: Activity,
    goBack: () -> Unit,
    onConfirm: () -> Unit,
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
            walletViewModel.deleteWallet(
                onSuccess = {
                    onConfirm()
                    activity.finish()
                    activity.startActivity(Intent(activity, MainActivity::class.java))
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
