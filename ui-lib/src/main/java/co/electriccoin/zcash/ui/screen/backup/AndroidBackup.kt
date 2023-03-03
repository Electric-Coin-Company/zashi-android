package co.electriccoin.zcash.ui.screen.backup

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import cash.z.ecc.android.sdk.model.PersistableWallet
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.configuration.ConfigurationEntries
import co.electriccoin.zcash.ui.configuration.RemoteConfig
import co.electriccoin.zcash.ui.screen.backup.ext.Saver
import co.electriccoin.zcash.ui.screen.backup.state.BackupState
import co.electriccoin.zcash.ui.screen.backup.state.TestChoices
import co.electriccoin.zcash.ui.screen.backup.view.LongNewWalletBackup
import co.electriccoin.zcash.ui.screen.backup.view.ShortNewWalletBackup

@Composable
internal fun MainActivity.WrapNewWallet(
    persistableWallet: PersistableWallet,
    onBackupComplete: () -> Unit
) {
    if (ConfigurationEntries.IS_SHORT_NEW_WALLET_BACKUP_UX.getValue(RemoteConfig.current)) {
        WrapShortNewWallet(
            persistableWallet,
            onBackupComplete = onBackupComplete
        )
    } else {
        WrapLongNewWallet(
            persistableWallet,
            onBackupComplete = onBackupComplete
        )
    }
}

@Composable
internal fun MainActivity.WrapLongNewWallet(
    persistableWallet: PersistableWallet,
    onBackupComplete: () -> Unit
) {
    WrapLongNewWallet(this, persistableWallet, onBackupComplete)
}

// This layer of indirection allows for activity re-creation tests
@Composable
internal fun WrapLongNewWallet(
    activity: ComponentActivity,
    persistableWallet: PersistableWallet,
    onBackupComplete: () -> Unit
) {
    WrapLongNewWallet(
        persistableWallet,
        onCopyToClipboard = { copyToClipboard(activity.applicationContext, persistableWallet) },
        onBackupComplete = onBackupComplete
    )
}

// This extra layer of indirection allows unit tests to validate the screen state retention.
@Composable
internal fun WrapLongNewWallet(
    persistableWallet: PersistableWallet,
    onCopyToClipboard: () -> Unit,
    onBackupComplete: () -> Unit
) {
    val testChoices by rememberSaveable(stateSaver = TestChoices.Saver) { mutableStateOf(TestChoices()) }
    val backupState by rememberSaveable(stateSaver = BackupState.Saver) { mutableStateOf(BackupState()) }

    LongNewWalletBackup(
        persistableWallet,
        backupState,
        testChoices,
        onCopyToClipboard = onCopyToClipboard,
        onComplete = onBackupComplete,
        null
    )
}

@Composable
private fun MainActivity.WrapShortNewWallet(
    persistableWallet: PersistableWallet,
    onBackupComplete: () -> Unit
) {
    WrapShortNewWallet(this, persistableWallet, onBackupComplete)
}

@Composable
private fun WrapShortNewWallet(
    activity: ComponentActivity,
    persistableWallet: PersistableWallet,
    onBackupComplete: () -> Unit
) {
    WrapShortNewWallet(
        persistableWallet,
        onCopyToClipboard = { copyToClipboard(activity.applicationContext, persistableWallet) },
        onNewWalletComplete = onBackupComplete
    )
}

@Composable
private fun WrapShortNewWallet(
    persistableWallet: PersistableWallet,
    onCopyToClipboard: () -> Unit,
    onNewWalletComplete: () -> Unit
) {
    ShortNewWalletBackup(
        persistableWallet,
        onCopyToClipboard = onCopyToClipboard,
        onComplete = onNewWalletComplete,
    )
}

internal fun copyToClipboard(context: Context, persistableWallet: PersistableWallet) {
    val clipboardManager = context.getSystemService(ClipboardManager::class.java)
    val data = ClipData.newPlainText(
        context.getString(R.string.new_wallet_clipboard_tag),
        persistableWallet.seedPhrase.joinToString()
    )
    clipboardManager.setPrimaryClip(data)
}
