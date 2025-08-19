package co.electriccoin.zcash.ui.screen.swap.slippage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.FiatCurrency
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.usecase.GetSlippageUseCase
import co.electriccoin.zcash.ui.common.usecase.SetSlippageUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber
import co.electriccoin.zcash.ui.design.util.stringResByNumber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import java.math.MathContext

@Suppress("TooManyFunctions")
class SwapSlippageVM(
    swapSlippageArgs: SwapSlippageArgs,
    getSlippage: GetSlippageUseCase,
    private val setSlippage: SetSlippageUseCase,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    private val fiatAmount = swapSlippageArgs.fiatAmount?.toBigDecimal()

    private val slippageSelection: MutableStateFlow<BigDecimal?> = MutableStateFlow(getSlippage())

    private val slippagePickerState =
        slippageSelection
            .map {
                createSlippagePickerState(it)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = createSlippagePickerState(slippageSelection.value)
            )

    private val slippageInfoState =
        slippageSelection
            .map {
                createSlippageInfoState(it)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = createSlippageInfoState(slippageSelection.value)
            )

    private val confirmButtonState =
        slippageSelection
            .map {
                createButtonState(it)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = createButtonState(slippageSelection.value)
            )

    val state: StateFlow<SwapSlippageState> =
        combine(
            slippagePickerState,
            slippageInfoState,
            confirmButtonState
        ) { slippagePickerState, slippageInfoState, confirmButtonState ->
            createState(slippagePickerState, slippageInfoState, confirmButtonState)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue =
                createState(
                    slippagePickerState = slippagePickerState.value,
                    slippageInfoState = slippageInfoState.value,
                    confirmButtonState = confirmButtonState.value
                )
        )

    private fun createState(
        slippagePickerState: SlippagePickerState,
        slippageInfoState: SwapSlippageInfoState?,
        confirmButtonState: ButtonState
    ) = SwapSlippageState(
        picker = slippagePickerState,
        info = slippageInfoState,
        primary = confirmButtonState,
        onBack = ::onBack
    )

    private fun createButtonState(amount: BigDecimal?) =
        ButtonState(
            text = stringRes("Confirm"),
            isEnabled = amount != null,
            onClick = ::onConfirmClick
        )

    @Suppress("MagicNumber")
    private fun createSlippageInfoState(percent: BigDecimal?): SwapSlippageInfoState? {
        if (percent == null) return null

        val percentString = stringResByNumber(percent, minDecimals = 0) + stringRes("%")

        val result =
            when {
                percent > BigDecimal("30") -> stringRes("Please enter maximum slippage of 30%.")
                fiatAmount == null ->
                    stringRes("You may receive up to ") +
                        percentString +
                        stringRes(" less than quoted.")
                else -> {
                    val slippageFiat =
                        fiatAmount.multiply(
                            percent.divide(BigDecimal(100), MathContext.DECIMAL128),
                            MathContext.DECIMAL128
                        )
                    stringRes("You may receive up to ") +
                        percentString +
                        stringRes(" ") +
                        stringRes("(") +
                        stringResByDynamicCurrencyNumber(slippageFiat, FiatCurrency.USD.symbol) +
                        stringRes(")") +
                        stringRes(" less than quoted.")
                }
            }

        return SwapSlippageInfoState(
            title = result,
            mode =
                when {
                    percent <= BigDecimal("1") -> SwapSlippageInfoState.Mode.LOW
                    percent <= BigDecimal("2") -> SwapSlippageInfoState.Mode.MEDIUM
                    else -> SwapSlippageInfoState.Mode.HIGH
                }
        )
    }

    private fun createSlippagePickerState(amount: BigDecimal?) =
        SlippagePickerState(
            amount = amount,
            onAmountChange = ::onSlippageChanged
        )

    private fun onSlippageChanged(new: BigDecimal?) = slippageSelection.update { new }

    private fun onConfirmClick() = slippageSelection.value?.let { setSlippage(it) }

    private fun onBack() = navigationRouter.back()
}
