package cash.z.ecc.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.FontRes
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cash.z.ecc.android.sdk.type.WalletBirthday
import cash.z.ecc.android.sdk.type.ZcashNetwork
import cash.z.ecc.sdk.model.PersistableWallet
import cash.z.ecc.sdk.model.SeedPhrase
import cash.z.ecc.sdk.type.fromResources
import cash.z.ecc.ui.screen.backup.view.BackupWallet
import cash.z.ecc.ui.screen.backup.viewmodel.BackupViewModel
import cash.z.ecc.ui.screen.common.GradientSurface
import cash.z.ecc.ui.screen.home.view.Home
import cash.z.ecc.ui.screen.home.viewmodel.SecretState
import cash.z.ecc.ui.screen.home.viewmodel.WalletViewModel
import cash.z.ecc.ui.screen.onboarding.view.Onboarding
import cash.z.ecc.ui.screen.onboarding.viewmodel.OnboardingViewModel
import cash.z.ecc.ui.screen.restore.view.RestoreWallet
import cash.z.ecc.ui.screen.restore.viewmodel.CompleteWordSetState
import cash.z.ecc.ui.screen.restore.viewmodel.RestoreViewModel
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

            SecretState.Loading == walletViewModel.secretState.value
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
                    val secretState = walletViewModel.secretState.collectAsState().value

                    when (secretState) {
                        SecretState.Loading -> {
                            // For now, keep displaying splash screen using condition above.
                            // In the future, we might consider displaying something different here.
                        }
                        SecretState.None -> {
                            WrapOnboarding()
                        }
                        is SecretState.NeedsBackup -> WrapBackup(secretState.persistableWallet)
                        is SecretState.Ready -> Navigation()
                    }

                    if (secretState != SecretState.Loading) {
                        reportFullyDrawn()
                    }
                }
            }
        }
    }

    @Composable
    private fun WrapOnboarding() {
        val onboardingViewModel by viewModels<OnboardingViewModel>()

        if (!onboardingViewModel.isImporting.collectAsState().value) {
            Onboarding(
                onboardingState = onboardingViewModel.onboardingState,
                onImportWallet = { onboardingViewModel.isImporting.value = true },
                onCreateWallet = {
                    walletViewModel.persistNewWallet()
                }
            )
        } else {
            WrapRestore()
        }
    }

    @Composable
    private fun WrapBackup(persistableWallet: PersistableWallet) {
        val backupViewModel by viewModels<BackupViewModel>()

        BackupWallet(
            persistableWallet, backupViewModel.backupState, backupViewModel.testChoices,
            onCopyToClipboard = {
                val clipboardManager = getSystemService(ClipboardManager::class.java)
                val data = ClipData.newPlainText(
                    getString(R.string.new_wallet_clipboard_tag),
                    persistableWallet.seedPhrase.joinToString()
                )
                clipboardManager.setPrimaryClip(data)
            }, onComplete = {
            walletViewModel.persistBackupComplete()
        }
        )
    }

    @Composable
    private fun WrapRestore() {
        val onboardingViewModel by viewModels<OnboardingViewModel>()
        val restoreViewModel by viewModels<RestoreViewModel>()

        when (val completeWordList = restoreViewModel.completeWordList.collectAsState().value) {
            CompleteWordSetState.Loading -> {
                // Although it might perform IO, it should be relatively fast.
                // Consider whether to display indeterminate progress here.
                // Another option would be to go straight to the restore screen with autocomplete
                // disabled for a few milliseconds.  Users would probably never notice due to the
                // time it takes to re-orient on the new screen, unless users were doing this
                // on a daily basis and become very proficient at our UI.  The Therac-25 has
                // historical precedent on how that could cause problems.
            }
            is CompleteWordSetState.Loaded -> {
                RestoreWallet(
                    completeWordList.list,
                    restoreViewModel.userWordList,
                    onBack = { onboardingViewModel.isImporting.value = false },
                    paste = {
                        val clipboardManager = getSystemService(ClipboardManager::class.java)
                        return@RestoreWallet clipboardManager?.primaryClip?.toString()
                    },
                    onFinished = {
                        // Write the backup complete flag first, then the seed phrase.  That avoids the UI
                        // flickering to the backup screen.  Assume if a user is restoring from
                        // a backup, then the user has a valid backup.
                        walletViewModel.persistBackupComplete()

                        val network = ZcashNetwork.fromResources(application)
                        val restoredWallet = PersistableWallet(
                            network,
                            WalletBirthday(network.saplingActivationHeight),
                            SeedPhrase(restoreViewModel.userWordList.current.value)
                        )
                        walletViewModel.persistExistingWallet(restoredWallet)
                    }
                )
            }
        }
    }

    @Composable
    private fun Navigation() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "home") {
            composable("home") { WrapHome({}, {}, {}, {}) }
        }
    }

    @Composable
    private fun WrapHome(
        goScan: () -> Unit,
        goProfile: () -> Unit,
        goSend: () -> Unit,
        goRequest: () -> Unit
    ) {
        val walletSnapshot = walletViewModel.walletSnapshot.collectAsState().value
        if (null == walletSnapshot) {
            // Display loading indicator
        } else {
            Home(
                walletSnapshot,
                walletViewModel.transactionSnapshot.collectAsState().value,
                goScan = goScan,
                goRequest = goRequest,
                goSend = goSend,
                goProfile = goProfile
            )
        }
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
private suspend fun prefetchFontLegacy(context: Context, @FontRes fontRes: Int) =
    withContext(Dispatchers.IO) {
        ResourcesCompat.getFont(context, fontRes)
    }
