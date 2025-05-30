package co.electriccoin.zcash.ui.screen.swap.slippage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.usecase.GetSlippageUseCase
import co.electriccoin.zcash.ui.common.usecase.SetSlippageUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.NumberTextFieldState
import co.electriccoin.zcash.ui.design.util.stringRes
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

@Suppress("TooManyFunctions")
class SwapSlippageViewModel(
    getSlippage: GetSlippageUseCase,
    private val setSlippage: SetSlippageUseCase,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    private val slippageSelection =
        MutableStateFlow(
            if (getSlippage() < TOTAL_STEPS) {
                SlippageSliderState.Selection.ByPercent(getSlippage())
            } else {
                SlippageSliderState.Selection.Custom
            }
        )

    private val isCustomSlippageVisible =
        slippageSelection
            .map {
                isCustomSlippageVisible(it)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = isCustomSlippageVisible(slippageSelection.value)
            )

    private val customSlippage = MutableStateFlow(createCustomSlippageInitialState(getSlippage))

    private val customSlippageState =
        combine(
            isCustomSlippageVisible,
            customSlippage
        ) { isCustomTextFieldVisible, customSlippage ->
            createCustomSlippageStateNormalizedState(customSlippage, isCustomTextFieldVisible)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = createCustomSlippageStateNormalizedState(customSlippage.value, isCustomSlippageVisible.value)
        )

    private val slippageSliderState =
        slippageSelection
            .map {
                createSlippageSliderState(it)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = createSlippageSliderState(slippageSelection.value)
            )

    private val actualSelection =
        combine(
            slippageSelection,
            customSlippage
        ) { slippageSelection, customSlippage ->
            getActualSelection(slippageSelection, customSlippage)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = getActualSelection(slippageSelection.value, customSlippage.value)
        )

    private val slippageInfoState =
        actualSelection
            .map {
                createSlippageInfoState(it)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = createSlippageInfoState(actualSelection.value)
            )

    private val confirmButtonState =
        actualSelection
            .map {
                createButtonState(it)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = createButtonState(actualSelection.value)
            )

    val state: StateFlow<SwapSlippageState> =
        combine(
            slippageSliderState,
            customSlippageState,
            slippageInfoState,
            confirmButtonState
        ) { slippageSliderState, customSlippageState, slippageInfoState, confirmButtonState ->
            createState(slippageSliderState, customSlippageState, slippageInfoState, confirmButtonState)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue =
                createState(
                    slippageSliderState = slippageSliderState.value,
                    customSlippageState = customSlippageState.value,
                    slippageInfoState = slippageInfoState.value,
                    confirmButtonState = confirmButtonState.value
                )
        )

    private fun createState(
        slippageSliderState: SlippageSliderState,
        customSlippageState: NumberTextFieldState?,
        slippageInfoState: SwapSlippageInfoState?,
        confirmButtonState: ButtonState
    ) = SwapSlippageState(
        slider = slippageSliderState,
        customSlippage = customSlippageState,
        info = slippageInfoState,
        primary = confirmButtonState,
        onBack = ::onBack
    )

    private fun createButtonState(amount: Int?) =
        ButtonState(
            text = stringRes("Confirm"),
            isEnabled = amount != null,
            onClick = ::onConfirmClick
        )

    @Suppress("ForbiddenComment") // TODO swap
    private fun createSlippageInfoState(amount: Int?): SwapSlippageInfoState? {
        if (amount == null) return null

        return SwapSlippageInfoState(
            title = stringRes("You will pay up to XY for the swap."), // TODO swap - implement with next screen
            description =
                stringRes(
                    "Any unused portion of the slippage fee will be refunded if the swap executes with" +
                        " lower slippage than expected."
                ),
            mode =
                when {
                    amount <= 10 -> SwapSlippageInfoState.Mode.LOW
                    amount < 30 -> SwapSlippageInfoState.Mode.MEDIUM
                    else -> SwapSlippageInfoState.Mode.HIGH
                }
        )
    }

    private fun getActualSelection(
        slippageSelection: SlippageSliderState.Selection,
        customSlippage: NumberTextFieldState
    ) = when (slippageSelection) {
        is SlippageSliderState.Selection.ByPercent -> slippageSelection.percent
        SlippageSliderState.Selection.Custom -> {
            val customAmount = customSlippage.amount
            if (customAmount != null) {
                (customAmount.toDouble() * STEPS_IN_ONE_PERCENT).toInt()
            } else {
                null
            }
        }
    }

    private fun createSlippageSliderState(it: SlippageSliderState.Selection) =
        SlippageSliderState(
            selected = it,
            percentRange = 0..STEPS step 1,
            labelRange = 0..STEPS step STEPS_IN_ONE_PERCENT.toInt(),
            onValueChange = ::onSlippageChanged
        )

    private fun isCustomSlippageVisible(selection: SlippageSliderState.Selection) =
        selection is SlippageSliderState.Selection.Custom

    private fun createCustomSlippageInitialState(getSlippage: GetSlippageUseCase): NumberTextFieldState {
        val amount = getSlippage().let { BigDecimal.valueOf(it.toDouble() / STEPS_IN_ONE_PERCENT) }
        return NumberTextFieldState(
            text = stringResByNumber(amount),
            amount = amount,
            errorString = stringRes(""),
            onValueChange = ::onCustomSlippageChanged
        )
    }

    private fun createCustomSlippageStateNormalizedState(
        customSlippage: NumberTextFieldState,
        isCustomTextFieldVisible: Boolean
    ) = customSlippage.takeIf { isCustomTextFieldVisible }

    private fun onSlippageChanged(new: SlippageSliderState.Selection) = slippageSelection.update { new }

    private fun onCustomSlippageChanged(new: NumberTextFieldState) {
        customSlippage.update { new }
    }

    private fun onConfirmClick() = actualSelection.value?.let { setSlippage(it) }

    private fun onBack() = navigationRouter.back()
}

private const val STEPS_IN_ONE_PERCENT = 10.0
private const val STEPS = 30
private const val TOTAL_STEPS = 40
