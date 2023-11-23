package co.electriccoin.zcash.ui.screen.newwalletrecovery

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import cash.z.ecc.android.sdk.model.PersistableWallet
import co.electriccoin.zcash.spackle.ClipboardManagerUtil
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.newwalletrecovery.view.NewWalletRecovery

@Composable
fun MainActivity.WrapNewWalletRecovery(
    persistableWallet: PersistableWallet,
    onBackupComplete: () -> Unit
) {
    WrapNewWalletRecovery(this, persistableWallet, onBackupComplete)
}

// This layer of indirection allows for activity re-creation tests
@Composable
private fun WrapNewWalletRecovery(
    activity: ComponentActivity,
    persistableWallet: PersistableWallet,
    onBackupComplete: () -> Unit
) {
    WrapNewWalletRecovery(
        persistableWallet,
        onSeedCopyToClipboard = {
            ClipboardManagerUtil.copyToClipboard(
                activity.applicationContext,
                activity.getString(R.string.new_wallet_seed_clipboard_tag),
                persistableWallet.seedPhrase.joinToString()
            )
        },
        onBirthdayCopyToClipboard = {
            ClipboardManagerUtil.copyToClipboard(
                activity.applicationContext,
                activity.getString(R.string.new_wallet_birthday_clipboard_tag),
                persistableWallet.birthday?.value.toString()
            )
        },
        onNewWalletComplete = onBackupComplete
    )
}

// This extra layer of indirection allows unit tests to validate the screen state retention
@Composable
private fun WrapNewWalletRecovery(
    persistableWallet: PersistableWallet,
    onSeedCopyToClipboard: () -> Unit,
    onBirthdayCopyToClipboard: () -> Unit,
    onNewWalletComplete: () -> Unit
) {
    NewWalletRecovery(
        persistableWallet,
        onSeedCopy = onSeedCopyToClipboard,
        onBirthdayCopy = onBirthdayCopyToClipboard,
        onComplete = onNewWalletComplete,
    )
}
