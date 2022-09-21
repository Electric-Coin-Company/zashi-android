@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.screen.seed

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.common.LocalScreenSecurity
import co.electriccoin.zcash.ui.screen.backup.copyToClipboard
import co.electriccoin.zcash.ui.screen.home.viewmodel.SecretState
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.seed.view.Seed

@Composable
internal fun MainActivity.WrapSeed(
    goBack: () -> Unit
) {
    WrapSeed(this, goBack)
}

@Composable
private fun WrapSeed(
    activity: ComponentActivity,
    goBack: () -> Unit
) {
    val screenSecurity = LocalScreenSecurity.current
    DisposableEffect(screenSecurity) {
        screenSecurity.acquire()
        onDispose { screenSecurity.release() }
    }

    val walletViewModel by activity.viewModels<WalletViewModel>()

    val persistableWallet = run {
        val secretState = walletViewModel.secretState.collectAsState().value
        if (secretState is SecretState.Ready) {
            secretState.persistableWallet
        } else {
            null
        }
    }
    val synchronizer = walletViewModel.synchronizer.collectAsState().value
    if (null == synchronizer || null == persistableWallet) {
        // Display loading indicator
    } else {
        Seed(
            persistableWallet = persistableWallet,
            onBack = goBack,
            onCopyToClipboard = {
                copyToClipboard(activity.applicationContext, persistableWallet)
            }
        )
    }
}
