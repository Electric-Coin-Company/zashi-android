package cash.z.ecc.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cash.z.ecc.sdk.model.PersistableWallet
import cash.z.ecc.ui.screen.backup.view.BackupWallet
import cash.z.ecc.ui.screen.backup.viewmodel.BackupViewModel
import cash.z.ecc.ui.screen.home.view.Home
import cash.z.ecc.ui.screen.home.viewmodel.WalletState
import cash.z.ecc.ui.screen.home.viewmodel.WalletViewModel
import cash.z.ecc.ui.screen.onboarding.view.Onboarding
import cash.z.ecc.ui.screen.onboarding.viewmodel.OnboardingViewModel
import cash.z.ecc.ui.theme.ZcashTheme
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {

    private val walletViewModel by viewModels<WalletViewModel>()

    private val onboardingViewModel by viewModels<OnboardingViewModel>()
    private val backupViewModel by viewModels<BackupViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupSplashScreen()

        setContent {
            ZcashTheme {
                val walletState = walletViewModel.state.collectAsState().value

                when (walletState) {
                    WalletState.Loading -> {
                        // For now, keep displaying splash screen using condition above.
                        // In the future, we might consider displaying something different here.
                    }
                    WalletState.NoWallet -> {
                        WrapOnboarding()
                    }
                    is WalletState.NeedsBackup -> WrapBackup(walletState.persistableWallet)
                    is WalletState.Ready -> WrapHome(walletState.persistableWallet)
                }

                if (walletState != WalletState.Loading) {
                    reportFullyDrawn()
                }
            }
        }
    }

    private fun setupSplashScreen() {
        installSplashScreen().also {
            val start = SystemClock.elapsedRealtime().milliseconds
            it.setKeepVisibleCondition {
                if (SPLASH_SCREEN_DELAY > Duration.ZERO) {
                    val now = SystemClock.elapsedRealtime().milliseconds

                    // This delay is for debug purposes only; do not enable for production usage.
                    if (now - start < SPLASH_SCREEN_DELAY) {
                        return@setKeepVisibleCondition true
                    }
                }

                WalletState.Loading == walletViewModel.state.value
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

    @Composable
    private fun WrapHome(persistableWallet: PersistableWallet) {
        Home(persistableWallet)
    }

    companion object {
        @VisibleForTesting
        internal val SPLASH_SCREEN_DELAY = 0.seconds
    }
}
