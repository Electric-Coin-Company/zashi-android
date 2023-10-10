package co.electriccoin.zcash.ui

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import cash.z.ecc.android.sdk.fixture.WalletFixture
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.zcash.spackle.FirebaseTestLabUtil
import co.electriccoin.zcash.ui.common.BindCompLocalProvider
import co.electriccoin.zcash.ui.configuration.RemoteConfig
import co.electriccoin.zcash.ui.design.component.ConfigurationOverride
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.Override
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.backup.WrapNewWallet
import co.electriccoin.zcash.ui.screen.home.model.OnboardingState
import co.electriccoin.zcash.ui.screen.home.viewmodel.HomeViewModel
import co.electriccoin.zcash.ui.screen.home.viewmodel.SecretState
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.onboarding.WrapOnboarding
import co.electriccoin.zcash.ui.screen.onboarding.persistExistingWalletWithSeedPhrase
import co.electriccoin.zcash.ui.screen.securitywarning.WrapSecurityWarning
import co.electriccoin.zcash.ui.screen.warning.WrapNotEnoughSpace
import co.electriccoin.zcash.ui.screen.warning.viewmodel.StorageCheckViewModel
import co.electriccoin.zcash.work.WorkIds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {

    val homeViewModel by viewModels<HomeViewModel>()

    val walletViewModel by viewModels<WalletViewModel>()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val storageCheckViewModel by viewModels<StorageCheckViewModel>()

    lateinit var navControllerForTesting: NavHostController

    val configurationOverrideFlow = MutableStateFlow<ConfigurationOverride?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupSplashScreen()

        setupUiContent()

        monitorForBackgroundSync()
    }

    private fun setupSplashScreen() {
        val splashScreen = installSplashScreen()
        val start = SystemClock.elapsedRealtime().milliseconds

        splashScreen.setKeepOnScreenCondition {
            if (SPLASH_SCREEN_DELAY > Duration.ZERO) {
                val now = SystemClock.elapsedRealtime().milliseconds

                // This delay is for debug purposes only; do not enable for production usage.
                if (now - start < SPLASH_SCREEN_DELAY) {
                    return@setKeepOnScreenCondition true
                }
            }

            // Note this condition needs to be kept in sync with the condition in MainContent()
            homeViewModel.configurationFlow.value == null || SecretState.Loading == walletViewModel.secretState.value
        }
    }

    private fun setupUiContent() {
        setContent {
            Override(configurationOverrideFlow) {
                ZcashTheme {
                    GradientSurface(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        BindCompLocalProvider {
                            val isEnoughSpace by storageCheckViewModel.isEnoughSpace.collectAsStateWithLifecycle()
                            if (isEnoughSpace == false) {
                                WrapNotEnoughSpace()
                            } else {
                                MainContent()
                            }
                        }
                    }
                }
            }

            // Force collection to improve performance; sync can start happening while
            // the user is going through the backup flow.
            walletViewModel.synchronizer.collectAsStateWithLifecycle()
        }
    }

    @Composable
    private fun MainContent() {
        val configuration = homeViewModel.configurationFlow.collectAsStateWithLifecycle().value
        val secretState = walletViewModel.secretState.collectAsStateWithLifecycle().value

        // Note this condition needs to be kept in sync with the condition in setupSplashScreen()
        if (null == configuration || secretState == SecretState.Loading) {
            // For now, keep displaying splash screen using condition above.
            // In the future, we might consider displaying something different here.
        } else {
            // Note that the deeply nested child views will probably receive arguments derived from
            // the configuration.  The CompositionLocalProvider is helpful for passing the configuration
            // to the "platform" layer, which is where the arguments will be derived from.
            CompositionLocalProvider(RemoteConfig provides configuration) {
                when (secretState) {
                    SecretState.None -> {
                        WrapOnboarding()
                    }
                    is SecretState.NeedsWarning -> {
                        WrapSecurityWarning(
                            onBack = { walletViewModel.persistOnboardingState(OnboardingState.NONE) },
                            onConfirm = {
                                walletViewModel.persistOnboardingState(OnboardingState.NEEDS_BACKUP)

                                if (FirebaseTestLabUtil.isFirebaseTestLab(applicationContext)) {
                                    persistExistingWalletWithSeedPhrase(
                                        applicationContext,
                                        walletViewModel,
                                        SeedPhrase.new(WalletFixture.Alice.seedPhrase),
                                        WalletFixture.Alice.getBirthday(ZcashNetwork.fromResources(applicationContext))
                                    )
                                } else {
                                    walletViewModel.persistNewWallet()
                                }
                            }
                        )
                    }
                    is SecretState.NeedsBackup -> {
                        WrapNewWallet(
                            secretState.persistableWallet,
                            onBackupComplete = { walletViewModel.persistOnboardingState(OnboardingState.READY) }
                        )
                    }
                    is SecretState.Ready -> {
                        Navigation()
                    }
                    else -> {
                        error("Unhandled secret state: $secretState")
                    }
                }
            }
        }
    }

    private fun monitorForBackgroundSync() {
        val isEnableBackgroundSyncFlow = run {
            val homeViewModel by viewModels<HomeViewModel>()
            val isSecretReadyFlow = walletViewModel.secretState.map { it is SecretState.Ready }
            val isBackgroundSyncEnabledFlow = homeViewModel.isBackgroundSyncEnabled.filterNotNull()

            isSecretReadyFlow.combine(isBackgroundSyncEnabledFlow) { isSecretReady, isBackgroundSyncEnabled ->
                isSecretReady && isBackgroundSyncEnabled
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                isEnableBackgroundSyncFlow.collect { isEnableBackgroundSync ->
                    if (isEnableBackgroundSync) {
                        WorkIds.enableBackgroundSynchronization(application)
                    } else {
                        WorkIds.disableBackgroundSynchronization(application)
                    }
                }
            }
        }
    }

    companion object {
        @VisibleForTesting
        internal val SPLASH_SCREEN_DELAY = 0.seconds
    }
}
