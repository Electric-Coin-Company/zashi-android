@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.balances

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import cash.z.ecc.android.sdk.SdkSynchronizer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.BalanceState
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.usecase.GetZashiSpendingKeyUseCase
import co.electriccoin.zcash.ui.common.viewmodel.CheckUpdateViewModel
import co.electriccoin.zcash.ui.common.viewmodel.HomeViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.common.viewmodel.ZashiMainTopAppBarViewModel
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.ZashiMainTopAppBarState
import co.electriccoin.zcash.ui.screen.balances.model.ShieldState
import co.electriccoin.zcash.ui.screen.balances.model.StatusAction
import co.electriccoin.zcash.ui.screen.balances.view.Balances
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SubmitResult
import co.electriccoin.zcash.ui.screen.sendconfirmation.viewmodel.CreateTransactionsViewModel
import co.electriccoin.zcash.ui.screen.support.model.SupportInfo
import co.electriccoin.zcash.ui.screen.support.model.SupportInfoType
import co.electriccoin.zcash.ui.screen.support.viewmodel.SupportViewModel
import co.electriccoin.zcash.ui.screen.update.model.UpdateState
import co.electriccoin.zcash.ui.util.EmailUtil
import co.electriccoin.zcash.ui.util.PlayStoreUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import org.koin.compose.koinInject

@Composable
internal fun WrapBalances(goMultiTrxSubmissionFailure: () -> Unit) {
    val activity = LocalActivity.current

    val walletViewModel = koinActivityViewModel<WalletViewModel>()

    val createTransactionsViewModel = koinActivityViewModel<CreateTransactionsViewModel>()

    val homeViewModel = koinActivityViewModel<HomeViewModel>()

    val supportViewModel = koinActivityViewModel<SupportViewModel>()

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    val walletSnapshot = walletViewModel.currentWalletSnapshot.collectAsStateWithLifecycle().value

    val walletRestoringState = walletViewModel.walletRestoringState.collectAsStateWithLifecycle().value

    val isHideBalances = homeViewModel.isHideBalances.collectAsStateWithLifecycle().value ?: false

    val checkUpdateViewModel = koinActivityViewModel<CheckUpdateViewModel>()

    val balanceState = walletViewModel.balanceState.collectAsStateWithLifecycle().value

    val supportInfo = supportViewModel.supportInfo.collectAsStateWithLifecycle().value

    val zashiMainTopAppBarViewModel = koinActivityViewModel<ZashiMainTopAppBarViewModel>()

    val zashiMainTopAppBarState = zashiMainTopAppBarViewModel.state.collectAsStateWithLifecycle().value

    WrapBalances(
        balanceState = balanceState,
        createTransactionsViewModel = createTransactionsViewModel,
        checkUpdateViewModel = checkUpdateViewModel,
        goMultiTrxSubmissionFailure = goMultiTrxSubmissionFailure,
        isHideBalances = isHideBalances,
        lifecycleScope = activity.lifecycleScope,
        supportInfo = supportInfo,
        synchronizer = synchronizer,
        walletSnapshot = walletSnapshot,
        walletRestoringState = walletRestoringState,
        zashiMainTopAppBarState = zashiMainTopAppBarState
    )
}

const val DEFAULT_SHIELDING_THRESHOLD = 100000L

// This function should be refactored into smaller chunks
@Composable
@VisibleForTesting
@Suppress("LongParameterList", "LongMethod", "CyclomaticComplexMethod")
internal fun WrapBalances(
    balanceState: BalanceState,
    checkUpdateViewModel: CheckUpdateViewModel,
    createTransactionsViewModel: CreateTransactionsViewModel,
    goMultiTrxSubmissionFailure: () -> Unit,
    lifecycleScope: CoroutineScope,
    isHideBalances: Boolean,
    supportInfo: SupportInfo?,
    synchronizer: Synchronizer?,
    walletSnapshot: WalletSnapshot?,
    walletRestoringState: WalletRestoringState,
    zashiMainTopAppBarState: ZashiMainTopAppBarState
) {
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    // To show information about the app update, if available
    val isUpdateAvailable =
        checkUpdateViewModel.updateInfo.collectAsStateWithLifecycle().value.let {
            it?.appUpdateInfo != null && it.state == UpdateState.Prepared
        }

    val (shieldState, setShieldState) =
        rememberSaveable(stateSaver = ShieldState.Saver) { mutableStateOf(ShieldState.None) }

    // Keep the state always up-to-date with the latest transparent balance
    setShieldState(updateTransparentBalanceState(shieldState, walletSnapshot))

    val (isShowingErrorDialog, setShowErrorDialog) = rememberSaveable { mutableStateOf(false) }

    fun showShieldingSuccess() {
        setShieldState(ShieldState.Shielded)
        Toast.makeText(context, context.getString(R.string.balances_shielding_successful), Toast.LENGTH_LONG).show()
    }

    suspend fun showShieldingError(shieldingState: ShieldState) {
        Twig.error { "Shielding proposal failed with: $shieldingState" }

        // Adding the extra delay before notifying UI for a better UX
        @Suppress("MagicNumber")
        delay(1000)

        setShieldState(shieldingState)
        setShowErrorDialog(true)
    }

    // We could also improve this by `rememberSaveable` to preserve the dialog after a configuration change. But the
    // dialog dismissing in such cases is not critical, and it would require creating StatusAction custom Saver
    val showStatusDialog = remember { mutableStateOf<StatusAction.Detailed?>(null) }

    val getZashiSpendingKey = koinInject<GetZashiSpendingKeyUseCase>()

    if (null == synchronizer || null == walletSnapshot) {
        // TODO [#1146]: Consider moving CircularScreenProgressIndicator from Android layer to View layer
        // TODO [#1146]: Improve this by allowing screen composition and updating it after the data is available
        // TODO [#1146]: https://github.com/Electric-Coin-Company/zashi-android/issues/1146
        CircularScreenProgressIndicator()
    } else {
        Balances(
            balanceState = balanceState,
            isHideBalances = isHideBalances,
            isUpdateAvailable = isUpdateAvailable,
            isShowingErrorDialog = isShowingErrorDialog,
            setShowErrorDialog = setShowErrorDialog,
            showStatusDialog = showStatusDialog.value,
            hideStatusDialog = { showStatusDialog.value = null },
            snackbarHostState = snackbarHostState,
            onShielding = {
                lifecycleScope.launch {
                    val spendingKey = getZashiSpendingKey()
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
                        Twig.info { "Shielding proposal result: ${newProposal?.toPrettyString()}" }

                        if (newProposal == null) {
                            showShieldingError(
                                ShieldState.Failed(
                                    error =
                                        context.getString(
                                            R.string.balances_shielding_dialog_error_text_below_threshold
                                        ),
                                    stackTrace = ""
                                )
                            )
                        } else {
                            val result =
                                createTransactionsViewModel.runCreateTransactions(
                                    synchronizer = synchronizer,
                                    spendingKey = spendingKey,
                                    proposal = newProposal
                                )

                            // Triggering the transaction history and balances refresh to be notified immediately
                            // about the wallet's updated state
                            (synchronizer as SdkSynchronizer).run {
                                refreshTransactions()
                                refreshAllBalances()
                            }

                            when (result) {
                                SubmitResult.Success -> {
                                    Twig.info { "Shielding transaction done successfully" }
                                    showShieldingSuccess()
                                }
                                is SubmitResult.SimpleTrxFailure.SimpleTrxFailureGrpc -> {
                                    Twig.warn { "Shielding transaction failed" }
                                    showShieldingError(ShieldState.FailedGrpc)
                                }
                                is SubmitResult.SimpleTrxFailure -> {
                                    Twig.warn { "Shielding transaction failed" }
                                    showShieldingError(
                                        ShieldState.Failed(
                                            error = result.toErrorMessage(),
                                            stackTrace = result.toErrorStacktrace()
                                        )
                                    )
                                }
                                is SubmitResult.MultipleTrxFailure -> {
                                    Twig.warn { "Shielding failed with multi-transactions-submission-error handling" }
                                    goMultiTrxSubmissionFailure()
                                }
                            }
                        }
                    }.onFailure {
                        showShieldingError(
                            ShieldState.Failed(
                                error = it.message ?: "Unknown error",
                                stackTrace = it.stackTraceToString()
                            )
                        )
                    }
                }
            },
            onStatusClick = { status ->
                when (status) {
                    is StatusAction.Detailed -> showStatusDialog.value = status
                    StatusAction.AppUpdate -> {
                        openPlayStoreAppSite(
                            context = context,
                            snackbarHostState = snackbarHostState,
                            scope = scope
                        )
                    }
                    else -> {
                        // No action required
                    }
                }
            },
            onContactSupport = { error ->
                val fullMessage =
                    EmailUtil.formatMessage(
                        body = error,
                        supportInfo = supportInfo?.toSupportString(SupportInfoType.entries.toSet())
                    )
                val mailIntent =
                    EmailUtil.newMailActivityIntent(
                        context.getString(R.string.support_email_address),
                        context.getString(R.string.app_name),
                        fullMessage
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                runCatching {
                    context.startActivity(mailIntent)
                }.onFailure {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.unable_to_open_email)
                        )
                    }
                }
            },
            shieldState = shieldState,
            walletSnapshot = walletSnapshot,
            walletRestoringState = walletRestoringState,
            zashiMainTopAppBarState = zashiMainTopAppBarState
        )
    }
}

private fun updateTransparentBalanceState(
    currentShieldState: ShieldState,
    walletSnapshot: WalletSnapshot?
): ShieldState {
    return when {
        (walletSnapshot == null) -> currentShieldState
        (walletSnapshot.transparentBalance >= Zatoshi(DEFAULT_SHIELDING_THRESHOLD) && currentShieldState.isEnabled()) ->
            ShieldState.Available
        else -> currentShieldState
    }
}

private fun openPlayStoreAppSite(
    context: Context,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    val storeIntent = PlayStoreUtil.newActivityIntent(context)
    runCatching {
        context.startActivity(storeIntent)
    }.onFailure {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = context.getString(R.string.unable_to_open_play_store)
            )
        }
    }
}
