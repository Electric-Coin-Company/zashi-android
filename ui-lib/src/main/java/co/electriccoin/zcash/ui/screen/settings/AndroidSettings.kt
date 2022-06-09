package co.electriccoin.zcash.ui.screen.settings

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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

@Composable
private fun WrapSettings(
    activity: ComponentActivity,
    goBack: () -> Unit,
    goWalletBackup: () -> Unit
) {
    val walletViewModel by activity.viewModels<WalletViewModel>()

    val synchronizer = walletViewModel.synchronizer.collectAsState().value
    if (null == synchronizer) {
        // Display loading indicator
    } else {
        Settings(
            onBack = goBack,
            onBackupWallet = goWalletBackup,
            onRescanWallet = {
                walletViewModel.rescanBlockchain()
            }, onWipeWallet = {
                walletViewModel.wipeEntireWallet()

                val onboardingViewModel by activity.viewModels<OnboardingViewModel>()
                onboardingViewModel.onboardingState.goToBeginning()
                onboardingViewModel.isImporting.value = false
            }
        )
    }
}
