package co.electriccoin.zcash.ui.screen.swap.receiver

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarViewModel
import co.electriccoin.zcash.ui.screen.balances.BalanceWidgetArgs
import co.electriccoin.zcash.ui.screen.balances.BalanceWidgetViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SwapReceiverScreen() {
    val balanceVM =
        koinViewModel<BalanceWidgetViewModel> {
            parametersOf(
                BalanceWidgetArgs(
                    isBalanceButtonEnabled = true,
                    isExchangeRateButtonEnabled = false,
                    showDust = true
                )
            )
        }
    val topAppBarViewModel = koinActivityViewModel<ZashiTopAppBarViewModel>()
    val vm = koinViewModel<SwapReceiverViewModel>()
    val state by vm.state.collectAsStateWithLifecycle()
    val balanceState by balanceVM.state.collectAsStateWithLifecycle()
    val topAppBarState by topAppBarViewModel.state.collectAsStateWithLifecycle()
    SwapReceiverView(
        state = state,
        balanceWidgetState = balanceState,
        appBarState = topAppBarState
    )
}

@Serializable
object SwapReceiver
