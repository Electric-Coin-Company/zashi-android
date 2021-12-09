package cash.z.ecc.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import cash.z.ecc.sdk.model.PersistableWallet
import cash.z.ecc.ui.screen.backup.view.BackupWallet
import cash.z.ecc.ui.screen.backup.viewmodel.BackupViewModel
import cash.z.ecc.ui.screen.common.GradientSurface
import cash.z.ecc.ui.screen.home.view.Home
import cash.z.ecc.ui.screen.home.viewmodel.WalletState
import cash.z.ecc.ui.screen.home.viewmodel.WalletViewModel
import cash.z.ecc.ui.screen.onboarding.view.Onboarding
import cash.z.ecc.ui.screen.onboarding.viewmodel.OnboardingViewModel
import cash.z.ecc.ui.theme.ZcashTheme
import cash.z.ecc.ui.util.AndroidApiVersion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        if (AndroidApiVersion.isAtLeastO) {
            setupUiContent()
        } else {
            lifecycleScope.launch {
                prefetchFontLegacy(applicationContext, R.font.rubik_medium)
                prefetchFontLegacy(applicationContext, R.font.rubik_regular)

                setupUiContent()
            }
        }
    }

    private fun setupSplashScreen() {
        val splashScreen = installSplashScreen()
        val start = SystemClock.elapsedRealtime().milliseconds

        splashScreen.setKeepVisibleCondition {
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

    private fun setupUiContent() {
        setContent {
            ZcashTheme {
                GradientSurface(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
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

/**
 * Pre-fetches fonts on Android N (API 25) and below.
 */
/*
 * ResourcesCompat is used implicitly by Compose on older Android versions.
 * The backwards compatibility library performs disk IO and then
 * caches the results.  This moves that IO off the main thread, to prevent ANRs and
 * jank during app startup.
 */
private suspend fun prefetchFontLegacy(context: Context, @androidx.annotation.FontRes fontRes: Int) =
    withContext(Dispatchers.IO) {
        ResourcesCompat.getFont(context, fontRes)
    }
