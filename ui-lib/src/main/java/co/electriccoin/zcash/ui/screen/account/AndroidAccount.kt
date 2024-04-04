@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.account

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.internal.Twig
import co.electriccoin.zcash.spackle.ClipboardManagerUtil
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.screen.account.model.TransactionUiState
import co.electriccoin.zcash.ui.screen.account.view.Account
import co.electriccoin.zcash.ui.screen.account.view.TrxItemAction
import co.electriccoin.zcash.ui.screen.account.viewmodel.TransactionHistoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting

@Composable
internal fun WrapAccount(
    activity: ComponentActivity,
    goBalances: () -> Unit,
    goSettings: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val walletViewModel by activity.viewModels<WalletViewModel>()

    val transactionHistoryViewModel by activity.viewModels<TransactionHistoryViewModel>()

    val walletSnapshot = walletViewModel.walletSnapshot.collectAsStateWithLifecycle().value

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    val transactionsUiState = transactionHistoryViewModel.transactionUiState.collectAsStateWithLifecycle().value

    walletViewModel.transactionHistoryState.collectAsStateWithLifecycle().run {
        transactionHistoryViewModel.processTransactionState(value)
    }

    val walletRestoringState = walletViewModel.walletRestoringState.collectAsStateWithLifecycle().value

    WrapAccount(
        context = activity.applicationContext,
        goBalances = goBalances,
        goSettings = goSettings,
        scope = scope,
        synchronizer = synchronizer,
        transactionHistoryViewModel = transactionHistoryViewModel,
        transactionsUiState = transactionsUiState,
        walletSnapshot = walletSnapshot,
        walletRestoringState = walletRestoringState
    )

    // For benchmarking purposes
    activity.reportFullyDrawn()
}

@Composable
@VisibleForTesting
@Suppress("LongParameterList")
internal fun WrapAccount(
    context: Context,
    scope: CoroutineScope,
    goBalances: () -> Unit,
    goSettings: () -> Unit,
    transactionsUiState: TransactionUiState,
    synchronizer: Synchronizer?,
    transactionHistoryViewModel: TransactionHistoryViewModel,
    walletSnapshot: WalletSnapshot?,
    walletRestoringState: WalletRestoringState,
) {
    if (null == synchronizer || null == walletSnapshot) {
        // TODO [#1146]: Consider moving CircularScreenProgressIndicator from Android layer to View layer
        // TODO [#1146]: Improve this by allowing screen composition and updating it after the data is available
        // TODO [#1146]: https://github.com/Electric-Coin-Company/zashi-android/issues/1146
        CircularScreenProgressIndicator()
    } else {
        Account(
            walletSnapshot = walletSnapshot,
            transactionsUiState = transactionsUiState,
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
            walletRestoringState = walletRestoringState
        )
    }
}
