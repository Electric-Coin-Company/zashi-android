@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.screen.home

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import co.electriccoin.zcash.spackle.EmulatorWtfUtil
import co.electriccoin.zcash.spackle.FirebaseTestLabUtil
import co.electriccoin.zcash.ui.BuildConfig
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.backup.viewmodel.BackupViewModel
import co.electriccoin.zcash.ui.screen.home.view.Home
import co.electriccoin.zcash.ui.screen.home.viewmodel.CheckUpdateViewModel
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.onboarding.viewmodel.OnboardingViewModel
import co.electriccoin.zcash.ui.screen.update.AppUpdateCheckerImp
import co.electriccoin.zcash.ui.screen.update.model.UpdateState

@Composable
internal fun MainActivity.WrapHome(
    goScan: () -> Unit,
    goProfile: () -> Unit,
    goSend: () -> Unit,
    goRequest: () -> Unit
) {
    WrapHome(
        this,
        goScan = goScan,
        goProfile = goProfile,
        goSend = goSend,
        goRequest = goRequest
    )
}

@Composable
internal fun WrapHome(
    activity: ComponentActivity,
    goScan: () -> Unit,
    goProfile: () -> Unit,
    goSend: () -> Unit,
    goRequest: () -> Unit
) {
    // we want to show information about app update, if available
    val checkUpdateViewModel by activity.viewModels<CheckUpdateViewModel> {
        CheckUpdateViewModel.CheckUpdateViewModelFactory(
            activity.application,
            AppUpdateCheckerImp.new()
        )
    }
    val updateAvailable = checkUpdateViewModel.updateInfo.collectAsState().value.let {
        it?.appUpdateInfo != null && it.state == UpdateState.Prepared
    }

    val walletViewModel by activity.viewModels<WalletViewModel>()

    val walletSnapshot = walletViewModel.walletSnapshot.collectAsState().value
    if (null == walletSnapshot) {
        // Display loading indicator
    } else {
        val context = LocalContext.current

        // We might eventually want to check the debuggable property of the manifest instead
        // of relying on BuildConfig.
        val isDebugMenuEnabled = BuildConfig.DEBUG &&
            !FirebaseTestLabUtil.isFirebaseTestLab(context) &&
            !EmulatorWtfUtil.isEmulatorWtf(context)

        Home(
            walletSnapshot,
            walletViewModel.transactionSnapshot.collectAsState().value,
            goScan = goScan,
            goRequest = goRequest,
            goSend = goSend,
            goProfile = goProfile,
            isDebugMenuEnabled = isDebugMenuEnabled,
            resetSdk = {
                walletViewModel.resetSdk()
            },
            wipeEntireWallet = {
                // Although this is debug only, it still might be nice to show a warning dialog
                // before performing this action
                walletViewModel.wipeEntireWallet()

                val onboardingViewModel by activity.viewModels<OnboardingViewModel>()
                onboardingViewModel.onboardingState.goToBeginning()
                onboardingViewModel.isImporting.value = false

                val backupViewModel by activity.viewModels<BackupViewModel>()
                backupViewModel.backupState.goToBeginning()
            },
            updateAvailable = updateAvailable
        )

        activity.reportFullyDrawn()
    }
}
