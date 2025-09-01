package co.electriccoin.zcash.ui.screen.pay

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarVM
import co.electriccoin.zcash.ui.screen.balances.BalanceWidgetArgs
import co.electriccoin.zcash.ui.screen.balances.BalanceWidgetVM
import co.electriccoin.zcash.ui.screen.swap.SwapCancelView
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PayScreen() {
    val vm = koinViewModel<PayVM>()
    val balanceVM = koinViewModel<BalanceWidgetVM> {
        parametersOf(
            BalanceWidgetArgs(
                isBalanceButtonEnabled = true,
                isExchangeRateButtonEnabled = false,
                showDust = true
            )
        )
    }
    val appBarVM = koinViewModel<ZashiTopAppBarVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    val balanceState by balanceVM.state.collectAsStateWithLifecycle()
    val appBarState by appBarVM.state.collectAsStateWithLifecycle()
    val cancelState by vm.cancelState.collectAsStateWithLifecycle()
    state?.let { PayView(it, balanceState, appBarState) }
    BackHandler(state != null) { state?.onBack?.invoke() }
    SwapCancelView(cancelState)
}

@Serializable
object PayArgs
