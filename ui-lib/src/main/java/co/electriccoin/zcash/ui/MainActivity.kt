package co.electriccoin.zcash.ui

import android.os.Bundle
import android.os.SystemClock
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import cash.z.ecc.android.sdk.ext.collectWith
import co.electriccoin.zcash.spackle.EmulatorWtfUtil
import co.electriccoin.zcash.spackle.FirebaseTestLabUtil
import co.electriccoin.zcash.ui.common.LocalScreenBrightness
import co.electriccoin.zcash.ui.common.LocalScreenSecurity
import co.electriccoin.zcash.ui.common.ScreenBrightness
import co.electriccoin.zcash.ui.common.ScreenSecurity
import co.electriccoin.zcash.ui.design.compat.FontCompat
import co.electriccoin.zcash.ui.design.component.ConfigurationOverride
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.Override
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.backup.WrapBackup
import co.electriccoin.zcash.ui.screen.home.viewmodel.SecretState
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.onboarding.WrapOnboarding
import co.electriccoin.zcash.ui.screen.warning.WrapNotEnoughSpace
import co.electriccoin.zcash.ui.screen.warning.viewmodel.StorageCheckViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val walletViewModel by viewModels<WalletViewModel>()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val storageCheckViewModel by viewModels<StorageCheckViewModel>()

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    lateinit var navControllerForTesting: NavHostController

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val configurationOverrideFlow = MutableStateFlow<ConfigurationOverride?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupSplashScreen()

        if (FontCompat.isFontPrefetchNeeded()) {
            lifecycleScope.launch {
                FontCompat.prefetchFontsLegacy(applicationContext)
                setupUiContent()
            }
        } else {
            setupUiContent()
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

            SecretState.Loading == walletViewModel.secretState.value
        }
    }

    private fun setupUiContent() {
        val screenSecurity = ScreenSecurity()
        val screenBrightness = ScreenBrightness()
        observeScreenSecurityFlag(screenSecurity)
        observeScreenBrightnessFlag(screenBrightness)

        setContent {
            Override(configurationOverrideFlow) {
                ZcashTheme {
                    GradientSurface(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        CompositionLocalProvider(
                            LocalScreenSecurity provides screenSecurity,
                            LocalScreenBrightness provides screenBrightness
                        ) {
                            val isEnoughSpace by storageCheckViewModel.isEnoughSpace.collectAsState()
                            if (isEnoughSpace == false) {
                                WrapNotEnoughSpace()
                            } else {
                                MainContent()
                            }
                        }
                    }
                }
            }
        }

        // Force collection to improve performance; sync can start happening while
        // the user is going through the backup flow. Don't use eager collection in the view model,
        // so that the collection is still tied to UI lifecycle.
        walletViewModel.synchronizer.collectWith(lifecycleScope) {
        }
    }

    @Composable
    private fun MainContent() {
        when (val secretState = walletViewModel.secretState.collectAsState().value) {
            SecretState.Loading -> {
                // For now, keep displaying splash screen using condition above.
                // In the future, we might consider displaying something different here.
            }
            SecretState.None -> {
                WrapOnboarding()
            }
            is SecretState.NeedsBackup -> {
                WrapBackup(
                    secretState.persistableWallet,
                    onBackupComplete = { walletViewModel.persistBackupComplete() }
                )
            }
            is SecretState.Ready -> {
                Navigation()
            }
        }
    }

    private fun observeScreenSecurityFlag(screenSecurity: ScreenSecurity) {
        screenSecurity.referenceCount.map { it > 0 }.collectWith(lifecycleScope) { isSecure ->
            val isTest = FirebaseTestLabUtil.isFirebaseTestLab(applicationContext) ||
                EmulatorWtfUtil.isEmulatorWtf(applicationContext)

            if (isSecure && !isTest) {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
                )
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }
        }
    }

    private fun observeScreenBrightnessFlag(screenBrightness: ScreenBrightness) {
        screenBrightness.referenceCount.map { it > 0 }.collectWith(lifecycleScope) { maxBrightness ->
            if (maxBrightness) {
                window.attributes = window.attributes.apply {
                    this.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
                }
            } else {
                window.attributes = window.attributes.apply {
                    this.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                }
            }
        }
    }

    companion object {
        @VisibleForTesting
        internal val SPLASH_SCREEN_DELAY = 0.seconds
    }
}
