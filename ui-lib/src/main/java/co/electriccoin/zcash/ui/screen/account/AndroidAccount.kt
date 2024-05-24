@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.account

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.internal.Twig
import co.electriccoin.zcash.spackle.ClipboardManagerUtil
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.BalanceState
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
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
    activity: ComponentActivity,
    goBalances: () -> Unit,
    goSettings: () -> Unit,
) {
    val walletViewModel by activity.viewModels<WalletViewModel>()

    val transactionHistoryViewModel by activity.viewModels<TransactionHistoryViewModel>()

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    val transactionsUiState = transactionHistoryViewModel.transactionUiState.collectAsStateWithLifecycle().value

    walletViewModel.transactionHistoryState.collectAsStateWithLifecycle().run {
        transactionHistoryViewModel.processTransactionState(value)
    }

    val walletRestoringState = walletViewModel.walletRestoringState.collectAsStateWithLifecycle().value

    val balanceState = walletViewModel.balanceState.collectAsStateWithLifecycle().value

    val walletSnapshot = walletViewModel.walletSnapshot.collectAsStateWithLifecycle().value

    WrapAccount(
        balanceState = balanceState,
        context = activity.applicationContext,
        goBalances = goBalances,
        goSettings = goSettings,
        synchronizer = synchronizer,
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
    context: Context,
    goBalances: () -> Unit,
    goSettings: () -> Unit,
    transactionsUiState: TransactionUiState,
    synchronizer: Synchronizer?,
    transactionHistoryViewModel: TransactionHistoryViewModel,
    walletRestoringState: WalletRestoringState,
    walletSnapshot: WalletSnapshot?
) {
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
            transactionsUiState = transactionsUiState,
            showStatusDialog = showStatusDialog.value,
            hideStatusDialog = { showStatusDialog.value = null },
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
