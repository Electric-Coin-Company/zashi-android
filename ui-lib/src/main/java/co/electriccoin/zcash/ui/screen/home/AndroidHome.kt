@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarViewModel
import co.electriccoin.zcash.ui.screen.balances.BalanceWidgetArgs
import co.electriccoin.zcash.ui.screen.balances.BalanceWidgetViewModel
import co.electriccoin.zcash.ui.screen.restoresuccess.WrapRestoreSuccess
import co.electriccoin.zcash.ui.screen.transactionhistory.widget.TransactionHistoryWidgetViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun AndroidHome() {
    val topAppBarViewModel = koinActivityViewModel<ZashiTopAppBarViewModel>()
    val balanceWidgetViewModel =
        koinViewModel<BalanceWidgetViewModel> {
            parametersOf(
                BalanceWidgetArgs(
                    isBalanceButtonEnabled = false,
                    isExchangeRateButtonEnabled = true,
                    showDust = false,
                )
            )
        }
    val homeViewModel = koinViewModel<HomeViewModel>()
    val transactionHistoryWidgetViewModel = koinViewModel<TransactionHistoryWidgetViewModel>()
    val restoreDialogState by homeViewModel.restoreDialogState.collectAsStateWithLifecycle()
    val appBarState by topAppBarViewModel.state.collectAsStateWithLifecycle()
    val balanceState by balanceWidgetViewModel.state.collectAsStateWithLifecycle()
    val state by homeViewModel.state.collectAsStateWithLifecycle()
    val transactionWidgetState by transactionHistoryWidgetViewModel.state.collectAsStateWithLifecycle()

    state?.let {
        HomeView(
            appBarState = appBarState,
            balanceWidgetState = balanceState,
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
