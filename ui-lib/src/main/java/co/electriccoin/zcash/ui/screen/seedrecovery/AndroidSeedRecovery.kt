package co.electriccoin.zcash.ui.screen.seedrecovery

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.spackle.ClipboardManagerUtil
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.viewmodel.SecretState
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.seedrecovery.view.SeedRecovery

@Composable
internal fun MainActivity.WrapSeedRecovery(
    goBack: () -> Unit,
    onDone: () -> Unit
) {
    WrapSeedRecovery(this, goBack, onDone)
}

@Composable
private fun WrapSeedRecovery(
    activity: ComponentActivity,
    goBack: () -> Unit,
    onDone: () -> Unit
) {
    val walletViewModel by activity.viewModels<WalletViewModel>()

    val persistableWallet = run {
        val secretState = walletViewModel.secretState.collectAsStateWithLifecycle().value
        if (secretState is SecretState.Ready) {
            secretState.persistableWallet
        } else {
            null
        }
    }
    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    if (null == synchronizer || null == persistableWallet) {
        // Display loading indicator
    } else {
        SeedRecovery(
            persistableWallet,
            onBack = goBack,
            onSeedCopy = {
                ClipboardManagerUtil.copyToClipboard(
                    activity.applicationContext,
                    activity.getString(R.string.seed_recovery_seed_clipboard_tag),
                    persistableWallet.seedPhrase.joinToString()
                )
            },
            onBirthdayCopy = {
                ClipboardManagerUtil.copyToClipboard(
                    activity.applicationContext,
                    activity.getString(R.string.seed_recovery_birthday_clipboard_tag),
                    persistableWallet.birthday?.value.toString()
                )
            },
            onDone = onDone
        )
    }
}
