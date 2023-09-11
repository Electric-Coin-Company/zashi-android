@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.screen.onboarding

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.WalletInitMode
import cash.z.ecc.android.sdk.fixture.WalletFixture
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.zcash.spackle.EmulatorWtfUtil
import co.electriccoin.zcash.spackle.FirebaseTestLabUtil
import co.electriccoin.zcash.ui.BuildConfig
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.configuration.ConfigurationEntries
import co.electriccoin.zcash.ui.configuration.RemoteConfig
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.onboarding.view.LongOnboarding
import co.electriccoin.zcash.ui.screen.onboarding.view.ShortOnboarding
import co.electriccoin.zcash.ui.screen.onboarding.viewmodel.OnboardingViewModel
import co.electriccoin.zcash.ui.screen.restore.WrapRestore

@Composable
internal fun MainActivity.WrapOnboarding() {
    WrapOnboarding(this)
}

@Suppress("LongMethod")
@Composable
internal fun WrapOnboarding(
    activity: ComponentActivity
) {
    val walletViewModel by activity.viewModels<WalletViewModel>()
    val onboardingViewModel by activity.viewModels<OnboardingViewModel>()

    val applicationContext = LocalContext.current.applicationContext

    // We might eventually want to check the debuggable property of the manifest instead
    // of relying on BuildConfig.
    val isDebugMenuEnabled = BuildConfig.DEBUG &&
        !FirebaseTestLabUtil.isFirebaseTestLab(applicationContext) &&
        !EmulatorWtfUtil.isEmulatorWtf(applicationContext)

    // TODO [#383]: https://github.com/zcash/secant-android-wallet/issues/383
    if (!onboardingViewModel.isImporting.collectAsStateWithLifecycle().value) {
        val onCreateWallet = {
            if (FirebaseTestLabUtil.isFirebaseTestLab(applicationContext)) {
                persistExistingWalletWithSeedPhrase(
                    applicationContext,
                    walletViewModel,
                    SeedPhrase.new(WalletFixture.Alice.seedPhrase),
                    birthday = WalletFixture.Alice.getBirthday(ZcashNetwork.fromResources(applicationContext))
                )
            } else {
                walletViewModel.persistNewWallet()
            }
        }

        val onImportWallet = {
            // In the case of the app currently being messed with by the robo test runner on
            // Firebase Test Lab or Google Play pre-launch report, we want to skip creating
            // a new or restoring an existing wallet screens by persisting an existing wallet
            // with a mock seed.
            if (FirebaseTestLabUtil.isFirebaseTestLab(applicationContext)) {
                persistExistingWalletWithSeedPhrase(
                    applicationContext,
                    walletViewModel,
                    SeedPhrase.new(WalletFixture.Alice.seedPhrase),
                    birthday = WalletFixture.Alice.getBirthday(ZcashNetwork.fromResources(applicationContext))
                )
            } else {
                onboardingViewModel.setIsImporting(true)
            }
        }

        val onFixtureWallet = {
            persistExistingWalletWithSeedPhrase(
                applicationContext,
                walletViewModel,
                SeedPhrase.new(WalletFixture.Alice.seedPhrase),
                birthday = WalletFixture.Alice.getBirthday(ZcashNetwork.fromResources(applicationContext))
            )
        }

        if (ConfigurationEntries.IS_SHORT_ONBOARDING_UX.getValue(RemoteConfig.current)) {
            ShortOnboarding(
                isDebugMenuEnabled = isDebugMenuEnabled,
                onImportWallet = onImportWallet,
                onCreateWallet = onCreateWallet,
                onFixtureWallet = onFixtureWallet
            )
        } else {
            LongOnboarding(
                onboardingState = onboardingViewModel.onboardingState,
                isDebugMenuEnabled = isDebugMenuEnabled,
                onImportWallet = onImportWallet,
                onCreateWallet = onCreateWallet,
                onFixtureWallet = onFixtureWallet
            )
        }

        activity.reportFullyDrawn()
    } else {
        WrapRestore(activity)
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
    walletViewModel.persistBackupComplete()

    val network = ZcashNetwork.fromResources(context)
    val restoredWallet = PersistableWallet(
        network,
        birthday,
        seedPhrase,
        WalletInitMode.RestoreWallet
    )
    walletViewModel.persistExistingWallet(restoredWallet)
}
