package co.electriccoin.zcash.ui.screen.swap.amount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.FiatCurrency
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.usecase.ConvertFiatToZatoshiUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedSwapAssetUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSlippageUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTotalFiatBalanceUseCase
import co.electriccoin.zcash.ui.design.component.AssetCardState
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.NumberTextFieldState
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber
import co.electriccoin.zcash.ui.design.util.stringResByNumber
import co.electriccoin.zcash.ui.screen.swap.slippage.SwapSlippage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import java.math.MathContext

@Suppress("TooManyFunctions")
class SwapAmountViewModel(
    getSlippage: GetSlippageUseCase,
    getSelectedSwapAsset: GetSelectedSwapAssetUseCase,
    getTotalFiatBalance: GetTotalFiatBalanceUseCase,
    private val navigationRouter: NavigationRouter,
    private val convertFiatToZatoshi: ConvertFiatToZatoshiUseCase,
) : ViewModel() {
    private val totalFiatBalance =
        getTotalFiatBalance
            .observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = getTotalFiatBalance()
            )

    private val currencyType = MutableStateFlow(CurrencyType.TOKEN)

    private val innerTextFieldState = MutableStateFlow(NumberTextFieldState(onValueChange = ::onTextFieldChange))

    private val fiatAmount =
        combine(
            innerTextFieldState,
            currencyType,
            getSelectedSwapAsset.observe()
        ) { textFieldState, textFieldType, swapAsset ->
            getFiatAmount(textFieldType, textFieldState, swapAsset)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue =
                getFiatAmount(
                    currencyType = currencyType.value,
                    textFieldState = innerTextFieldState.value,
                    swapAsset = getSelectedSwapAsset.observe().value
                )
        )

    private val textFieldState =
        combine(
            getSelectedSwapAsset.observe(),
            currencyType,
            totalFiatBalance,
            innerTextFieldState,
            fiatAmount
        ) { asset, type, totalFiatBalance, innerTextFieldState, fiatAmount ->
            createTextFieldState(
                swapAsset = asset,
                currencyType = type,
                totalFiatBalance = totalFiatBalance,
                textFieldState = innerTextFieldState,
                fiatAmount = fiatAmount
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue =
                createTextFieldState(
                    swapAsset = getSelectedSwapAsset.observe().value,
                    currencyType = currencyType.value,
                    textFieldState = innerTextFieldState.value,
                    totalFiatBalance = totalFiatBalance.value,
                    fiatAmount = fiatAmount.value
                )
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val textState =
        fiatAmount
            .flatMapLatest { fiatAmount ->
                convertFiatToZatoshi
                    .observe(fiatAmount)
                    .map {
                        createTextState(fiatAmount = fiatAmount, it)
                    }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue =
                    createTextState(
                        fiatAmount.value,
                        convertFiatToZatoshi(fiatAmount.value)
                    )
            )

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

    private val primaryButtonState =
        textFieldState
            .map { textField ->
                createPrimaryButtonState(textField)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = createPrimaryButtonState(textFieldState.value)
            )

    val state: StateFlow<SwapAmountState> =
        combine(
            slippageState,
            textState,
            textFieldState,
            primaryButtonState
        ) { slippage, text, textField, button ->
            createState(
                textField = textField,
                slippage = slippage,
                text = text,
                primaryButton = button
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue =
                createState(
                    textField = textFieldState.value,
                    slippage = slippageState.value,
                    text = textState.value,
                    primaryButton = primaryButtonState.value
                )
        )

    private fun createState(
        textField: SwapTextFieldState,
        slippage: ButtonState,
        text: SwapTextState,
        primaryButton: ButtonState
    ) =
        SwapAmountState(
            recipientGets = textField,
            slippage = slippage,
            youPay = text,
            primaryButton = primaryButton,
            onBack = ::onBack,
            swapWidgetState = SwapWidgetState(
                selection = SwapWidgetState.Selection.SWAP,
                onClick = { }
            ),
            swapInfoButton = IconButtonState(R.drawable.ic_help) {},
            infoItems = listOf()
        )

    @Suppress("CyclomaticComplexMethod")
    private fun createTextFieldState(
        swapAsset: SwapAsset?,
        currencyType: CurrencyType,
        textFieldState: NumberTextFieldState,
        totalFiatBalance: BigDecimal?,
        fiatAmount: BigDecimal?,
    ): SwapTextFieldState =
        SwapTextFieldState(
            title = stringRes("Recipient gets"),
            error =
                when (currencyType) {
                    CurrencyType.TOKEN -> {
                        if (totalFiatBalance != null && (fiatAmount ?: BigDecimal(0)) > totalFiatBalance) {
                            stringRes("Insufficient funds")
                        } else {
                            null
                        }
                    }

                    CurrencyType.FIAT -> {
                        if (totalFiatBalance != null && (fiatAmount ?: BigDecimal(0)) > totalFiatBalance) {
                            stringRes("Insufficient funds")
                        } else {
                            null
                        }
                    }
                },
            token =
                AssetCardState(
                    ticker = swapAsset?.tokenTicker?.let { stringRes(it) } ?: stringRes(""),
                    token = swapAsset?.tokenIcon,
                    chain = swapAsset?.chainIcon,
                    {}
                ),
            textFieldPrefix =
                when (currencyType) {
                    CurrencyType.TOKEN -> null
                    CurrencyType.FIAT -> imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_zec_symbol)
                },
            textField = textFieldState,
            secondaryText =
                when (currencyType) {
                    CurrencyType.TOKEN ->
                        stringResByDynamicCurrencyNumber(
                            amount = fiatAmount ?: BigDecimal(0),
                            ticker = FiatCurrency.USD.symbol,
                            maxDecimals = 2
                        )

                    CurrencyType.FIAT -> {
                        val tokenAmount =
                            if (fiatAmount == null || swapAsset == null) {
                                BigDecimal.valueOf(0)
                            } else {
                                fiatAmount.divide(swapAsset.usdPrice, MathContext.DECIMAL128)
                            }
                        stringResByDynamicCurrencyNumber(tokenAmount, swapAsset?.tokenTicker.orEmpty())
                    }
                },
            max =
                totalFiatBalance?.let {
                    stringResByDynamicCurrencyNumber(it, FiatCurrency.USD.symbol, maxDecimals = 2)
                },
            onSwapChange = ::onSwapChangeClick
        )

    private fun createTextState(
        fiatAmount: BigDecimal?,
        zatoshi: Zatoshi?
    ): SwapTextState =
        SwapTextState(
            token =
                AssetCardState(
                    stringRes(cash.z.ecc.sdk.ext.R.string.zcash_token_zec),
                    token = imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_token_zec),
                    chain = imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_chain_zec),
                    {}
                ),
            title = stringRes("You pay"),
            text = stringResByDynamicCurrencyNumber(fiatAmount ?: 0, FiatCurrency.USD.symbol),
            secondaryText = zatoshi?.let { stringRes(it) },
            max = null
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

    private fun createPrimaryButtonState(textField: SwapTextFieldState): ButtonState {
        val amount = textField.textField.amount
        return ButtonState(
            text = stringRes("Confirm"),
            onClick = ::onPrimaryClick,
            isEnabled = !textField.isError && amount != null && amount > BigDecimal(0)
        )
    }

    private fun getFiatAmount(
        currencyType: CurrencyType,
        textFieldState: NumberTextFieldState,
        swapAsset: SwapAsset?
    ) = when (currencyType) {
        CurrencyType.TOKEN -> {
            val tokenAmount = textFieldState.amount
            if (tokenAmount == null || swapAsset == null) {
                null
            } else {
                tokenAmount.multiply(swapAsset.usdPrice)
            }
        }

        CurrencyType.FIAT -> textFieldState.amount
    }

    private fun onSlippageClick() = navigationRouter.forward(SwapSlippage)

    private fun onBack() = navigationRouter.back()

    private fun onSwapChangeClick() {
        currencyType.update {
            when (it) {
                CurrencyType.TOKEN -> CurrencyType.FIAT
                CurrencyType.FIAT -> CurrencyType.TOKEN
            }
        }
    }

    private fun onTextFieldChange(new: NumberTextFieldState) {
        innerTextFieldState.update { new }
    }

    @Suppress("ForbiddenComment")
    private fun onPrimaryClick() {
        // TODO swap
    }
}

private enum class CurrencyType { TOKEN, FIAT }
