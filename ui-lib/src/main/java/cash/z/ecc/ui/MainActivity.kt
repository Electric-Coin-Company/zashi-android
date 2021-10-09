package cash.z.ecc.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import cash.z.ecc.ui.screen.onboarding.view.Onboarding
import cash.z.ecc.ui.screen.onboarding.viewmodel.OnboardingViewModel
import cash.z.ecc.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val onboardingViewModel by viewModels<OnboardingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Onboarding(
                    onboardingState = onboardingViewModel.onboardingState,
                    onImportWallet = { TODO("Implement wallet import") },
                    onCreateWallet = { TODO("Implement wallet create") }
                )
            }
        }
    }
}
