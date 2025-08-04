package co.electriccoin.zcash.ui.screen.swap

import cash.z.ecc.android.sdk.ext.Conversions.ZEC_FORMATTER
import cash.z.ecc.android.sdk.model.FiatCurrency
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.model.SwapMode
import co.electriccoin.zcash.ui.common.repository.EnhancedABContact
import co.electriccoin.zcash.ui.common.repository.SwapAssetsData
import co.electriccoin.zcash.ui.design.component.AssetCardState
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ButtonStyle
import co.electriccoin.zcash.ui.design.component.ChipButtonState
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
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

internal class ExactOutputVMMapper : SwapVMMapper {
    override fun createState(
        internalState: InternalState,
        onBack: () -> Unit,
        onSwapInfoClick: () -> Unit,
        onSwapAssetPickerClick: () -> Unit,
        onSwapCurrencyTypeClick: (BigDecimal?) -> Unit,
        onSlippageClick: (BigDecimal?) -> Unit,
        onRequestSwapQuoteClick: (BigDecimal, String) -> Unit,
        onTryAgainClick: () -> Unit,
        onAddressChange: (String) -> Unit,
        onSwapModeChange: (SwapMode) -> Unit,
        onTextFieldChange: (NumberTextFieldInnerState) -> Unit,
        onQrCodeScannerClick: () -> Unit,
        onAddressBookClick: () -> Unit,
        onDeleteSelectedContactClick: () -> Unit
    ): SwapState {
        val state = ExactOutputInternalState(internalState)
        val textFieldState =
            createAmountTextFieldState(
                state = state,
                onSwapAssetPickerClick = onSwapAssetPickerClick,
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
            amountText = createAmountTextState(state),
            mode = state.swapMode,
            addressContact =
                createAddressContactState(
                    state = state,
                    onDeleteSelectedContactClick = onDeleteSelectedContactClick
                ),
            address =
                createAddressState(
                    state = state,
                    onAddressChange = onAddressChange
                ),
            isAddressBookHintVisible = state.isAddressBookHintVisible,
            onBack = onBack,
            swapInfoButton = IconButtonState(
                icon = co.electriccoin.zcash.ui.design.R.drawable.ic_info,
                onClick = onSwapInfoClick
            ),
            infoItems = createListItems(state),
            qrScannerButton =
                IconButtonState(
                    icon = R.drawable.qr_code_icon,
                    onClick = onQrCodeScannerClick
                ),
            addressBookButton =
                IconButtonState(
                    icon = R.drawable.send_address_book,
                    onClick = onAddressBookClick
                ),
            changeModeButton =
                IconButtonState(
                    icon = R.drawable.ic_swap_change_mode,
                    onClick = { onSwapModeChange(SwapMode.SWAP) },
                ),
            appBarState =
                SwapAppBarState(
                    title = stringRes("Pay with"),
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

    private fun createAddressContactState(
        state: ExactOutputInternalState,
        onDeleteSelectedContactClick: () -> Unit
    ): ChipButtonState? {
        if (state.selectedContact == null) return null

        return ChipButtonState(
            text = stringRes(state.selectedContact.contact.name),
            onClick = onDeleteSelectedContactClick,
            endIcon = co.electriccoin.zcash.ui.design.R.drawable.ic_chip_close
        )
    }

    @Suppress("CyclomaticComplexMethod")
    private fun createAmountTextFieldState(
        state: ExactOutputInternalState,
        onSwapAssetPickerClick: () -> Unit,
        onSwapCurrencyTypeClick: (BigDecimal?) -> Unit,
        onTextFieldChange: (NumberTextFieldInnerState) -> Unit,
    ): SwapAmountTextFieldState {
        val amountFiat = state.getOriginFiatAmount()
        val zatoshiAmount = state.getZatoshi()
        return SwapAmountTextFieldState(
            title = stringRes("To"),
            error =
                if (state.totalSpendableBalance != null &&
                    zatoshiAmount != null &&
                    state.totalSpendableBalance.value < zatoshiAmount
                ) {
                    stringRes("Insufficient funds")
                } else {
                    null
                },
            token =
                if (state.swapAsset == null) {
                    AssetCardState.Loading(
                        onClick = onSwapAssetPickerClick,
                        isEnabled = !state.isRequestingQuote,
                    )
                } else {
                    AssetCardState.Data(
                        ticker = state.swapAsset.tokenTicker.let { stringRes(it) },
                        bigIcon = state.swapAsset.tokenIcon,
                        smallIcon = state.swapAsset.chainIcon,
                        onClick = onSwapAssetPickerClick,
                        isEnabled = !state.isRequestingQuote,
                    )
                },
            textFieldPrefix =
                when (state.currencyType) {
                    TOKEN -> null
                    FIAT -> imageRes(R.drawable.ic_send_usd)
                },
            textField =
                NumberTextFieldState(
                    innerState = state.amountTextState,
                    onValueChange = onTextFieldChange,
                    isEnabled = !state.isRequestingQuote,
                ),
            secondaryText =
                when (state.currencyType) {
                    TOKEN ->
                        stringResByDynamicCurrencyNumber(
                            amount = amountFiat ?: BigDecimal(0),
                            ticker = FiatCurrency.USD.symbol,
                        )

                    FIAT -> {
                        val tokenAmount =
                            if (amountFiat == null || state.swapAsset == null) {
                                BigDecimal.valueOf(0)
                            } else {
                                amountFiat.divide(state.swapAsset.usdPrice, MathContext.DECIMAL128)
                            }
                        stringResByDynamicCurrencyNumber(
                            amount =
                                tokenAmount.setScale(
                                    state.swapAsset?.decimals ?: ZEC_FORMATTER.maximumFractionDigits,
                                    RoundingMode.DOWN
                                ),
                            ticker = state.swapAsset?.tokenTicker.orEmpty(),
                        )
                    }
                },
            max = null,
            onSwapChange = {
                val newTextAmount =
                    when (state.currencyType) {
                        TOKEN -> amountFiat

                        FIAT -> {
                            if (amountFiat == null || state.swapAsset == null) {
                                null
                            } else {
                                amountFiat
                                    .divide(state.swapAsset.usdPrice, MathContext.DECIMAL128)
                                    .setScale(state.swapAsset.decimals, RoundingMode.DOWN)
                            }
                        }
                    }

                onSwapCurrencyTypeClick(newTextAmount.takeIf { it != BigDecimal.ZERO })
            },
            isSwapChangeEnabled = !state.isRequestingQuote,
        )
    }

    private fun createAmountTextState(state: ExactOutputInternalState): SwapAmountTextState {
        val fiatText =
            stringResByDynamicCurrencyNumber(
                amount = state.getOriginFiatAmount() ?: 0,
                ticker = FiatCurrency.USD.symbol
            )

        return SwapAmountTextState(
            token =
                AssetCardState.Data(
                    stringRes(cash.z.ecc.sdk.ext.R.string.zcash_token_zec),
                    bigIcon = imageRes(R.drawable.ic_zec_round_full),
                    smallIcon = imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_receive_shield),
                    onClick = null
                ),
            title = stringRes("From"),
            subtitle =
                when (state.currencyType) {
                    TOKEN ->
                        state.totalSpendableBalance?.let {
                            stringRes("Max: ") + stringRes(it, TickerLocation.HIDDEN)
                        }

                    FIAT ->
                        state.getTotalSpendableFiatBalance()?.let {
                            stringRes("Max: ") + stringResByDynamicCurrencyNumber(it, FiatCurrency.USD.symbol)
                        }
                },
            text =
                when (state.currencyType) {
                    TOKEN -> stringResByDynamicNumber(state.getZatoshi()?.convertZatoshiToZecBigDecimal() ?: 0)
                    FIAT -> fiatText
                },
            secondaryText =
                when (state.currencyType) {
                    TOKEN -> fiatText
                    FIAT ->
                        stringResByDynamicCurrencyNumber(
                            amount = state.getZatoshi()?.convertZatoshiToZecBigDecimal() ?: 0,
                            ticker = "ZEC",
                        )
                },
        )
    }

    private fun createSlippageState(
        state: ExactOutputInternalState,
        onSlippageClick: (BigDecimal?) -> Unit
    ): ButtonState {
        val amount = state.slippage
        return ButtonState(
            text = stringResByNumber(amount, minDecimals = 0) + stringRes("%"),
            icon = R.drawable.ic_swap_slippage,
            onClick = { onSlippageClick(state.getOriginFiatAmount()) },
            isEnabled = !state.isRequestingQuote,
        )
    }

    private fun createErrorFooterState(state: ExactOutputInternalState): ErrorFooter? {
        if (state.swapAssets.error == null) return null

        val isServiceUnavailableError =
            state.swapAssets.error is ResponseException &&
                state.swapAssets.error.response.status == HttpStatusCode.ServiceUnavailable

        return ErrorFooter(
            title =
                if (isServiceUnavailableError) {
                    stringRes("The service is unavailable")
                } else {
                    stringRes("Unexpected error")
                },
            subtitle =
                if (isServiceUnavailableError) {
                    stringRes("Please try again later.")
                } else {
                    stringRes("Please check your connection and try again.")
                }
        )
    }

    @Suppress("CyclomaticComplexMethod")
    private fun createPrimaryButtonState(
        textField: SwapAmountTextFieldState,
        state: ExactOutputInternalState,
        onRequestSwapQuoteClick: (BigDecimal, String) -> Unit,
        onTryAgainClick: () -> Unit
    ): ButtonState? {
        if (state.swapAssets.error is ResponseException &&
            state.swapAssets.error.response.status == HttpStatusCode.ServiceUnavailable
        ) {
            return null
        }

        val amount = textField.textField.innerState.amount
        return ButtonState(
            text =
                when {
                    state.swapAssets.error != null -> stringRes("Try again")
                    state.swapAssets.isLoading && state.swapAssets.data == null -> stringRes("Loading")
                    else -> stringRes("Confirm")
                },
            style = if (state.swapAssets.error != null) ButtonStyle.DESTRUCTIVE1 else null,
            onClick = {
                if (state.swapAssets.error != null) {
                    onTryAgainClick()
                } else {
                    val address = state.selectedContact?.address ?: state.addressText
                    state.getOriginTokenAmount()?.let { onRequestSwapQuoteClick(it, address) }
                }
            },
            isEnabled =
                if (state.swapAssets.error != null) {
                    !state.swapAssets.isLoading
                } else {
                    (!state.swapAssets.isLoading && state.swapAssets.data != null) &&
                        state.swapAsset != null &&
                        !textField.isError &&
                        amount != null &&
                        amount > BigDecimal(0) &&
                        (state.addressText.isNotBlank() || state.selectedContact != null) &&
                        !state.isRequestingQuote
                },
            isLoading = state.isRequestingQuote || (state.swapAssets.isLoading && state.swapAssets.data == null)
        )
    }

    private fun createAddressState(
        state: ExactOutputInternalState,
        onAddressChange: (String) -> Unit
    ): TextFieldState {
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
            isEnabled = !state.isRequestingQuote,
        )
    }

    private fun createListItems(state: ExactOutputInternalState): List<SimpleListItemState> {
        val zecToAssetExchangeRate = state.getZecToOriginAssetExchangeRate()
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
                    text =
                        stringRes("1 ZEC = ") +
                            stringResByDynamicCurrencyNumber(zecToAssetExchangeRate, assetTokenTicker)
                )
            )
        }
    }
}

private data class ExactOutputInternalState(
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
    override val selectedContact: EnhancedABContact?
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
        selectedContact = original.selectedContact
    )

    fun getTotalSpendableFiatBalance(): BigDecimal? {
        if (totalSpendableBalance == null || swapAssets.zecAsset?.usdPrice == null) return null
        return totalSpendableBalance.value
            .convertZatoshiToZecBigDecimal()
            .multiply(swapAssets.zecAsset.usdPrice, MathContext.DECIMAL128)
    }

    fun getOriginFiatAmount(): BigDecimal? =
        when (currencyType) {
            TOKEN -> {
                val tokenAmount = amountTextState.amount
                if (tokenAmount == null || swapAsset == null) {
                    null
                } else {
                    tokenAmount.multiply(swapAsset.usdPrice, MathContext.DECIMAL128)
                }
            }

            FIAT -> amountTextState.amount
        }

    fun getOriginTokenAmount(): BigDecimal? {
        val fiatAmount = amountTextState.amount
        return when (currencyType) {
            TOKEN -> fiatAmount
            FIAT ->
                if (fiatAmount == null || swapAsset == null) {
                    null
                } else {
                    fiatAmount.divide(swapAsset.usdPrice, MathContext.DECIMAL128)
                }
        }
    }

    fun getZecToOriginAssetExchangeRate(): BigDecimal? {
        val zecUsdPrice = swapAssets.zecAsset?.usdPrice
        val assetUsdPrice = swapAsset?.usdPrice
        if (zecUsdPrice == null || assetUsdPrice == null) return null
        return zecUsdPrice.divide(assetUsdPrice, MathContext.DECIMAL128)
    }

    fun getZatoshi(): Long? {
        val amountToken = getOriginTokenAmount()
        return if (swapAssets.zecAsset?.usdPrice == null || swapAsset?.usdPrice == null || amountToken == null) {
            null
        } else {
            amountToken
                .multiply(swapAsset.usdPrice, MathContext.DECIMAL128)
                .divide(swapAssets.zecAsset.usdPrice, MathContext.DECIMAL128)
                .convertZecToZatoshiLong()
        }
    }
}
