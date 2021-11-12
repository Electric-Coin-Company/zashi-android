package cash.z.ecc.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import cash.z.ecc.ui.screen.home.view.Home
import cash.z.ecc.ui.screen.home.viewmodel.WalletViewModel
import cash.z.ecc.ui.screen.onboarding.view.Onboarding
import cash.z.ecc.ui.screen.onboarding.viewmodel.OnboardingViewModel
import cash.z.ecc.ui.theme.ZcashTheme

class MainActivity : ComponentActivity() {

    private val walletViewModel by viewModels<WalletViewModel>()

    private val onboardingViewModel by viewModels<OnboardingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZcashTheme {
                if (null == walletViewModel.persistableWallet.collectAsState(null).value) {
                    // Optimized path to get to onboarding as quickly as possible
                    Onboarding(
                        onboardingState = onboardingViewModel.onboardingState,
                        onImportWallet = { TODO("Implement wallet import") },
                        onCreateWallet = { TODO("Implement wallet create") }
                    )
                } else {
                    if (null == walletViewModel.synchronizer.collectAsState(null).value) {
                        // Continue displaying splash screen
                    } else {
                        Home()
                    }
                }
            }
        }
    }
}
