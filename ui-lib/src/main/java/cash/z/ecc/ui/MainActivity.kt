package cash.z.ecc.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cash.z.ecc.sdk.model.PersistableWallet
import cash.z.ecc.ui.screen.backup.view.BackupWallet
import cash.z.ecc.ui.screen.backup.viewmodel.BackupViewModel
import cash.z.ecc.ui.screen.home.view.Home
import cash.z.ecc.ui.screen.home.viewmodel.WalletState
import cash.z.ecc.ui.screen.home.viewmodel.WalletViewModel
import cash.z.ecc.ui.screen.onboarding.view.Onboarding
import cash.z.ecc.ui.screen.onboarding.viewmodel.OnboardingViewModel
import cash.z.ecc.ui.theme.ZcashTheme

class MainActivity : ComponentActivity() {

    private val walletViewModel by viewModels<WalletViewModel>()

    private val onboardingViewModel by viewModels<OnboardingViewModel>()
    private val backupViewModel by viewModels<BackupViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZcashTheme {
                when (val walletState = walletViewModel.state.collectAsState().value) {
                    WalletState.Loading -> {
                        // For now, keep displaying splash screen
                    }
                    WalletState.NoWallet -> WrapOnboarding()
                    is WalletState.NeedsBackup -> WrapBackup(walletState.persistableWallet)
                    is WalletState.Ready -> Home(walletState.persistableWallet)
                }
            }
        }
    }

    @Composable
    private fun WrapBackup(persistableWallet: PersistableWallet) {
        BackupWallet(
            persistableWallet, backupViewModel.backupState, backupViewModel.testChoices,
            onCopyToClipboard = {
                val clipboardManager = getSystemService(ClipboardManager::class.java)
                val data = ClipData.newPlainText(
                    getString(R.string.new_wallet_clipboard_tag),
                    persistableWallet.seedPhrase.phrase
                )
                clipboardManager.setPrimaryClip(data)
            }, onComplete = {
            walletViewModel.persistBackupComplete()
        }
        )
    }

    @Composable
    private fun WrapOnboarding() {
        Onboarding(
            onboardingState = onboardingViewModel.onboardingState,
            onImportWallet = { TODO("Implement wallet import") },
            onCreateWallet = {
                walletViewModel.createAndPersistWallet()
            }
        )
    }
}
