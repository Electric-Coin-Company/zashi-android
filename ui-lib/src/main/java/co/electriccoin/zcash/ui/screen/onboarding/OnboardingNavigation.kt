@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.onboarding

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import cash.z.ecc.android.sdk.fixture.WalletFixture
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.zcash.spackle.FirebaseTestLabUtil
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.Navigator
import co.electriccoin.zcash.ui.NavigatorImpl
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.datasource.MessageAvailabilityDataSource
import co.electriccoin.zcash.ui.common.model.OnboardingState
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.design.LocalKeyboardManager
import co.electriccoin.zcash.ui.design.LocalSheetStateManager
import co.electriccoin.zcash.ui.design.animation.ScreenAnimation.enterTransition
import co.electriccoin.zcash.ui.design.animation.ScreenAnimation.exitTransition
import co.electriccoin.zcash.ui.design.animation.ScreenAnimation.popEnterTransition
import co.electriccoin.zcash.ui.design.animation.ScreenAnimation.popExitTransition
import co.electriccoin.zcash.ui.screen.flexa.FlexaViewModel
import co.electriccoin.zcash.ui.screen.onboarding.view.Onboarding
import co.electriccoin.zcash.ui.screen.restore.date.AndroidRestoreBDDate
import co.electriccoin.zcash.ui.screen.restore.date.RestoreBDDate
import co.electriccoin.zcash.ui.screen.restore.estimation.AndroidRestoreBDEstimation
import co.electriccoin.zcash.ui.screen.restore.estimation.RestoreBDEstimation
import co.electriccoin.zcash.ui.screen.restore.height.AndroidRestoreBDHeight
import co.electriccoin.zcash.ui.screen.restore.height.RestoreBDHeight
import co.electriccoin.zcash.ui.screen.restore.info.AndroidSeedInfo
import co.electriccoin.zcash.ui.screen.restore.info.SeedInfo
import co.electriccoin.zcash.ui.screen.restore.seed.AndroidRestoreSeed
import co.electriccoin.zcash.ui.screen.restore.seed.RestoreSeed
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun MainActivity.OnboardingNavigation() {
    val activity = LocalActivity.current
    val navController = LocalNavController.current
    val keyboardManager = LocalKeyboardManager.current
    val sheetStateManager = LocalSheetStateManager.current
    val navigationRouter = koinInject<NavigationRouter>()
    val flexaViewModel = koinViewModel<FlexaViewModel>()
    val messageAvailabilityDataSource = koinInject<MessageAvailabilityDataSource>()

    val navigator: Navigator =
        remember(
            navController,
            flexaViewModel,
            keyboardManager,
            sheetStateManager,
            messageAvailabilityDataSource
        ) {
            NavigatorImpl(
                activity = this@OnboardingNavigation,
                navController = navController,
                flexaViewModel = flexaViewModel,
                keyboardManager = keyboardManager,
                sheetStateManager = sheetStateManager,
                messageAvailabilityDataSource = messageAvailabilityDataSource
            )
        }

    LaunchedEffect(Unit) {
        navigationRouter.observePipeline().collect {
            navigator.executeCommand(it)
        }
    }
    NavHost(
        navController = navController,
        startDestination = Onboarding,
        enterTransition = { enterTransition() },
        exitTransition = { exitTransition() },
        popEnterTransition = { popEnterTransition() },
        popExitTransition = { popExitTransition() }
    ) {
        composable<Onboarding> {
            Onboarding(
                onImportWallet = {
                    // In the case of the app currently being messed with by the robo test runner on
                    // Firebase Test Lab or Google Play pre-launch report, we want to skip creating
                    // a new or restoring an existing wallet screens by persisting an existing wallet
                    // with a mock seed.
                    if (FirebaseTestLabUtil.isFirebaseTestLab(activity.applicationContext)) {
                        persistExistingWalletWithSeedPhrase(
                            activity.applicationContext,
                            walletViewModel,
                            SeedPhrase.new(WalletFixture.Alice.seedPhrase),
                            WalletFixture.Alice.getBirthday(ZcashNetwork.fromResources(activity.applicationContext))
                        )
                    } else {
                        navigationRouter.forward(RestoreSeed)
                    }
                },
                onCreateWallet = {
                    if (FirebaseTestLabUtil.isFirebaseTestLab(applicationContext)) {
                        persistExistingWalletWithSeedPhrase(
                            applicationContext,
                            walletViewModel,
                            SeedPhrase.new(WalletFixture.Alice.seedPhrase),
                            WalletFixture.Alice.getBirthday(
                                ZcashNetwork.fromResources(
                                    applicationContext
                                )
                            )
                        )
                    } else {
                        walletViewModel.persistOnboardingState(OnboardingState.READY)
                        walletViewModel.persistNewWalletAndRestoringState(WalletRestoringState.INITIATING)
                    }
                }
            )
        }
        composable<RestoreSeed> {
            AndroidRestoreSeed()
        }
        composable<RestoreBDHeight> {
            AndroidRestoreBDHeight(it.toRoute())
        }
        composable<RestoreBDDate> {
            AndroidRestoreBDDate(it.toRoute())
        }
        composable<RestoreBDEstimation> {
            AndroidRestoreBDEstimation(it.toRoute())
        }
        dialog<SeedInfo>(
            dialogProperties =
                DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                )
        ) {
            AndroidSeedInfo()
        }
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
