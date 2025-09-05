package co.electriccoin.zcash.ui.screen.swap

import cash.z.ecc.android.sdk.ext.Conversions
import cash.z.ecc.android.sdk.ext.Conversions.ZEC_FORMATTER
import cash.z.ecc.android.sdk.model.FiatCurrency
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.model.WalletAccount
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
import kotlin.math.absoluteValue

internal class ExactInputVMMapper {
    fun createState(
        internalState: InternalState,
        onBack: () -> Unit,
        onSwapInfoClick: () -> Unit,
        onSwapAssetPickerClick: () -> Unit,
        onSwapCurrencyTypeClick: (BigDecimal?) -> Unit,
        onSlippageClick: (BigDecimal?) -> Unit,
        onRequestSwapQuoteClick: (BigDecimal, String) -> Unit,
        onTryAgainClick: () -> Unit,
        onAddressChange: (String) -> Unit,
        onTextFieldChange: (NumberTextFieldInnerState) -> Unit,
        onQrCodeScannerClick: () -> Unit,
        onAddressBookClick: () -> Unit,
        onDeleteSelectedContactClick: () -> Unit,
        onBalanceButtonClick: () -> Unit
    ): SwapState {
        val state = ExactInputInternalState(internalState)
        val textFieldState =
            createAmountTextFieldState(
                state = state,
                onSwapCurrencyTypeClick = onSwapCurrencyTypeClick,
                onTextFieldChange = onTextFieldChange,
                onBalanceButtonClick = onBalanceButtonClick
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
            onBack = onBack,
            swapInfoButton =
                IconButtonState(
                    co.electriccoin.zcash.ui.design.R.drawable.ic_info,
                    onClick = onSwapInfoClick
                ),
            infoItems = createListItems(state),
            qrScannerButton =
                IconButtonState(
                    icon = R.drawable.qr_code_icon,
                    onClick = onQrCodeScannerClick,
                    isEnabled = !state.isRequestingQuote
                ),
            addressBookButton =
                IconButtonState(
                    icon = R.drawable.send_address_book,
                    onClick = onAddressBookClick,
                    isEnabled = !state.isRequestingQuote
                ),
            appBarState =
                SwapAppBarState(
                    title = stringRes(R.string.swap_title),
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
        state: ExactInputInternalState,
        onDeleteSelectedContactClick: () -> Unit
    ): ChipButtonState? {
        if (state.selectedContact == null) return null

        return ChipButtonState(
            text = stringRes(state.selectedContact.contact.name),
            onClick = onDeleteSelectedContactClick,
            endIcon = co.electriccoin.zcash.ui.design.R.drawable.ic_chip_close,
            isEnabled = !state.isRequestingQuote
        )
    }

    @Suppress("CyclomaticComplexMethod")
    private fun createAmountTextFieldState(
        state: ExactInputInternalState,
        onSwapCurrencyTypeClick: (BigDecimal?) -> Unit,
        onTextFieldChange: (NumberTextFieldInnerState) -> Unit,
        onBalanceButtonClick: () -> Unit
    ): SwapAmountTextFieldState {
        val amountFiat = state.getOriginFiatAmount()
        val zatoshiAmount = state.getZatoshi()
        return SwapAmountTextFieldState(
            title = stringRes(R.string.swap_from),
            error =
                if (zatoshiAmount != null &&
                    state.totalSpendableBalance < zatoshiAmount
                ) {
                    stringRes(R.string.swap_insufficient_funds)
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

                    FIAT ->
                        stringResByDynamicCurrencyNumber(
                            (zatoshiAmount ?: Zatoshi(0)).value.convertZatoshiToZecBigDecimal(),
                            "ZEC"
                        )
                },
            max = createMaxState(state, onBalanceButtonClick),
            onSwapChange = {
                when (state.currencyType) {
                    TOKEN -> onSwapCurrencyTypeClick(amountFiat.takeIf { it != BigDecimal.ZERO })
                    FIAT -> {
                        if (zatoshiAmount == null) {
                            onSwapCurrencyTypeClick(null)
                        } else {
                            onSwapCurrencyTypeClick(zatoshiAmount.value.convertZatoshiToZecBigDecimal())
                        }
                    }
                }
            },
            isSwapChangeEnabled = !state.isRequestingQuote
        )
    }

    private fun createMaxState(
        state: ExactInputInternalState,
        onBalanceButtonClick: () -> Unit
    ): ButtonState {
        val account = state.account ?: return ButtonState(
            text = stringRes(R.string.swap_max_standalone),
            isLoading = true,
            onClick = onBalanceButtonClick
        )

        return when {
            account.totalBalance > account.spendableShieldedBalance &&
                account.isShieldedPending &&
                account.totalShieldedBalance > Zatoshi(0) &&
                account.spendableShieldedBalance == Zatoshi(0) ->
                ButtonState(
                    text = stringRes(R.string.swap_max_standalone),
                    isLoading = true,
                    onClick = onBalanceButtonClick
                )

            account.totalBalance > account.spendableShieldedBalance &&
                !account.isShieldedPending &&
                account.totalShieldedBalance > Zatoshi(0) &&
                account.spendableShieldedBalance == Zatoshi(0) &&
                account.totalTransparentBalance == Zatoshi(0) ->
                ButtonState(
                    text = stringRes(R.string.swap_max_standalone),
                    isLoading = true,
                    onClick = onBalanceButtonClick
                )

            else -> {
                val amount =
                    when (state.currencyType) {
                        TOKEN -> stringRes(state.totalSpendableBalance, TickerLocation.HIDDEN)

                        FIAT ->
                            stringResByDynamicCurrencyNumber(
                                state.getTotalSpendableFiatBalance(),
                                FiatCurrency.USD.symbol
                            )
                    }

                ButtonState(
                    text = stringRes(R.string.swap_max, amount),
                    // amount = account.spendableShieldedBalance,
                    isLoading = false,
                    onClick = onBalanceButtonClick
                )
            }
        }
    }

    private fun createAmountTextState(
        state: ExactInputInternalState,
        onSwapAssetPickerClick: (() -> Unit)?
    ): SwapAmountTextState {
        val fiatText =
            stringResByDynamicCurrencyNumber(
                amount = state.getOriginFiatAmount() ?: 0,
                ticker = FiatCurrency.USD.symbol
            )

        return SwapAmountTextState(
            token =
                if (state.swapAsset == null) {
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
            title = stringRes(R.string.swap_to),
            subtitle = null,
            text =
                when (state.currencyType) {
                    TOKEN -> stringResByDynamicNumber(state.getDestinationAssetAmount() ?: 0)
                    FIAT -> fiatText
                },
            secondaryText =
                when (state.currencyType) {
                    TOKEN -> fiatText
                    FIAT -> {
                        if (state.swapAsset?.tokenTicker == null) {
                            stringResByDynamicNumber(state.getDestinationAssetAmount() ?: 0)
                        } else {
                            stringResByDynamicCurrencyNumber(
                                state.getDestinationAssetAmount() ?: 0,
                                state.swapAsset.tokenTicker
                            )
                        }
                    }
                }
        )
    }

    private fun createSlippageState(
        state: ExactInputInternalState,
        onSlippageClick: (BigDecimal?) -> Unit
    ): ButtonState {
        val amount = state.slippage
        return ButtonState(
            text = stringResByNumber(amount, minDecimals = 0) + stringRes("%"),
            trailingIcon = R.drawable.ic_swap_slippage,
            onClick = { onSlippageClick(state.getOriginFiatAmount()) },
            isEnabled = !state.isRequestingQuote
        )
    }

    private fun createErrorFooterState(state: ExactInputInternalState): SwapErrorFooterState? {
        if (state.swapAssets.error == null) return null

        val isServiceUnavailableError =
            state.swapAssets.error is ResponseException &&
                state.swapAssets.error.response.status == HttpStatusCode.ServiceUnavailable

        return SwapErrorFooterState(
            title =
                if (isServiceUnavailableError) {
                    stringRes(co.electriccoin.zcash.ui.design.R.string.general_service_unavailable)
                } else {
                    stringRes(co.electriccoin.zcash.ui.design.R.string.general_unexpected_error)
                },
            subtitle =
                if (isServiceUnavailableError) {
                    stringRes(co.electriccoin.zcash.ui.design.R.string.general_please_try_again)
                } else {
                    stringRes(co.electriccoin.zcash.ui.design.R.string.general_check_connection)
                }
        )
    }

    @Suppress("CyclomaticComplexMethod")
    private fun createPrimaryButtonState(
        textField: SwapAmountTextFieldState,
        state: ExactInputInternalState,
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
                    state.swapAssets.error != null ->
                        stringRes(co.electriccoin.zcash.ui.design.R.string.general_try_again)

                    state.swapAssets.isLoading && state.swapAssets.data == null ->
                        stringRes(co.electriccoin.zcash.ui.design.R.string.general_loading)

                    else -> stringRes(R.string.swap_confirm)
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
                    title = stringRes(R.string.swap_rate),
                    text = null
                )
            )
        } else {
            listOf(
                SimpleListItemState(
                    title = stringRes(R.string.swap_rate),
                    text =
                        stringRes(
                            R.string.swap_zec_exchange_rate,
                            stringResByDynamicCurrencyNumber(zecToAssetExchangeRate, assetTokenTicker)
                        )
                )
            )
        }
    }
}

private data class ExactInputInternalState(
    override val account: WalletAccount?,
    override val swapAsset: SwapAsset?,
    override val currencyType: CurrencyType,
    override val amountTextState: NumberTextFieldInnerState,
    override val addressText: String,
    override val slippage: BigDecimal,
    override val swapAssets: SwapAssetsData,
    override val isRequestingQuote: Boolean,
    override val selectedContact: EnhancedABContact?,
) : InternalState {
    constructor(original: InternalState) : this(
        account = original.account,
        swapAsset = original.swapAsset,
        currencyType = original.currencyType,
        amountTextState = original.amountTextState,
        addressText = original.addressText,
        slippage = original.slippage,
        swapAssets = original.swapAssets,
        isRequestingQuote = original.isRequestingQuote,
        selectedContact = original.selectedContact
    )

    fun getTotalSpendableFiatBalance(): BigDecimal {
        if (swapAssets.zecAsset?.usdPrice == null) return BigDecimal(0)
        return totalSpendableBalance.value
            .convertZatoshiToZecBigDecimal()
            .multiply(swapAssets.zecAsset.usdPrice, MathContext.DECIMAL128)
    }

    fun getOriginFiatAmount(): BigDecimal? =
        when (currencyType) {
            TOKEN -> {
                val tokenAmount = amountTextState.amount
                if (tokenAmount == null || swapAssets.zecAsset == null) {
                    null
                } else {
                    tokenAmount.multiply(swapAssets.zecAsset.usdPrice, MathContext.DECIMAL128)
                }
            }

            FIAT -> amountTextState.amount
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

    fun getZatoshi() = getOriginTokenAmount()?.convertZecToZatoshi()
}

@Suppress("MagicNumber")
internal fun BigDecimal.convertZecToZatoshi(): Zatoshi =
    Zatoshi(
        this
            .coerceIn(BigDecimal(0), BigDecimal(21_000_000))
            .multiply(Conversions.ONE_ZEC_IN_ZATOSHI, MathContext.DECIMAL128)
            .toLong()
            .absoluteValue
    )

internal fun Long.convertZatoshiToZecBigDecimal(scale: Int = ZEC_FORMATTER.maximumFractionDigits): BigDecimal =
    BigDecimal(this, MathContext.DECIMAL128)
        .divide(
            Conversions.ONE_ZEC_IN_ZATOSHI,
            MathContext.DECIMAL128
        ).setScale(scale, ZEC_FORMATTER.roundingMode)
