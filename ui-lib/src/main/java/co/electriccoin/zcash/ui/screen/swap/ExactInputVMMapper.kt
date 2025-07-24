package co.electriccoin.zcash.ui.screen.swap

import cash.z.ecc.android.sdk.ext.Conversions
import cash.z.ecc.android.sdk.ext.Conversions.ZEC_FORMATTER
import cash.z.ecc.android.sdk.model.FiatCurrency
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.model.SwapMode
import co.electriccoin.zcash.ui.common.repository.SwapAssetsData
import co.electriccoin.zcash.ui.common.repository.SwapAssetsData.Error.*
import co.electriccoin.zcash.ui.design.component.AssetCardState
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ButtonStyle
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.NumberTextFieldInnerState
import co.electriccoin.zcash.ui.design.component.NumberTextFieldState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.listitem.SimpleListItemState
import co.electriccoin.zcash.ui.design.util.TickerLocation
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber
import co.electriccoin.zcash.ui.design.util.stringResByDynamicNumber
import co.electriccoin.zcash.ui.design.util.stringResByNumber
import co.electriccoin.zcash.ui.screen.swap.CurrencyType.FIAT
import co.electriccoin.zcash.ui.screen.swap.CurrencyType.TOKEN
import co.electriccoin.zcash.ui.screen.swap.ui.SwapAmountTextFieldState
import co.electriccoin.zcash.ui.screen.swap.ui.SwapAmountTextState
import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.absoluteValue

internal class ExactInputVMMapper : SwapVMMapper {
    override fun createState(
        internalState: InternalState,
        onBack: () -> Unit,
        onSwapInfoClick: () -> Unit,
        onSwapAssetPickerClick: () -> Unit,
        onSwapCurrencyTypeClick: (BigDecimal) -> Unit,
        onSlippageClick: (BigDecimal?) -> Unit,
        onRequestSwapQuoteClick: (BigDecimal, String) -> Unit,
        onTryAgainClick: () -> Unit,
        onAddressChange: (String) -> Unit,
        onSwapModeChange: (SwapMode) -> Unit,
        onTextFieldChange: (NumberTextFieldInnerState) -> Unit,
        onQrCodeScannerClick: () -> Unit,
        onAddressBookClick: () -> Unit
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
            mode = state.swapMode,
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
            ),
            addressBookButton = IconButtonState(
                icon = R.drawable.send_address_book,
                onClick = onAddressBookClick
            ),
            changeModeButton = IconButtonState(
                icon = R.drawable.ic_swap_change_mode,
                onClick = { onSwapModeChange(SwapMode.PAY) },
            ),
            appBarState = SwapAppBarState(
                title = stringRes("Swap with"),
                icon = R.drawable.ic_near_logo
            ),
            errorFooter = createErrorFooterState(state),
            primaryButton =
                createPrimaryButtonState(
                    textField = textFieldState,
                    state = state,
                    onRequestSwapQuoteClick = onRequestSwapQuoteClick,
                    onTryAgainClick = onTryAgainClick
                ),
        )
    }

    private fun createAmountTextFieldState(
        state: ExactInputInternalState,
        onSwapCurrencyTypeClick: (BigDecimal) -> Unit,
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
                AssetCardState.Data(
                    ticker = stringRes(cash.z.ecc.sdk.ext.R.string.zcash_token_zec),
                    bigIcon = imageRes(R.drawable.ic_zec_round_full),
                    smallIcon = imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_receive_shield),
                    onClick = null
                ),
            textFieldPrefix =
                when (state.currencyType) {
                    TOKEN -> imageRes(R.drawable.ic_send_zashi)
                    FIAT -> imageRes(R.drawable.ic_send_usd)
                },
            textField =
                NumberTextFieldState(
                    innerState = state.amountTextState,
                    onValueChange = onTextFieldChange,
                    isEnabled = !state.isRequestingQuote
                ),
            secondaryText =
                when (state.currencyType) {
                    TOKEN ->
                        stringResByDynamicCurrencyNumber(
                            amount = amountFiat ?: BigDecimal(0),
                            ticker = FiatCurrency.USD.symbol,
                        )

                    FIAT -> stringResByDynamicCurrencyNumber(
                        (zatoshiAmount ?: 0).convertZatoshiToZecBigDecimal(),
                        "ZEC"
                    )
                },
            max =
                state.totalSpendableBalance?.let {
                    stringRes("Max: ") + stringRes(it, TickerLocation.HIDDEN)
                },
            onSwapChange = {
                when (state.currencyType) {
                    TOKEN -> {
                        onSwapCurrencyTypeClick(amountFiat ?: BigDecimal(0))
                    }

                    FIAT -> {
                        onSwapCurrencyTypeClick((zatoshiAmount ?: 0).convertZatoshiToZecBigDecimal())
                    }
                }
            },
            isSwapChangeEnabled = !state.isRequestingQuote
        )
    }

    private fun createAmountTextState(
        state: ExactInputInternalState,
        onSwapAssetPickerClick: (() -> Unit)?
    ): SwapAmountTextState =
        SwapAmountTextState(
            token = if (state.swapAsset == null) {
                AssetCardState.Loading(
                    onClick = onSwapAssetPickerClick,
                    isEnabled = !state.isRequestingQuote
                )
            } else {
                AssetCardState.Data(
                    ticker = state.swapAsset.tokenTicker.let { stringRes(it) },
                    bigIcon = state.swapAsset.tokenIcon,
                    smallIcon = state.swapAsset.chainIcon,
                    onClick = onSwapAssetPickerClick,
                    isEnabled = !state.isRequestingQuote
                )
            },
            title = stringRes("To"),
            subtitle = null,
            text = stringResByDynamicNumber(state.getDestinationAssetAmount() ?: 0),
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
            text = stringResByNumber(amount, minDecimals = 0) + stringRes("%"),
            icon = R.drawable.ic_swap_slippage,
            onClick = { onSlippageClick(state.getOriginFiatAmount()) },
            isEnabled = !state.isRequestingQuote
        )
    }

    private fun createErrorFooterState(state: ExactInputInternalState): ErrorFooter? {
        if (state.swapAssets.error == null) return null

        return ErrorFooter(
            title = when (state.swapAssets.error) {
                UNEXPECTED_ERROR -> stringRes("Unexpected error")
                SERVICE_UNAVAILABLE -> stringRes("The service is unavailable")
            },
            subtitle = when(state.swapAssets.error) {
                UNEXPECTED_ERROR -> stringRes("Please check your connection and try again.")
                SERVICE_UNAVAILABLE -> stringRes("Please try again later.")
            }
        )
    }

    private fun createPrimaryButtonState(
        textField: SwapAmountTextFieldState,
        state: ExactInputInternalState,
        onRequestSwapQuoteClick: (BigDecimal, String) -> Unit,
        onTryAgainClick: () -> Unit
    ): ButtonState? {
        if (state.swapAssets.error == SERVICE_UNAVAILABLE) return null

        val amount = textField.textField.innerState.amount
        return ButtonState(
            text = when {
                state.swapAssets.error != null -> stringRes("Try again")
                state.swapAssets.isLoading && state.swapAssets.data == null -> stringRes("Loading")
                else -> stringRes("Confirm")
            },
            style = if (state.swapAssets.error != null) ButtonStyle.DESTRUCTIVE1 else null,
            onClick = {
                if (state.swapAssets.error != null) {
                    onTryAgainClick()
                } else {
                    state.getOriginTokenAmount()?.let { onRequestSwapQuoteClick(it, state.addressText) }
                }
            },
            isEnabled = if (state.swapAssets.error != null) {
                !state.swapAssets.isLoading
            } else {
                (!state.swapAssets.isLoading && state.swapAssets.data != null) &&
                    state.swapAsset != null &&
                    !textField.isError &&
                    amount != null &&
                    amount > BigDecimal(0) &&
                    state.addressText.isNotBlank() &&
                    !state.isRequestingQuote
            },
            isLoading = state.isRequestingQuote || (state.swapAssets.isLoading && state.swapAssets.data == null)
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
            listOf(
                SimpleListItemState(
                    title = stringRes("Rate"),
                    text = null
                )
            )
        } else {
            listOf(
                SimpleListItemState(
                    title = stringRes("Rate"),
                    text = stringRes("1 ZEC = ") +
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
    override val swapAssets: SwapAssetsData,
    override val swapMode: SwapMode,
    override val isRequestingQuote: Boolean,
) : InternalState {

    constructor(original: InternalState) : this(
        swapAsset = original.swapAsset,
        currencyType = original.currencyType,
        totalSpendableBalance = original.totalSpendableBalance,
        amountTextState = original.amountTextState,
        addressText = original.addressText,
        slippage = original.slippage,
        isAddressBookHintVisible = original.isAddressBookHintVisible,
        swapAssets = original.swapAssets,
        swapMode = original.swapMode,
        isRequestingQuote = original.isRequestingQuote,
    )

    fun getOriginFiatAmount(): BigDecimal? {
        return when (currencyType) {
            TOKEN -> {
                val tokenAmount = amountTextState.amount
                if (tokenAmount == null || swapAssets.zecAsset == null) {
                    null
                } else {
                    tokenAmount.multiply(swapAssets.zecAsset.usdPrice)
                }
            }

            FIAT -> amountTextState.amount
        }
    }

    fun getOriginTokenAmount(): BigDecimal? {
        val fiatAmount = amountTextState.amount
        return when (currencyType) {
            TOKEN -> fiatAmount
            FIAT ->
                if (fiatAmount == null || swapAssets.zecAsset == null) {
                    null
                } else {
                    fiatAmount.divide(swapAssets.zecAsset.usdPrice, MathContext.DECIMAL128)
                }
        }
    }

    fun getDestinationAssetAmount(): BigDecimal? {
        val amountToken = getOriginTokenAmount()
        return if (swapAssets.zecAsset?.usdPrice == null || swapAsset?.usdPrice == null || amountToken == null) {
            null
        } else {
            amountToken
                .multiply(swapAssets.zecAsset.usdPrice, MathContext.DECIMAL128)
                .divide(swapAsset.usdPrice, MathContext.DECIMAL128)
        }
    }

    fun getZecToDestinationAssetExchangeRate(): BigDecimal? {
        val zecUsdPrice = swapAssets.zecAsset?.usdPrice
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

