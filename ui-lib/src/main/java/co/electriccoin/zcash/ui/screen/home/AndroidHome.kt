@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarViewModel
import co.electriccoin.zcash.ui.screen.balances.BalanceViewModel
import co.electriccoin.zcash.ui.screen.restoresuccess.WrapRestoreSuccess
import co.electriccoin.zcash.ui.screen.transactionhistory.widget.TransactionHistoryWidgetViewModel
import kotlinx.serialization.Serializable

@Composable
internal fun AndroidHome() {
    val topAppBarViewModel = koinActivityViewModel<ZashiTopAppBarViewModel>()
    val balanceViewModel = koinActivityViewModel<BalanceViewModel>()
    val homeViewModel = koinActivityViewModel<HomeViewModel>()
    val transactionHistoryWidgetViewModel = koinActivityViewModel<TransactionHistoryWidgetViewModel>()
    val restoreDialogState by homeViewModel.restoreDialogState.collectAsStateWithLifecycle()
    val appBarState by topAppBarViewModel.state.collectAsStateWithLifecycle()
    val balanceState by balanceViewModel.state.collectAsStateWithLifecycle()
    val state by homeViewModel.state.collectAsStateWithLifecycle()
    val transactionWidgetState by transactionHistoryWidgetViewModel.state.collectAsStateWithLifecycle()

    state?.let {
        HomeView(
            appBarState = appBarState,
            balanceState = balanceState,
            state = it,
            transactionWidgetState = transactionWidgetState
        )
    }

    if (restoreDialogState != null) {
        WrapRestoreSuccess(
            onComplete = { restoreDialogState?.onClick?.invoke() }
        )
    }
}

@Serializable
object Home
