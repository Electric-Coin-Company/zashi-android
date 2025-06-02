package co.electriccoin.zcash.ui.screen.swap.amount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.GetSlippageUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber
import co.electriccoin.zcash.ui.design.util.stringResByNumber
import co.electriccoin.zcash.ui.screen.swap.slippage.SwapSlippage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.math.BigDecimal

class SwapAmountViewModel(
    getSlippage: GetSlippageUseCase,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    private val slippageState =
        getSlippage
            .observe()
            .map { slippage ->
                createSlippageState(slippage)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = createSlippageState(getSlippage.observe().value)
            )

    val state: StateFlow<SwapAmountState> =
        slippageState
            .map { slippage ->
                createState(slippage)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = createState(slippageState.value)
            )

    @Suppress("MagicNumber")
    private fun createState(slippage: ButtonState) =
        SwapAmountState(
            recipientGets =
                SwapTextFieldState(
                    token = SwapTokenState(stringRes("USDT")),
                    title = stringRes("Recipient gets"),
                    symbol = stringRes("$"),
                    primaryText = TextFieldState(value = stringRes("")) {},
                    primaryPlaceholder = stringResByDynamicCurrencyNumber(0, "$"),
                    secondaryText = stringResByDynamicCurrencyNumber(100, "USDT"),
                    exchangeRate = stringResByDynamicCurrencyNumber(100, "$"),
                    onSwapChange = {},
                ),
            slippage = slippage,
            youPay =
                SwapTextState(
                    token =
                        SwapTokenState(
                            stringRes("ZEC")
                        ),
                    title = stringRes("You pay"),
                    text = stringResByDynamicCurrencyNumber(101, "$"),
                    secondaryText = stringResByDynamicCurrencyNumber(2.47123, "ZEC")
                ),
            primaryButton =
                ButtonState(
                    stringRes("Get a quote")
                ),
            onBack = ::onBack
        )

    @Suppress("MagicNumber")
    private fun createSlippageState(slippage: Int): ButtonState {
        val amount = BigDecimal.valueOf(slippage.toDouble()) / BigDecimal(10.0)
        return ButtonState(
            text = stringResByNumber(amount) + stringRes("%"),
            icon = R.drawable.ic_swap_slippage,
            onClick = ::onSlippageClick
        )
    }

    private fun onSlippageClick() = navigationRouter.forward(SwapSlippage)

    private fun onBack() = navigationRouter.back()
}
