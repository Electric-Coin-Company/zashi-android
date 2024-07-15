package co.electriccoin.zcash.ui.screen.restore

import android.content.ClipboardManager
import androidx.activity.ComponentActivity
import co.electriccoin.zcash.di.koinActivityViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.onboarding.persistExistingWalletWithSeedPhrase
import co.electriccoin.zcash.ui.screen.onboarding.viewmodel.OnboardingViewModel
import co.electriccoin.zcash.ui.screen.restore.view.RestoreWallet
import co.electriccoin.zcash.ui.screen.restore.viewmodel.CompleteWordSetState
import co.electriccoin.zcash.ui.screen.restore.viewmodel.RestoreViewModel

@Composable
fun WrapRestore() {
    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val onboardingViewModel = koinActivityViewModel<OnboardingViewModel>()
    val restoreViewModel = koinActivityViewModel<RestoreViewModel>()

    val applicationContext = LocalContext.current.applicationContext

    when (val completeWordList = restoreViewModel.completeWordList.collectAsStateWithLifecycle().value) {
        CompleteWordSetState.Loading -> {
            // Although it might perform IO, it should be relatively fast.
            // Consider whether to display indeterminate progress here.
            // Another option would be to go straight to the restore screen with autocomplete
            // disabled for a few milliseconds.  Users would probably never notice due to the
            // time it takes to re-orient on the new screen, unless users were doing this
            // on a daily basis and become very proficient at our UI.  The Therac-25 has
            // historical precedent on how that could cause problems.
        }
        is CompleteWordSetState.Loaded -> {
            RestoreWallet(
                ZcashNetwork.fromResources(applicationContext),
                restoreViewModel.restoreState,
                completeWordList.list,
                restoreViewModel.userWordList,
                restoreViewModel.userBirthdayHeight.collectAsStateWithLifecycle().value,
                setRestoreHeight = { restoreViewModel.userBirthdayHeight.value = it },
                onBack = { onboardingViewModel.setIsImporting(false) },
                paste = {
                    val clipboardManager = applicationContext.getSystemService(ClipboardManager::class.java)
                    return@RestoreWallet clipboardManager?.primaryClip?.toString()
                },
                onFinished = {
                    persistExistingWalletWithSeedPhrase(
                        applicationContext,
                        walletViewModel,
                        SeedPhrase(restoreViewModel.userWordList.current.value),
                        restoreViewModel.userBirthdayHeight.value
                    )
                }
            )
        }
    }
}
