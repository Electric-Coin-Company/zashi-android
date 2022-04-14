package co.electriccoin.zcash.ui.screen.onboarding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.onboarding.view.Onboarding
import co.electriccoin.zcash.ui.screen.onboarding.viewmodel.OnboardingViewModel

class TestOnboardingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUiContent()
    }

    private fun setupUiContent() {
        setContent {
            ZcashTheme {
                GradientSurface(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    WrapOnboarding()
                }
            }
        }
    }

    @Composable
    private fun WrapOnboarding() {
        val onboardingViewModel by viewModels<OnboardingViewModel>()

        // TODO [#383]: https://github.com/zcash/secant-android-wallet/issues/383
        if (!onboardingViewModel.isImporting.collectAsState().value) {
            Onboarding(
                onboardingState = onboardingViewModel.onboardingState,
                onImportWallet = { onboardingViewModel.isImporting.value = true },
                onCreateWallet = {}
            )

            reportFullyDrawn()
        }
    }
}
