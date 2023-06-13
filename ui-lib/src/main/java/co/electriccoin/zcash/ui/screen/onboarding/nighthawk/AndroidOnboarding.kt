package co.electriccoin.zcash.ui.screen.onboarding.nighthawk

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.onboarding.nighthawk.view.GetStarted
import co.electriccoin.zcash.ui.screen.onboarding.nighthawk.view.RestoreWallet
import co.electriccoin.zcash.ui.screen.onboarding.viewmodel.OnboardingViewModel

@Composable
internal fun MainActivity.WrapOnBoarding() {
    WrapOnBoarding(this)
}

@Composable
internal fun WrapOnBoarding(activity: ComponentActivity) {
    val walletViewModel by activity.viewModels<WalletViewModel>()
    val onBoardingViewModel by activity.viewModels<OnboardingViewModel>()

    if (!onBoardingViewModel.isImporting.collectAsStateWithLifecycle().value) {
        val onCreateWallet = {
            walletViewModel.persistNewWallet()
        }
        val onRestore = {
            onBoardingViewModel.setIsImporting(true)
        }

        GetStarted(
            onCreateWallet = onCreateWallet,
            onRestore = onRestore,
            onReference = {
                activity.onLaunchUrl(url = activity.getString(R.string.ns_privacy_policy_link))
            }
        )
    } else {
        RestoreWallet(activity)
    }
}

internal fun ComponentActivity.onLaunchUrl(url: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (t: Throwable) {
        print("Warning: failed to open browser due to $t")
    }
}