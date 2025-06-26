package co.electriccoin.zcash.ui.screen.swap

import cash.z.ecc.android.sdk.ext.convertZatoshiToZec
import cash.z.ecc.android.sdk.model.FiatCurrency
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.repository.SwapMode
import co.electriccoin.zcash.ui.common.repository.SwapMode.PAY
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
import java.math.BigDecimal
import java.math.MathContext

internal class NearPayMapper : SwapVMMapper {
    override fun createState(
        internalState: InternalState,
        onBack: () -> Unit,
        onSwapInfoClick: () -> Unit,
        onSwapAssetPickerClick: () -> Unit,
        onSwapCurrencyTypeClick: () -> Unit,
        onSlippageClick: (BigDecimal?) -> Unit,
        onPrimaryClick: (BigDecimal, String) -> Unit,
        onAddressChange: (String) -> Unit,
        onSwapModeChange: (SwapMode) -> Unit,
        onTextFieldChange: (NumberTextFieldInnerState) -> Unit
    ): SwapState {
        val textFieldState =
            createAmountTextFieldState(
                internalState = internalState,
                onSwapAssetPickerClick = onSwapAssetPickerClick,
                onSwapCurrencyTypeClick = onSwapCurrencyTypeClick,
                onTextFieldChange = onTextFieldChange
            )
        return SwapState(
            amountTextField = textFieldState,
            slippage =
                createSlippageState(
                    internalState = internalState,
                    onSlippageClick = onSlippageClick
                ),
            amountText = createAmountTextState(internalState),
            primaryButton =
                createPrimaryButtonState(
                    textField = textFieldState,
                    internalState = internalState,
                    onPrimaryClick = onPrimaryClick
                ),
            swapModeSelectorState =
                SwapModeSelectorState(
                    swapMode = PAY,
                    onClick = onSwapModeChange,
                    isEnabled = !internalState.isRequestingQuote,
                ),
            address =
                createAddressState(
                    internalState = internalState,
                    onAddressChange = onAddressChange
                ),
            isAddressBookHintVisible = internalState.isAddressBookHintVisible,
            onBack = onBack,
            swapInfoButton = IconButtonState(R.drawable.ic_help, onClick = onSwapInfoClick),
            infoItems = createListItems(internalState)
        )
    }

    @Suppress("CyclomaticComplexMethod")
    private fun createAmountTextFieldState(
        internalState: InternalState,
        onSwapAssetPickerClick: () -> Unit,
        onSwapCurrencyTypeClick: () -> Unit,
        onTextFieldChange: (NumberTextFieldInnerState) -> Unit,
    ): SwapAmountTextFieldState {
        val amountFiat = internalState.getAmountFiat()
        val totalSpendableFiat = internalState.totalSpendableFiatBalance
        val zatoshiAmount = internalState.getZatoshiAmount()
        return SwapAmountTextFieldState(
            title = stringRes("To"),
            error =
                when (internalState.currencyType) {
                    CurrencyType.TOKEN -> {
                        if (internalState.totalSpendableBalance != null &&
                            zatoshiAmount != null &&
                            internalState.totalSpendableBalance < zatoshiAmount
                        ) {
                            stringRes("Insufficient funds")
                        } else {
                            null
                        }
                    }

                    CurrencyType.FIAT -> {
                        if (totalSpendableFiat != null &&
                            (amountFiat ?: BigDecimal(0)) > totalSpendableFiat
                        ) {
                            stringRes("Insufficient funds")
                        } else {
                            null
                        }
                    }
                },
            token =
                AssetCardState(
                    ticker = internalState.swapAsset?.tokenTicker?.let { stringRes(it) } ?: stringRes("Select token"),
                    bigIcon = internalState.swapAsset?.tokenIcon,
                    smallIcon = internalState.swapAsset?.chainIcon,
                    onClick = onSwapAssetPickerClick,
                    isEnabled = !internalState.isRequestingQuote,
                ),
            textFieldPrefix =
                when (internalState.currencyType) {
                    CurrencyType.TOKEN -> null
                    CurrencyType.FIAT -> imageRes(R.drawable.ic_send_usd)
                },
            textField =
                NumberTextFieldState(
                    innerState = internalState.amountTextState,
                    onValueChange = onTextFieldChange,
                    isEnabled = !internalState.isRequestingQuote,
                ),
            secondaryText =
                when (internalState.currencyType) {
                    CurrencyType.TOKEN ->
                        stringResByDynamicCurrencyNumber(
                            amount = amountFiat ?: BigDecimal(0),
                            ticker = FiatCurrency.USD.symbol,
                            maxDecimals = 2
                        )

                    CurrencyType.FIAT -> {
                        val tokenAmount =
                            if (amountFiat == null || internalState.swapAsset == null) {
                                BigDecimal.valueOf(0)
                            } else {
                                amountFiat.divide(
                                    internalState.swapAsset.usdPrice,
                                    MathContext.DECIMAL128
                                )
                            }
                        stringResByDynamicCurrencyNumber(tokenAmount, internalState.swapAsset?.tokenTicker.orEmpty())
                    }
                },
            max = null,
            onSwapChange = onSwapCurrencyTypeClick,
            isSwapChangeEnabled = !internalState.isRequestingQuote,
        )
    }

    private fun createAmountTextState(internalState: InternalState): SwapAmountTextState =
        SwapAmountTextState(
            token =
                AssetCardState(
                    stringRes(cash.z.ecc.sdk.ext.R.string.zcash_token_zec),
                    bigIcon = imageRes(R.drawable.ic_zec_round_full),
                    smallIcon = imageRes(R.drawable.ic_receive_shield),
                    onClick = null
                ),
            title = stringRes("From"),
            subtitle =
                internalState.totalSpendableBalance?.let {
                    stringRes("Max: ") + stringRes(it, CurrencySymbolLocation.HIDDEN)
                },
            text =
                internalState.getZatoshiAmount()?.let { stringRes(it, CurrencySymbolLocation.HIDDEN) }
                    ?: stringRes("0"),
            secondaryText =
                stringResByDynamicCurrencyNumber(
                    amount = internalState.getAmountFiat() ?: 0,
                    ticker = FiatCurrency.USD.symbol
                ),
        )

    @Suppress("MagicNumber")
    private fun createSlippageState(
        internalState: InternalState,
        onSlippageClick: (BigDecimal?) -> Unit
    ): ButtonState {
        val amount = internalState.slippage
        return ButtonState(
            text = stringResByNumber(amount) + stringRes("%"),
            icon = R.drawable.ic_swap_slippage,
            onClick = { onSlippageClick(internalState.getAmountFiat()) },
            isEnabled = !internalState.isRequestingQuote,
        )
    }

    private fun createPrimaryButtonState(
        textField: SwapAmountTextFieldState,
        internalState: InternalState,
        onPrimaryClick: (BigDecimal, String) -> Unit
    ): ButtonState {
        val amount = textField.textField.innerState.amount
        return ButtonState(
            text = stringRes("Confirm"),
            onClick = {
                internalState.getAmountToken()?.let {
                    onPrimaryClick(it, internalState.addressText)
                }
            },
            isEnabled =
                internalState.swapAsset != null &&
                    !textField.isError &&
                    amount != null &&
                    amount > BigDecimal(0) &&
                    internalState.addressText.isNotBlank() &&
                    !internalState.isRequestingQuote,
            isLoading = internalState.isRequestingQuote
        )
    }

    private fun createAddressState(internalState: InternalState, onAddressChange: (String) -> Unit): TextFieldState {
        val text = internalState.addressText

        return TextFieldState(
            error =
                when {
                    text.isEmpty() -> null
                    text.isBlank() -> stringRes("")
                    else -> null
                },
            value = stringRes(text),
            onValueChange = onAddressChange,
            isEnabled = !internalState.isRequestingQuote,
        )
    }

    private fun createListItems(internalState: InternalState): List<SimpleListItemState> {
        val zecToAssetExchangeRate = internalState.getZecToAssetExchangeRate()
        val assetTokenTicker = internalState.swapAsset?.tokenTicker
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
