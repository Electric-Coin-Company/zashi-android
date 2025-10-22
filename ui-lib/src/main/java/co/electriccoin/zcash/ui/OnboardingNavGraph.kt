package co.electriccoin.zcash.ui

import androidx.activity.ComponentActivity
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import cash.z.ecc.android.sdk.fixture.WalletFixture
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.zcash.spackle.FirebaseTestLabUtil
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.error.AndroidErrorBottomSheet
import co.electriccoin.zcash.ui.screen.error.AndroidErrorDialog
import co.electriccoin.zcash.ui.screen.error.ErrorBottomSheet
import co.electriccoin.zcash.ui.screen.error.ErrorDialog
import co.electriccoin.zcash.ui.screen.onboarding.Onboarding
import co.electriccoin.zcash.ui.screen.onboarding.persistExistingWalletWithSeedPhrase
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
import co.electriccoin.zcash.ui.screen.restore.tor.RestoreTorArgs
import co.electriccoin.zcash.ui.screen.restore.tor.RestoreTorScreen
import co.electriccoin.zcash.ui.screen.scan.thirdparty.AndroidThirdPartyScan
import co.electriccoin.zcash.ui.screen.scan.thirdparty.ThirdPartyScan

fun NavGraphBuilder.onboardingNavGraph(
    activity: ComponentActivity,
    navigationRouter: NavigationRouter,
    walletViewModel: WalletViewModel
) {
    navigation<OnboardingGraph>(
        startDestination = Onboarding,
    ) {
        composable<Onboarding> {
            Onboarding(
                onImportWallet = {
                    if (FirebaseTestLabUtil.isFirebaseTestLab(activity.applicationContext)) {
                        persistExistingWalletWithSeedPhrase(
                            activity.applicationContext,
                            walletViewModel,
                            SeedPhrase.Companion.new(WalletFixture.Alice.seedPhrase),
                            WalletFixture.Alice
                                .getBirthday(ZcashNetwork.Companion.fromResources(activity.applicationContext))
                        )
                    } else {
                        navigationRouter.forward(RestoreSeed)
                    }
                },
                onCreateWallet = {
                    if (FirebaseTestLabUtil.isFirebaseTestLab(activity.applicationContext)) {
                        persistExistingWalletWithSeedPhrase(
                            activity.applicationContext,
                            walletViewModel,
                            SeedPhrase.Companion.new(WalletFixture.Alice.seedPhrase),
                            WalletFixture.Alice.getBirthday(
                                ZcashNetwork.Companion.fromResources(
                                    activity.applicationContext
                                )
                            )
                        )
                    } else {
                        walletViewModel.createNewWallet()
                    }
                }
            )
        }
        composable<RestoreSeed> { AndroidRestoreSeed() }
        composable<RestoreBDHeight> { AndroidRestoreBDHeight(it.toRoute()) }
        composable<RestoreBDDate> { AndroidRestoreBDDate(it.toRoute()) }
        composable<RestoreBDEstimation> { AndroidRestoreBDEstimation(it.toRoute()) }
        dialogComposable<SeedInfo> { AndroidSeedInfo() }
        composable<ThirdPartyScan> { AndroidThirdPartyScan() }
        dialogComposable<ErrorDialog> { AndroidErrorDialog() }
        dialogComposable<ErrorBottomSheet> { AndroidErrorBottomSheet() }
        dialogComposable<RestoreTorArgs> { RestoreTorScreen(it.toRoute()) }
    }
}
