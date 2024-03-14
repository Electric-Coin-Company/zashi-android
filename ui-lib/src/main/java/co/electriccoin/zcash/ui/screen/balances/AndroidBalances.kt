@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.balances

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.viewmodel.CheckUpdateViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.configuration.ConfigurationEntries
import co.electriccoin.zcash.ui.configuration.RemoteConfig
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.screen.balances.model.ShieldState
import co.electriccoin.zcash.ui.screen.balances.view.Balances
import co.electriccoin.zcash.ui.screen.settings.viewmodel.SettingsViewModel
import co.electriccoin.zcash.ui.screen.update.AppUpdateCheckerImp
import co.electriccoin.zcash.ui.screen.update.model.UpdateState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting

@Composable
internal fun WrapBalances(
    activity: ComponentActivity,
    goSettings: () -> Unit,
) {
    val walletViewModel by activity.viewModels<WalletViewModel>()

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    val walletSnapshot = walletViewModel.walletSnapshot.collectAsStateWithLifecycle().value

    val spendingKey = walletViewModel.spendingKey.collectAsStateWithLifecycle().value

    val checkUpdateViewModel by activity.viewModels<CheckUpdateViewModel> {
        CheckUpdateViewModel.CheckUpdateViewModelFactory(
            activity.application,
            AppUpdateCheckerImp.new()
        )
    }

    val settingsViewModel by activity.viewModels<SettingsViewModel>()

    WrapBalances(
        goSettings = goSettings,
        checkUpdateViewModel = checkUpdateViewModel,
        spendingKey = spendingKey,
        settingsViewModel = settingsViewModel,
        synchronizer = synchronizer,
        walletSnapshot = walletSnapshot
    )
}

const val DEFAULT_SHIELDING_THRESHOLD = 100000L

@Composable
@VisibleForTesting
@Suppress("LongParameterList", "LongMethod")
internal fun WrapBalances(
    goSettings: () -> Unit,
    checkUpdateViewModel: CheckUpdateViewModel,
    settingsViewModel: SettingsViewModel,
    spendingKey: UnifiedSpendingKey?,
    synchronizer: Synchronizer?,
    walletSnapshot: WalletSnapshot?,
) {
    val scope = rememberCoroutineScope()

    // To show information about the app update, if available
    val isUpdateAvailable =
        checkUpdateViewModel.updateInfo.collectAsStateWithLifecycle().value.let {
            it?.appUpdateInfo != null && it.state == UpdateState.Prepared
        }

    val isKeepScreenOnWhileSyncing = settingsViewModel.isKeepScreenOnWhileSyncing.collectAsStateWithLifecycle().value

    val isFiatConversionEnabled = ConfigurationEntries.IS_FIAT_CONVERSION_ENABLED.getValue(RemoteConfig.current)

    val (shieldState, setShieldState) =
        rememberSaveable(stateSaver = ShieldState.Saver) {
            mutableStateOf(
                if (walletSnapshot?.hasTransparentFunds == true) {
                    ShieldState.Available
                } else {
                    ShieldState.None
                }
            )
        }

    val (isShowingErrorDialog, setShowErrorDialog) = rememberSaveable { mutableStateOf(false) }

    suspend fun showShieldingError(error: Throwable?) {
        Twig.error { "Shielding proposal failed with: $error" }

        // Adding the extra delay before notifying UI for a better UX
        @Suppress("MagicNumber")
        delay(1500)

        setShieldState(ShieldState.Failed(error?.message ?: ""))
        setShowErrorDialog(true)
    }

    if (null == synchronizer || null == walletSnapshot || null == spendingKey) {
        // TODO [#1146]: Consider moving CircularScreenProgressIndicator from Android layer to View layer
        // TODO [#1146]: Improve this by allowing screen composition and updating it after the data is available
        // TODO [#1146]: https://github.com/Electric-Coin-Company/zashi-android/issues/1146
        CircularScreenProgressIndicator()
    } else {
        Balances(
            onSettings = goSettings,
            isFiatConversionEnabled = isFiatConversionEnabled,
            isKeepScreenOnWhileSyncing = isKeepScreenOnWhileSyncing,
            isUpdateAvailable = isUpdateAvailable,
            isShowingErrorDialog = isShowingErrorDialog,
            setShowErrorDialog = setShowErrorDialog,
            onShielding = {
                scope.launch {
                    setShieldState(ShieldState.Running)

                    Twig.debug { "Shielding transparent funds" }

                    runCatching {
                        synchronizer.proposeShielding(
                            account = spendingKey.account,
                            shieldingThreshold = Zatoshi(DEFAULT_SHIELDING_THRESHOLD),
                            // Using empty string for memo to clear the default memo prefix value defined in the SDK
                            memo = "",
                            // Using null will select whichever of the account's trans. receivers has funds to shield
                            transparentReceiver = null
                        )
                    }.onSuccess { newProposal ->
                        Twig.debug { "Shielding proposal result: ${newProposal?.toPrettyString()}" }

                        if (newProposal == null) {
                            showShieldingError(null)
                        } else {
                            // TODO [#1294]: Add Send.Multiple-Trx-Failed screen
                            // TODO [#1294]: Note that the following processing is not entirely correct and will be
                            //  reworked
                            // TODO [#1294]: https://github.com/Electric-Coin-Company/zashi-android/issues/1294
                            runCatching {
                                synchronizer.createProposedTransactions(
                                    proposal = newProposal,
                                    usk = spendingKey
                                )
                            }.onSuccess {
                                Twig.debug { "Shielding transaction event" }
                                setShieldState(ShieldState.None)
                            }.onFailure {
                                showShieldingError(null)
                            }
                        }
                    }.onFailure {
                        showShieldingError(it)
                    }
                }
            },
            shieldState = shieldState,
            walletSnapshot = walletSnapshot,
        )
    }
}
