package co.electriccoin.zcash.ui.screen.newwalletrecovery

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import cash.z.ecc.android.sdk.model.PersistableWallet
import co.electriccoin.zcash.spackle.ClipboardManagerUtil
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.screen.newwalletrecovery.view.NewWalletRecovery

@Composable
fun MainActivity.WrapNewWalletRecovery(
    persistableWallet: PersistableWallet,
    onBackupComplete: () -> Unit
) {
    WrapNewWalletRecovery(this, persistableWallet, onBackupComplete)
}

@Composable
private fun WrapNewWalletRecovery(
    activity: ComponentActivity,
    persistableWallet: PersistableWallet,
    onBackupComplete: () -> Unit
) {
    val versionInfo = VersionInfo.new(activity.applicationContext)

    NewWalletRecovery(
        persistableWallet,
        onSeedCopy = {
            ClipboardManagerUtil.copyToClipboard(
                activity.applicationContext,
                activity.getString(R.string.new_wallet_recovery_seed_clipboard_tag),
                persistableWallet.seedPhrase.joinToString()
            )
        },
        onBirthdayCopy = {
            ClipboardManagerUtil.copyToClipboard(
                activity.applicationContext,
                activity.getString(R.string.new_wallet_recovery_birthday_clipboard_tag),
                persistableWallet.birthday?.value.toString()
            )
        },
        onComplete = onBackupComplete,
        versionInfo = versionInfo
    )
}
