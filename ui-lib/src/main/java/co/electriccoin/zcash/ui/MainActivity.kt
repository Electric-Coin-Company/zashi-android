@file:Suppress("DEPRECATION")

package co.electriccoin.zcash.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.enableEdgeToEdge
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.compose.BindCompLocalProvider
import co.electriccoin.zcash.ui.common.extension.setContentCompat
import co.electriccoin.zcash.ui.common.viewmodel.AuthenticationUIState
import co.electriccoin.zcash.ui.common.viewmodel.AuthenticationViewModel
import co.electriccoin.zcash.ui.common.viewmodel.OldHomeViewModel
import co.electriccoin.zcash.ui.common.viewmodel.SecretState
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.ConfigurationOverride
import co.electriccoin.zcash.ui.design.component.Override
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.authentication.AuthenticationUseCase
import co.electriccoin.zcash.ui.screen.authentication.RETRY_TRIGGER_DELAY
import co.electriccoin.zcash.ui.screen.authentication.WrapAuthentication
import co.electriccoin.zcash.ui.screen.authentication.view.AnimationConstants
import co.electriccoin.zcash.ui.screen.authentication.view.WelcomeAnimationAutostart
import co.electriccoin.zcash.ui.screen.onboarding.OnboardingNavigation
import co.electriccoin.zcash.ui.screen.warning.viewmodel.StorageCheckViewModel
import co.electriccoin.zcash.work.WorkIds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class MainActivity : FragmentActivity() {
    private val oldHomeViewModel by viewModel<OldHomeViewModel>()

    val walletViewModel by viewModel<WalletViewModel>()

    val storageCheckViewModel by viewModel<StorageCheckViewModel>()

    internal val authenticationViewModel by viewModel<AuthenticationViewModel>()

    lateinit var navControllerForTesting: NavHostController

    val configurationOverrideFlow = MutableStateFlow<ConfigurationOverride?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Twig.debug { "Activity state: Create" }

        setAllowedScreenOrientation()

        setupSplashScreen()

        setupUiContent()

        monitorForBackgroundSync()
    }

    override fun onStart() {
        Twig.debug { "Activity state: Start" }
        authenticationViewModel.runAuthenticationRequiredCheck()
        super.onStart()
    }

    override fun onStop() {
        Twig.debug { "Activity state: Stop" }
        authenticationViewModel.persistGoToBackgroundTime(System.currentTimeMillis())
        super.onStop()
    }

    /**
     * Sets whether the screen rotation is enabled or screen orientation is locked in the portrait mode.
     */
    @SuppressLint("SourceLockedOrientationActivity")
    private fun setAllowedScreenOrientation() {
        requestedOrientation =
            if (BuildConfig.IS_SCREEN_ROTATION_ENABLED) {
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
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

            SecretState.LOADING == walletViewModel.secretState.value
        }
    }

    private fun setupUiContent() {
        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations, and go edge-to-edge.
        // This also sets up the initial system bar style based on the platform theme
        enableEdgeToEdge()
        setContentCompat {
            Override(configurationOverrideFlow) {
                val isHideBalances by oldHomeViewModel.isHideBalances.collectAsStateWithLifecycle()
                ZcashTheme(
                    balancesAvailable = isHideBalances == false
                ) {
                    BlankSurface(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .imePadding()
                    ) {
                        BindCompLocalProvider {
                            MainContent()
                            AuthenticationForAppAccess()
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
    private fun AuthenticationForAppAccess() {
        val authState = authenticationViewModel.appAccessAuthenticationResultState.collectAsStateWithLifecycle().value
        val animateAppAccess = authenticationViewModel.showWelcomeAnimation.collectAsStateWithLifecycle().value
        val authFailed = authenticationViewModel.authFailed.collectAsStateWithLifecycle().value

        if (animateAppAccess) {
            WelcomeAnimationAutostart(
                delay = AnimationConstants.INITIAL_DELAY.milliseconds,
                showAuthLogo = authFailed,
                onRetry = {
                    authenticationViewModel.resetAuthenticationResult()
                    authenticationViewModel.authenticate(
                        activity = this,
                        initialAuthSystemWindowDelay = RETRY_TRIGGER_DELAY.milliseconds,
                        useCase = AuthenticationUseCase.AppAccess
                    )
                }
            )
        }

        when (authState) {
            AuthenticationUIState.Initial -> {
                Twig.debug { "Authentication initial state" }
                // Wait for the state update
            }

            AuthenticationUIState.NotRequired -> {
                Twig.debug { "App access authentication NOT required - welcome animation only" }
                // Wait until the welcome animation finishes then mark it was shown
                LaunchedEffect(key1 = authenticationViewModel.showWelcomeAnimation) {
                    delay(AnimationConstants.together())
                    authenticationViewModel.setWelcomeAnimationDisplayed()
                }
            }

            AuthenticationUIState.Required -> {
                Twig.debug { "App access authentication required" }

                // Check and trigger app access authentication if required
                // Note that the Welcome animation is part of its logic
                WrapAuthentication(
                    onSuccess = {
                        lifecycleScope.launch {
                            // Wait until the welcome animation finishes, then mark it as presented to the user
                            delay((AnimationConstants.durationOnly()).milliseconds)
                            authenticationViewModel.appAccessAuthentication.value = AuthenticationUIState.Successful
                        }
                    },
                    onCancel = {
                        authenticationViewModel.setAuthFailed()
                    },
                    onFail = {
                        authenticationViewModel.setAuthFailed()
                    },
                    useCase = AuthenticationUseCase.AppAccess
                )
            }

            AuthenticationUIState.Successful -> {
                Twig.debug { "Authentication successful - entering the app" }
                // No action is needed - the main app content is laid out now
            }
        }
    }

    @Composable
    private fun MainContent() {
        val secretState by walletViewModel.secretState.collectAsStateWithLifecycle()

        when (secretState) {
            SecretState.NONE -> {
                OnboardingNavigation()
            }

            SecretState.READY -> {
                Navigation()
            }

            SecretState.LOADING -> {
                // For now, keep displaying splash screen using condition above.
                // In the future, we might consider displaying something different here.
            }
        }
    }

    private fun monitorForBackgroundSync() {
        val isEnableBackgroundSyncFlow =
            run {
                val isSecretReadyFlow = walletViewModel.secretState.map { it == SecretState.READY }
                val isBackgroundSyncEnabledFlow = oldHomeViewModel.isBackgroundSyncEnabled.filterNotNull()

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
