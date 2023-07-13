package co.electriccoin.zcash.ui.screen.shield

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.common.onLaunchUrl
import co.electriccoin.zcash.ui.screen.home.viewmodel.HomeViewModel
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.shield.model.ShieldUIState
import co.electriccoin.zcash.ui.screen.shield.model.ShieldUiDestination
import co.electriccoin.zcash.ui.screen.shield.model.ShieldingProcessState
import co.electriccoin.zcash.ui.screen.shield.view.AutoShieldingInfo
import co.electriccoin.zcash.ui.screen.shield.view.ShieldFunds
import co.electriccoin.zcash.ui.screen.shield.viewmodel.ShieldViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
internal fun MainActivity.AndroidShield(onBack: () -> Unit) {
    WrapShield(activity = this, onBack = onBack)
}

@Composable
internal fun WrapShield(activity: ComponentActivity, onBack: () -> Unit) {
    val shieldViewModel = viewModel<ShieldViewModel>()
    val homeViewModel by activity.viewModels<HomeViewModel>()
    val walletViewModel by activity.viewModels<WalletViewModel>()
    val scope = rememberCoroutineScope()

    BackHandler(enabled = shieldViewModel.getCurrentDestination() == ShieldUiDestination.ShieldFunds) {
        // Prevent back button click
    }

    DisposableEffect(key1 = Unit) {
        shieldViewModel.checkAutoShieldUiState()
        homeViewModel.onBottomNavBarVisibilityChanged(show = false)
        onDispose {
            shieldViewModel.clearData()
            homeViewModel.onBottomNavBarVisibilityChanged(show = true)
        }
    }

    when (val shieldUIState = shieldViewModel.shieldUIState.collectAsStateWithLifecycle().value) {
        ShieldUIState.Loading -> {
            // We can add loading state
        }

        is ShieldUIState.OnResult -> {
            when (val destination = shieldUIState.destination) {
                is ShieldUiDestination.AutoShieldError -> {
                    // May be log or may be show error and navigate back
                    Twig.info { "Error is ${destination.message}" }
                    Toast.makeText(activity, destination.message ?: "Error in autoShielding", Toast.LENGTH_SHORT).show()
                    onBack()
                }

                ShieldUiDestination.AutoShieldingInfo -> {
                    shieldViewModel.acknowledgeShieldingInfo()
                    AutoShieldingInfo(
                        onNext = { shieldViewModel.updateShieldUiState(ShieldUIState.OnResult(ShieldUiDestination.ShieldFunds)) },
                        onLaunchUrl = { activity.onLaunchUrl(it) }
                    )
                }

                ShieldUiDestination.ShieldFunds -> {
                    val shieldingProcessState = shieldViewModel.shieldingProcessState.collectAsStateWithLifecycle().value
                    val spendingKey = walletViewModel.spendingKey.collectAsStateWithLifecycle().value
                    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value
                    LaunchedEffect(key1 = synchronizer, key2 = spendingKey) {
                        if (synchronizer != null && spendingKey != null) {
                            scope.launch(Dispatchers.IO) {
                                runCatching {
                                    Twig.info { "AutoShield onStarted" }
                                    synchronizer.shieldFunds(spendingKey)
                                }
                                    .onSuccess {
                                        Twig.info { "AutoShield onSuccess $it" }
                                        shieldViewModel.updateShieldingProcessState(ShieldingProcessState.SUCCESS)
                                    }
                                    .onFailure {
                                        Twig.info { "AutoShield onFailure $it" }
                                        shieldViewModel.updateShieldingProcessState(ShieldingProcessState.FAILURE)
                                    }
                            }
                        }
                    }
                    shieldViewModel.updateLastAutoShieldTime()
                    ShieldFunds(
                        onBack = onBack,
                        shieldingProcessState = shieldingProcessState
                    )
                }
            }
        }
    }
}
