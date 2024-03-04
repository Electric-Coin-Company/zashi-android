@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.account

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.internal.Twig
import co.electriccoin.zcash.spackle.ClipboardManagerUtil
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.screen.account.view.Account
import co.electriccoin.zcash.ui.screen.settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

@Composable
internal fun WrapAccount(
    activity: ComponentActivity,
    goBalances: () -> Unit,
    goSettings: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val walletViewModel by activity.viewModels<WalletViewModel>()
    val walletSnapshot = walletViewModel.walletSnapshot.collectAsStateWithLifecycle().value

    val settingsViewModel by activity.viewModels<SettingsViewModel>()

    val isKeepScreenOnWhileSyncing = settingsViewModel.isKeepScreenOnWhileSyncing.collectAsStateWithLifecycle().value

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    val transactionHistoryState = walletViewModel.transactionHistoryState.collectAsStateWithLifecycle().value

    Twig.info { "Current transaction history state: $transactionHistoryState" }

    if (null == walletSnapshot) {
        // TODO [#1146]: Consider moving CircularScreenProgressIndicator from Android layer to View layer
        // TODO [#1146]: Improve this by allowing screen composition and updating it after the data is available
        // TODO [#1146]: https://github.com/Electric-Coin-Company/zashi-android/issues/1146
        CircularScreenProgressIndicator()
    } else {
        Account(
            walletSnapshot = walletSnapshot,
            isKeepScreenOnWhileSyncing = isKeepScreenOnWhileSyncing,
            transactionState = transactionHistoryState,
            onItemClick = { tx ->
                Twig.debug { "Transaction item clicked - querying memos..." }
                val memos = synchronizer?.getMemos(tx)
                scope.launch {
                    memos?.toList()?.let {
                        val merged = it.joinToString().ifEmpty { "-" }
                        Twig.info { "Transaction memos: count: ${it.size}, contains: $merged" }
                        ClipboardManagerUtil.copyToClipboard(
                            activity.applicationContext,
                            activity.getString(R.string.history_item_clipboard_tag),
                            merged
                        )
                    }
                }
            },
            onTransactionIdClick = { txId ->
                Twig.debug { "Transaction ID clicked: $txId" }
                ClipboardManagerUtil.copyToClipboard(
                    activity.applicationContext,
                    activity.getString(R.string.history_id_clipboard_tag),
                    txId
                )
            },
            goBalances = goBalances,
            goSettings = goSettings,
        )

        // For benchmarking purposes
        activity.reportFullyDrawn()
    }
}
