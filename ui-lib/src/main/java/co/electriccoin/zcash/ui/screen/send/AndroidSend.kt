@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.screen.send

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.sdk.send
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.home.model.spendableBalance
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.send.view.Send
import kotlinx.coroutines.launch

@Composable
internal fun MainActivity.WrapSend(
    goBack: () -> Unit
) {
    WrapSend(this, goBack)
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
private fun WrapSend(
    activity: ComponentActivity,
    goBack: () -> Unit
) {
    val walletViewModel by activity.viewModels<WalletViewModel>()
    val scope = rememberCoroutineScope()

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    val spendableBalance = walletViewModel.walletSnapshot.collectAsStateWithLifecycle().value?.spendableBalance()

    val spendingKey = walletViewModel.spendingKey.collectAsStateWithLifecycle().value
    if (null == synchronizer || null == spendableBalance || null == spendingKey) {
        // Display loading indicator
    } else {
        Send(
            mySpendableBalance = spendableBalance,
            goBack = goBack,
            onCreateAndSend = {
                scope.launch {
                    synchronizer.send(spendingKey, it)
                    goBack()
                }
            }
        )
    }
}
