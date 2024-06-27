package co.electriccoin.zcash.ui.screen.newwalletrecovery

import androidx.compose.runtime.Composable
import cash.z.ecc.android.sdk.model.PersistableWallet
import co.electriccoin.zcash.spackle.ClipboardManagerUtil
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.screen.newwalletrecovery.view.NewWalletRecovery

@Composable
fun WrapNewWalletRecovery(
    persistableWallet: PersistableWallet,
    onBackupComplete: () -> Unit
) {
    val activity = LocalActivity.current

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
