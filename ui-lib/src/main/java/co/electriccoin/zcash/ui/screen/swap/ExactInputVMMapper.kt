package co.electriccoin.zcash.ui.screen.swap

import cash.z.ecc.android.sdk.ext.Conversions
import cash.z.ecc.android.sdk.ext.Conversions.ZEC_FORMATTER
import cash.z.ecc.android.sdk.model.FiatCurrency
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.repository.SwapMode
import co.electriccoin.zcash.ui.common.repository.SwapMode.SWAP
import co.electriccoin.zcash.ui.design.component.AssetCardState
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.NumberTextFieldInnerState
import co.electriccoin.zcash.ui.design.component.NumberTextFieldState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.listitem.SimpleListItemState
import co.electriccoin.zcash.ui.design.util.CurrencySymbolLocation
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber
import co.electriccoin.zcash.ui.design.util.stringResByNumber
import co.electriccoin.zcash.ui.screen.swap.ui.SwapAmountTextFieldState
import co.electriccoin.zcash.ui.screen.swap.ui.SwapAmountTextState
import co.electriccoin.zcash.ui.screen.swap.ui.SwapModeSelectorState
import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.absoluteValue

internal class ExactInputVMMapper : SwapVMMapper {
    override fun createState(
        internalState: InternalState,
        onBack: () -> Unit,
        onSwapInfoClick: () -> Unit,
        onSwapAssetPickerClick: () -> Unit,
        onSwapCurrencyTypeClick: () -> Unit,
        onSlippageClick: (BigDecimal?) -> Unit,
        onRequestSwapQuoteClick: (BigDecimal, String) -> Unit,
        onAddressChange: (String) -> Unit,
        onSwapModeChange: (SwapMode) -> Unit,
        onTextFieldChange: (NumberTextFieldInnerState) -> Unit,
        onQrCodeScannerClick: () -> Unit
    ): SwapState {
        val state = ExactInputInternalState(internalState)
        val textFieldState =
            createAmountTextFieldState(
                state = state,
                onSwapCurrencyTypeClick = onSwapCurrencyTypeClick,
                onTextFieldChange = onTextFieldChange
            )
        return SwapState(
            amountTextField = textFieldState,
            slippage =
                createSlippageState(
                    state = state,
                    onSlippageClick = onSlippageClick
                ),
            amountText =
                createAmountTextState(
                    state = state,
                    onSwapAssetPickerClick = onSwapAssetPickerClick
                ),
            primaryButton =
                createPrimaryButtonState(
                    textField = textFieldState,
                    state = state,
                    onRequestSwapQuoteClick = onRequestSwapQuoteClick
                ),
            swapModeSelectorState =
                SwapModeSelectorState(
                    swapMode = SWAP,
                    onClick = onSwapModeChange,
                    isEnabled = !state.isRequestingQuote
                ),
            address =
                createAddressState(
                    state = state,
                    onAddressChange = onAddressChange
                ),
            isAddressBookHintVisible = state.isAddressBookHintVisible,
            onBack = onBack,
            swapInfoButton = IconButtonState(R.drawable.ic_help, onClick = onSwapInfoClick),
            infoItems = createListItems(state),
            qrScannerButton = IconButtonState(
                icon = R.drawable.qr_code_icon,
                onClick = onQrCodeScannerClick
            )
        )
    }

    private fun createAmountTextFieldState(
        state: ExactInputInternalState,
        onSwapCurrencyTypeClick: () -> Unit,
        onTextFieldChange: (NumberTextFieldInnerState) -> Unit,
    ): SwapAmountTextFieldState {
        val amountFiat = state.getOriginFiatAmount()
        val zatoshiAmount = state.getZatoshi()
        return SwapAmountTextFieldState(
            title = stringRes("From"),
            error = if (state.totalSpendableBalance != null &&
                zatoshiAmount != null &&
                state.totalSpendableBalance.value < zatoshiAmount
            ) {
                stringRes("Insufficient funds")
            } else {
                null
            },
            token =
                AssetCardState(
                    ticker = stringRes(cash.z.ecc.sdk.ext.R.string.zcash_token_zec),
                    bigIcon = imageRes(R.drawable.ic_zec_round_full),
                    smallIcon = imageRes(R.drawable.ic_receive_shield),
                    onClick = null
                ),
            textFieldPrefix =
                when (state.currencyType) {
                    CurrencyType.TOKEN -> null
                    CurrencyType.FIAT -> imageRes(R.drawable.ic_send_usd)
                },
            textField =
                NumberTextFieldState(
                    innerState = state.amountTextState,
                    onValueChange = onTextFieldChange,
                    isEnabled = !state.isRequestingQuote
                ),
            secondaryText =
                when (state.currencyType) {
                    CurrencyType.TOKEN ->
                        stringResByDynamicCurrencyNumber(
                            amount = amountFiat ?: BigDecimal(0),
                            ticker = FiatCurrency.USD.symbol,
                            maxDecimals = 2
                        )

                    CurrencyType.FIAT -> stringResByDynamicCurrencyNumber(
                        (zatoshiAmount ?: 0).convertZatoshiToZecBigDecimal(),
                        "ZEC"
                    )
                },
            max =
                state.totalSpendableBalance?.let {
                    stringRes("Max: ") + stringRes(it, CurrencySymbolLocation.HIDDEN)
                },
            onSwapChange = onSwapCurrencyTypeClick,
            isSwapChangeEnabled = !state.isRequestingQuote
        )
    }

    private fun createAmountTextState(
        state: ExactInputInternalState,
        onSwapAssetPickerClick: (() -> Unit)?
    ): SwapAmountTextState =
        SwapAmountTextState(
            token =
                AssetCardState(
                    ticker = state.swapAsset?.tokenTicker?.let { stringRes(it) } ?: stringRes("Select token"),
                    bigIcon = state.swapAsset?.tokenIcon,
                    smallIcon = state.swapAsset?.chainIcon,
                    onClick = onSwapAssetPickerClick,
                    isEnabled = !state.isRequestingQuote
                ),
            title = stringRes("To"),
            subtitle = null,
            text =
                stringResByDynamicCurrencyNumber(
                    amount = state.getDestinationAssetAmount() ?: 0,
                    ticker = state.swapAsset?.tokenTicker.orEmpty(),
                    symbolLocation = CurrencySymbolLocation.HIDDEN
                ),
            secondaryText =
                stringResByDynamicCurrencyNumber(
                    amount = state.getOriginFiatAmount() ?: 0,
                    ticker = FiatCurrency.USD.symbol
                ),
        )

    private fun createSlippageState(
        state: ExactInputInternalState,
        onSlippageClick: (BigDecimal?) -> Unit
    ): ButtonState {
        val amount = state.slippage
        return ButtonState(
            text = stringResByNumber(amount) + stringRes("%"),
            icon = R.drawable.ic_swap_slippage,
            onClick = { onSlippageClick(state.getOriginFiatAmount()) },
            isEnabled = !state.isRequestingQuote
        )
    }

    private fun createPrimaryButtonState(
        textField: SwapAmountTextFieldState,
        state: ExactInputInternalState,
        onRequestSwapQuoteClick: (BigDecimal, String) -> Unit
    ): ButtonState {
        val amount = textField.textField.innerState.amount
        return ButtonState(
            text = stringRes("Confirm"),
            onClick = {
                state.getOriginTokenAmount()?.let {
                    onRequestSwapQuoteClick(it, state.addressText)
                }
            },
            isEnabled =
                state.swapAsset != null &&
                    !textField.isError &&
                    amount != null &&
                    amount > BigDecimal(0) &&
                    state.addressText.isNotBlank() &&
                    !state.isRequestingQuote,
            isLoading = state.isRequestingQuote
        )
    }

    private fun createAddressState(state: ExactInputInternalState, onAddressChange: (String) -> Unit): TextFieldState {
        val text = state.addressText

        return TextFieldState(
            error =
                when {
                    text.isEmpty() -> null
                    text.isBlank() -> stringRes("")
                    else -> null
                },
            value = stringRes(text),
            onValueChange = onAddressChange,
            isEnabled = !state.isRequestingQuote
        )
    }

    private fun createListItems(state: ExactInputInternalState): List<SimpleListItemState> {
        val zecToAssetExchangeRate = state.getZecToDestinationAssetExchangeRate()
        val assetTokenTicker = state.swapAsset?.tokenTicker
        return if (zecToAssetExchangeRate == null || assetTokenTicker == null) {
            emptyList()
        } else {
            listOf(
                SimpleListItemState(
                    stringRes("Rate"),
                    stringRes("1 ZEC = ") +
                        stringResByDynamicCurrencyNumber(zecToAssetExchangeRate, assetTokenTicker)
                )
            )
        }
    }
}

internal data class ExactInputInternalState(
    override val swapAsset: SwapAsset?,
    override val currencyType: CurrencyType,
    override val totalSpendableBalance: Zatoshi?,
    override val amountTextState: NumberTextFieldInnerState,
    override val addressText: String,
    override val slippage: BigDecimal,
    override val isAddressBookHintVisible: Boolean,
    override val zecSwapAsset: SwapAsset?,
    override val swapMode: SwapMode,
    override val isRequestingQuote: Boolean
) : InternalState {

    constructor(original: InternalState) : this(
        swapAsset = original.swapAsset,
        currencyType = original.currencyType,
        totalSpendableBalance = original.totalSpendableBalance,
        amountTextState = original.amountTextState,
        addressText = original.addressText,
        slippage = original.slippage,
        isAddressBookHintVisible = original.isAddressBookHintVisible,
        zecSwapAsset = original.zecSwapAsset,
        swapMode = original.swapMode,
        isRequestingQuote = original.isRequestingQuote
    )

    fun getOriginFiatAmount(): BigDecimal? {
        return when (currencyType) {
            CurrencyType.TOKEN -> {
                val tokenAmount = amountTextState.amount
                if (tokenAmount == null || zecSwapAsset == null) null else tokenAmount.multiply(zecSwapAsset.usdPrice)
            }

            CurrencyType.FIAT -> amountTextState.amount
        }
    }

    fun getOriginTokenAmount(): BigDecimal? {
        val fiatAmount = amountTextState.amount
        return when (currencyType) {
            CurrencyType.TOKEN -> fiatAmount
            CurrencyType.FIAT ->
                if (fiatAmount == null || zecSwapAsset == null) {
                    null
                } else {
                    fiatAmount.divide(zecSwapAsset.usdPrice, MathContext.DECIMAL128)
                }
        }
    }

    fun getDestinationAssetAmount(): BigDecimal? {
        val amountToken = getOriginTokenAmount()
        return if (zecSwapAsset?.usdPrice == null || swapAsset?.usdPrice == null || amountToken == null) {
            null
        } else {
            amountToken
                .multiply(zecSwapAsset.usdPrice, MathContext.DECIMAL128)
                .divide(swapAsset.usdPrice, MathContext.DECIMAL128)
        }
    }

    fun getZecToDestinationAssetExchangeRate(): BigDecimal? {
        val zecUsdPrice = zecSwapAsset?.usdPrice
        val assetUsdPrice = swapAsset?.usdPrice
        if (zecUsdPrice == null || assetUsdPrice == null) return null
        return zecUsdPrice.divide(assetUsdPrice, MathContext.DECIMAL128)
    }

    fun getZatoshi() = getOriginTokenAmount()?.convertZecToZatoshiLong()
}

internal fun BigDecimal.convertZecToZatoshiLong(): Long {
    if (this < BigDecimal.ZERO) {
        throw IllegalArgumentException(
            "Invalid ZEC value: $this. ZEC is represented by notes and" +
                " cannot be negative"
        )
    }
    return this.multiply(Conversions.ONE_ZEC_IN_ZATOSHI, MathContext.DECIMAL128).toLong().absoluteValue
}

internal fun Long.convertZatoshiToZecBigDecimal(scale: Int = ZEC_FORMATTER.maximumFractionDigits): BigDecimal =
    BigDecimal(this, MathContext.DECIMAL128)
        .divide(
            Conversions.ONE_ZEC_IN_ZATOSHI,
            MathContext.DECIMAL128
        ).setScale(scale, ZEC_FORMATTER.roundingMode)

