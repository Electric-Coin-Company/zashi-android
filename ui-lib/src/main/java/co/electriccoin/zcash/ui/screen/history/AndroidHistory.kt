package co.electriccoin.zcash.ui.screen.history

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.internal.Twig
import co.electriccoin.zcash.spackle.ClipboardManagerUtil
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.screen.history.view.History
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

@Composable
internal fun MainActivity.WrapHistory(goBack: () -> Unit) {
    WrapHistory(
        activity = this,
        goBack = goBack
    )
}

@Composable
internal fun WrapHistory(
    activity: ComponentActivity,
    goBack: () -> Unit
) {
    val queryScope = rememberCoroutineScope()

    val walletViewModel by activity.viewModels<WalletViewModel>()

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    val transactionHistoryState =
        walletViewModel.transactionHistoryState.collectAsStateWithLifecycle().value

    Twig.info { "Current transaction history state: $transactionHistoryState" }

    if (synchronizer == null) {
        // TODO [#1146]: Consider moving CircularScreenProgressIndicator from Android layer to View layer
        // TODO [#1146]: Improve this by allowing screen composition and updating it after the data is available
        // TODO [#1146]: https://github.com/Electric-Coin-Company/zashi-android/issues/1146
        CircularScreenProgressIndicator()
    } else {
        History(
            transactionState = transactionHistoryState,
            onBack = goBack,
            onItemClick = { tx ->
                Twig.debug { "Querying transaction memos..." }
                val memos = synchronizer.getMemos(tx)
                queryScope.launch {
                    memos.toList().run {
                        val merged = joinToString().ifEmpty { "-" }
                        Twig.info { "Transaction memos: count: $size, contains: $merged" }
                        ClipboardManagerUtil.copyToClipboard(
                            activity.applicationContext,
                            activity.getString(R.string.history_item_clipboard_tag),
                            merged
                        )
                    }
                }
            },
        )
    }
}
