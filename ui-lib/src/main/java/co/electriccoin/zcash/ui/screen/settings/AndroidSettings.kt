@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.screen.settings

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.onboarding.viewmodel.OnboardingViewModel
import co.electriccoin.zcash.ui.screen.settings.view.Settings

@Composable
internal fun MainActivity.WrapSettings(
    goBack: () -> Unit,
    goWalletBackup: () -> Unit
) {
    WrapSettings(
        activity = this,
        goBack = goBack,
        goWalletBackup = goWalletBackup
    )
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
private fun WrapSettings(
    activity: ComponentActivity,
    goBack: () -> Unit,
    goWalletBackup: () -> Unit
) {
    val walletViewModel by activity.viewModels<WalletViewModel>()

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    if (null == synchronizer) {
        // Display loading indicator
    } else {
        Settings(
            onBack = goBack,
            onBackupWallet = goWalletBackup,
            onRescanWallet = {
                walletViewModel.rescanBlockchain()
            },
            onWipeWallet = {
                walletViewModel.wipeEntireWallet()

                val onboardingViewModel by activity.viewModels<OnboardingViewModel>()
                onboardingViewModel.onboardingState.goToBeginning()
                onboardingViewModel.isImporting.value = false
            }
        )
    }
}
