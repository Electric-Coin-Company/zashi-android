@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.screen.onboarding

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.WalletInitMode
import cash.z.ecc.android.sdk.fixture.WalletFixture
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.android.sdk.model.defaultForNetwork
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.lightwallet.client.model.LightWalletEndpoint
import co.electriccoin.zcash.spackle.FirebaseTestLabUtil
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.configuration.ConfigurationEntries
import co.electriccoin.zcash.ui.configuration.RemoteConfig
import co.electriccoin.zcash.ui.screen.home.model.OnboardingState
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

    val versionInfo = VersionInfo.new(activity.applicationContext)

    // TODO [#383]: https://github.com/Electric-Coin-Company/zashi-android/issues/383
    // TODO [#383]: Refactoring of UI state retention into rememberSaveable fields
    if (!onboardingViewModel.isImporting.collectAsStateWithLifecycle().value) {
        val onCreateWallet = {
            walletViewModel.persistOnboardingState(OnboardingState.NEEDS_WARN)
            onboardingViewModel.setShowWelcomeAnimation(false)
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

            onboardingViewModel.setShowWelcomeAnimation(false)
        }

        val onFixtureWallet: (String) -> Unit = { seed ->
            persistExistingWalletWithSeedPhrase(
                activity.applicationContext,
                walletViewModel,
                SeedPhrase.new(seed),
                birthday = WalletFixture.Alice.getBirthday(ZcashNetwork.fromResources(activity.applicationContext))
            )
        }

        val showWelcomeAnimation = onboardingViewModel.showWelcomeAnimation.collectAsStateWithLifecycle().value

        // TODO [#1003]: Clear unused alternative Onboarding screens
        // TODO [#1003]: https://github.com/Electric-Coin-Company/zashi-android/issues/1003

        if (ConfigurationEntries.IS_SHORT_ONBOARDING_UX.getValue(RemoteConfig.current)) {
            ShortOnboarding(
                showWelcomeAnim = showWelcomeAnimation,
                isDebugMenuEnabled = versionInfo.isDebuggable,
                onImportWallet = onImportWallet,
                onCreateWallet = onCreateWallet,
                onFixtureWallet = onFixtureWallet
            )
        } else {
            LongOnboarding(
                onboardingState = onboardingViewModel.onboardingState,
                isDebugMenuEnabled = versionInfo.isDebuggable,
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
    walletViewModel.persistOnboardingState(OnboardingState.READY)

    val network = ZcashNetwork.fromResources(context)
    val restoredWallet = PersistableWallet(
        network = network,
        birthday = birthday,
        endpoint = LightWalletEndpoint.defaultForNetwork(network),
        seedPhrase = seedPhrase,
        walletInitMode = WalletInitMode.RestoreWallet
    )
    walletViewModel.persistExistingWallet(restoredWallet)
}
