@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.onboarding

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.fixture.WalletFixture
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.spackle.FirebaseTestLabUtil
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.model.OnboardingState
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.onboarding.view.Onboarding
import co.electriccoin.zcash.ui.screen.onboarding.viewmodel.OnboardingViewModel
import co.electriccoin.zcash.ui.screen.restore.WrapRestore

@Suppress("LongMethod")
@Composable
internal fun WrapOnboarding() {
    val activity = LocalActivity.current
    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val onboardingViewModel = koinActivityViewModel<OnboardingViewModel>()

    val versionInfo = VersionInfo.new(activity.applicationContext)

    // TODO [#383]: https://github.com/Electric-Coin-Company/zashi-android/issues/383
    // TODO [#383]: Refactoring of UI state retention into rememberSaveable fields

    if (!onboardingViewModel.isImporting.collectAsStateWithLifecycle().value) {
        val onCreateWallet = {
            walletViewModel.persistOnboardingState(
                if (onboardingViewModel.isSecurityScreenAllowed) {
                    OnboardingState.NEEDS_WARN
                } else {
                    walletViewModel.persistNewWalletAndRestoringState(WalletRestoringState.INITIATING)
                    OnboardingState.NEEDS_BACKUP
                }
            )
        }
        val onImportWallet = {
            // In the case of the app currently being messed with by the robo test runner on
            // Firebase Test Lab or Google Play pre-launch report, we want to skip creating
            // a new or restoring an existing wallet screens by persisting an existing wallet
            // with a mock seed.
            if (FirebaseTestLabUtil.isFirebaseTestLab(activity.applicationContext)) {
                persistExistingWalletWithSeedPhrase(
                    activity.applicationContext,
                    walletViewModel,
                    SeedPhrase.new(WalletFixture.Alice.seedPhrase),
                    birthday = WalletFixture.Alice.getBirthday(ZcashNetwork.fromResources(activity.applicationContext))
                )
            } else {
                onboardingViewModel.setIsImporting(true)
            }
        }

        val onFixtureWallet: (String) -> Unit = { seed ->
            persistExistingWalletWithSeedPhrase(
                activity.applicationContext,
                walletViewModel,
                SeedPhrase.new(seed),
                birthday = WalletFixture.Alice.getBirthday(ZcashNetwork.fromResources(activity.applicationContext))
            )
        }

        Onboarding(
            isDebugMenuEnabled = versionInfo.isDebuggable && !versionInfo.isRunningUnderTestService,
            onImportWallet = onImportWallet,
            onCreateWallet = onCreateWallet,
            onFixtureWallet = onFixtureWallet
        )

        activity.reportFullyDrawn()
    } else {
        WrapRestore()
    }
}

/**
 * Persists existing wallet together with the backup complete flag to disk. Be aware of that, it
 * triggers navigation changes, as we observe the WalletViewModel.secretState.
 *
 * Write the backup complete flag first, then the seed phrase. That avoids the UI flickering to
 * the backup screen. Assume if a user is restoring from a backup, then the user has a valid backup.
 *
 * @param seedPhrase to be persisted as part of the wallet.
 * @param birthday optional user provided birthday to be persisted as part of the wallet.
 */
internal fun persistExistingWalletWithSeedPhrase(
    context: Context,
    walletViewModel: WalletViewModel,
    seedPhrase: SeedPhrase,
    birthday: BlockHeight?
) {
    walletViewModel.persistExistingWalletWithSeedPhrase(
        network = ZcashNetwork.fromResources(context),
        seedPhrase = seedPhrase,
        birthday = birthday
    )
}
