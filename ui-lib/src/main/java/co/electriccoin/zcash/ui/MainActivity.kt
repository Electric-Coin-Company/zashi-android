@file:Suppress("DEPRECATION")

package co.electriccoin.zcash.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.compose.BindCompLocalProvider
import co.electriccoin.zcash.ui.common.extension.setContentCompat
import co.electriccoin.zcash.ui.common.model.OnboardingState
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.repository.AddressBookRepositoryImpl
import co.electriccoin.zcash.ui.common.viewmodel.AuthenticationUIState
import co.electriccoin.zcash.ui.common.viewmodel.AuthenticationViewModel
import co.electriccoin.zcash.ui.common.viewmodel.HomeViewModel
import co.electriccoin.zcash.ui.common.viewmodel.SecretState
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.configuration.RemoteConfig
import co.electriccoin.zcash.ui.design.component.AnimationConstants
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.ConfigurationOverride
import co.electriccoin.zcash.ui.design.component.Override
import co.electriccoin.zcash.ui.design.component.WelcomeAnimationAutostart
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.authentication.AuthenticationUseCase
import co.electriccoin.zcash.ui.screen.authentication.WrapAuthentication
import co.electriccoin.zcash.ui.screen.newwalletrecovery.WrapNewWalletRecovery
import co.electriccoin.zcash.ui.screen.onboarding.WrapOnboarding
import co.electriccoin.zcash.ui.screen.onboarding.persistExistingWalletWithSeedPhrase
import co.electriccoin.zcash.ui.screen.securitywarning.WrapSecurityWarning
import co.electriccoin.zcash.ui.screen.support.WrapSupport
import co.electriccoin.zcash.ui.screen.warning.viewmodel.StorageCheckViewModel
import co.electriccoin.zcash.work.WorkIds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {
    private val homeViewModel by viewModel<HomeViewModel>()

    val walletViewModel by viewModel<WalletViewModel>()

    val storageCheckViewModel by viewModel<StorageCheckViewModel>()

    internal val authenticationViewModel by viewModel<AuthenticationViewModel>()

    lateinit var navControllerForTesting: NavHostController

    val configurationOverrideFlow = MutableStateFlow<ConfigurationOverride?>(null)

    private val addressBookRepository by inject<AddressBookRepositoryImpl>()

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    try {
                        addressBookRepository.onGoogleSignInSuccess(
                            account = GoogleSignIn.getSignedInAccountFromIntent(result.data).result
                        )
                        Twig.info { "Google sign in success" }
                    } catch (e: ApiException) {
                        Twig.error(e) { "Google sign in failed" }
                        addressBookRepository.onGoogleSignInError()
                    }
                }
                RESULT_CANCELED -> {
                    Twig.info { "Google sign in cancelled" }
                    addressBookRepository.onGoogleSignInCancelled()
                }
                else -> {
                    Twig.error { "Google sign in failed" }
                    addressBookRepository.onGoogleSignInError()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAllowedScreenOrientation()

        setupSplashScreen()

        setupUiContent()

        monitorForBackgroundSync()

        lifecycleScope.launch {
            addressBookRepository.googleSignInRequest.collect { scope ->
                val googleSignInClient = GoogleSignIn.getClient(
                    this@MainActivity, GoogleSignInOptions
                        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(scope)
                        .build()
                )

                googleSignInLauncher.launch(googleSignInClient.signInIntent)
            }
        }
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

            // Note this condition needs to be kept in sync with the condition in MainContent()
            homeViewModel.configurationFlow.value == null || SecretState.Loading == walletViewModel.secretState.value
        }
    }

    private fun setupUiContent() {
        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations, and go edge-to-edge.
        // This also sets up the initial system bar style based on the platform theme
        enableEdgeToEdge()
        setContentCompat {
            Override(configurationOverrideFlow) {
                ZcashTheme {
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

        when (authState) {
            AuthenticationUIState.Initial -> {
                Twig.debug { "Authentication initial state" }
                // Wait for the state update
            }

            AuthenticationUIState.NotRequired -> {
                Twig.debug { "App access authentication NOT required - welcome animation only" }
                if (animateAppAccess) {
                    WelcomeAnimationAutostart(
                        delay = AnimationConstants.INITIAL_DELAY.milliseconds
                    )
                    // Wait until the welcome animation finishes then mark it was shown
                    LaunchedEffect(key1 = authenticationViewModel.showWelcomeAnimation) {
                        delay(AnimationConstants.together())
                        authenticationViewModel.setWelcomeAnimationDisplayed()
                    }
                }
            }

            AuthenticationUIState.Required -> {
                Twig.debug { "App access authentication required" }

                // Check and trigger app access authentication if required
                // Note that the Welcome animation is part of its logic
                WrapAuthentication(
                    goSupport = {
                        authenticationViewModel.appAccessAuthentication.value = AuthenticationUIState.SupportedRequired
                    },
                    onSuccess = {
                        lifecycleScope.launch {
                            // Wait until the welcome animation finishes, then mark it as presented to the user
                            delay((AnimationConstants.together()).milliseconds)
                            authenticationViewModel.appAccessAuthentication.value = AuthenticationUIState.Successful
                        }
                    },
                    onCancel = {
                        finish()
                    },
                    onFailed = {
                        // No subsequent action required. User is prompted with an explanation dialog.
                    },
                    useCase = AuthenticationUseCase.AppAccess
                )
            }

            AuthenticationUIState.SupportedRequired -> {
                Twig.debug { "Authentication support required" }
                WrapSupport(
                    goBack = { finish() }
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
                                    walletViewModel.persistWalletRestoringState(WalletRestoringState.INITIATING)
                                }
                            }
                        )
                    }

                    is SecretState.NeedsBackup -> {
                        WrapNewWalletRecovery(
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
        val isEnableBackgroundSyncFlow =
            run {
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
