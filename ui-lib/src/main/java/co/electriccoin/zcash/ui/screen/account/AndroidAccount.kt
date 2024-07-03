@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.account

import android.content.Context
import androidx.activity.viewModels
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.internal.Twig
import co.electriccoin.zcash.spackle.ClipboardManagerUtil
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.BalanceState
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.viewmodel.HomeViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.screen.account.model.TransactionUiState
import co.electriccoin.zcash.ui.screen.account.view.Account
import co.electriccoin.zcash.ui.screen.account.view.TrxItemAction
import co.electriccoin.zcash.ui.screen.account.viewmodel.TransactionHistoryViewModel
import co.electriccoin.zcash.ui.screen.balances.model.StatusAction
import co.electriccoin.zcash.ui.util.PlayStoreUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting

@Composable
internal fun WrapAccount(
    goBalances: () -> Unit,
    goSettings: () -> Unit,
) {
    val activity = LocalActivity.current

    val walletViewModel by activity.viewModels<WalletViewModel>()

    val transactionHistoryViewModel by activity.viewModels<TransactionHistoryViewModel>()

    val homeViewModel by activity.viewModels<HomeViewModel>()

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    val transactionsUiState = transactionHistoryViewModel.transactionUiState.collectAsStateWithLifecycle().value

    walletViewModel.transactionHistoryState.collectAsStateWithLifecycle().run {
        transactionHistoryViewModel.processTransactionState(value)
    }

    val walletRestoringState = walletViewModel.walletRestoringState.collectAsStateWithLifecycle().value

    val walletState = walletViewModel.walletStateInformation.collectAsStateWithLifecycle().value

    val balanceState = walletViewModel.balanceState.collectAsStateWithLifecycle().value

    val walletSnapshot = walletViewModel.walletSnapshot.collectAsStateWithLifecycle().value

    val isHideBalances = homeViewModel.isHideBalances.collectAsStateWithLifecycle().value ?: false

    WrapAccount(
        balanceState = balanceState,
        goBalances = goBalances,
        goSettings = goSettings,
        isHideBalances = isHideBalances,
        onHideBalances = { homeViewModel.showOrHideBalances() },
        synchronizer = synchronizer,
        topAppBarSubTitleState = walletState,
        transactionHistoryViewModel = transactionHistoryViewModel,
        transactionsUiState = transactionsUiState,
        walletRestoringState = walletRestoringState,
        walletSnapshot = walletSnapshot
    )

    // For benchmarking purposes
    activity.reportFullyDrawn()
}

@Composable
@VisibleForTesting
@Suppress("LongParameterList", "LongMethod")
internal fun WrapAccount(
    balanceState: BalanceState,
    goBalances: () -> Unit,
    goSettings: () -> Unit,
    isHideBalances: Boolean,
    synchronizer: Synchronizer?,
    onHideBalances: () -> Unit,
    topAppBarSubTitleState: TopAppBarSubTitleState,
    transactionsUiState: TransactionUiState,
    transactionHistoryViewModel: TransactionHistoryViewModel,
    walletRestoringState: WalletRestoringState,
    walletSnapshot: WalletSnapshot?
) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    // We could also improve this by `rememberSaveable` to preserve the dialog after a configuration change. But the
    // dialog dismissing in such cases is not critical, and it would require creating StatusAction custom Saver
    val showStatusDialog = remember { mutableStateOf<StatusAction.Detailed?>(null) }

    if (null == synchronizer || null == walletSnapshot) {
        // TODO [#1146]: Consider moving CircularScreenProgressIndicator from Android layer to View layer
        // TODO [#1146]: Improve this by allowing screen composition and updating it after the data is available
        // TODO [#1146]: https://github.com/Electric-Coin-Company/zashi-android/issues/1146
        CircularScreenProgressIndicator()
    } else {
        Account(
            balanceState = balanceState,
            isHideBalances = isHideBalances,
            transactionsUiState = transactionsUiState,
            showStatusDialog = showStatusDialog.value,
            hideStatusDialog = { showStatusDialog.value = null },
            onHideBalances = onHideBalances,
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
            onTransactionItemAction = { action ->
                when (action) {
                    is TrxItemAction.TransactionIdClick -> {
                        Twig.info { "Transaction ID clicked" }
                        ClipboardManagerUtil.copyToClipboard(
                            context,
                            context.getString(R.string.account_history_id_clipboard_tag),
                            action.id
                        )
                    }
                    is TrxItemAction.ExpandableStateChange -> {
                        Twig.info { "Transaction new state: ${action.newState.name}" }
                        scope.launch {
                            transactionHistoryViewModel.updateTransactionItemState(
                                synchronizer = synchronizer,
                                txId = action.txId,
                                newState = action.newState
                            )
                        }
                    }
                    is TrxItemAction.AddressClick -> {
                        Twig.info { "Transaction address clicked" }
                        ClipboardManagerUtil.copyToClipboard(
                            context.applicationContext,
                            context.getString(R.string.account_history_address_clipboard_tag),
                            action.address.addressValue
                        )
                    }
                    is TrxItemAction.MessageClick -> {
                        Twig.info { "Transaction message clicked" }
                        ClipboardManagerUtil.copyToClipboard(
                            context.applicationContext,
                            context.getString(R.string.account_history_memo_clipboard_tag),
                            action.memo
                        )
                    }
                }
            },
            goBalances = goBalances,
            goSettings = goSettings,
            snackbarHostState = snackbarHostState,
            topAppBarSubTitleState = topAppBarSubTitleState,
            walletRestoringState = walletRestoringState,
            walletSnapshot = walletSnapshot
        )
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
