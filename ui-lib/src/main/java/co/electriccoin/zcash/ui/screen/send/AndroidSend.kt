@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.screen.send

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cash.z.ecc.sdk.send
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.home.model.spendableBalance
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.send.view.Send

@Composable
internal fun MainActivity.WrapSend(
    goBack: () -> Unit
) {
    WrapSend(this, goBack)
}

@Composable
private fun WrapSend(
    activity: ComponentActivity,
    goBack: () -> Unit
) {
    val walletViewModel by activity.viewModels<WalletViewModel>()

    val synchronizer = walletViewModel.synchronizer.collectAsState().value
    val spendableBalance = walletViewModel.walletSnapshot.collectAsState().value?.spendableBalance()
    val spendingKey = walletViewModel.spendingKey.collectAsState().value
    if (null == synchronizer || null == spendableBalance || null == spendingKey) {
        // Display loading indicator
    } else {
        Send(
            mySpendableBalance = spendableBalance,
            goBack = goBack,
            onCreateAndSend = {
                synchronizer.send(spendingKey, it)

                goBack()
            }
        )
    }
}
