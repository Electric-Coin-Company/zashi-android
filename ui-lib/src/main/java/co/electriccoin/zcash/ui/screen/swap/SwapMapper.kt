package co.electriccoin.zcash.ui.screen.swap

import cash.z.ecc.android.sdk.ext.convertZatoshiToZec
import cash.z.ecc.android.sdk.model.FiatCurrency
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.repository.SwapMode
import co.electriccoin.zcash.ui.common.repository.SwapMode.PAY
import co.electriccoin.zcash.ui.common.repository.SwapMode.SWAP
import co.electriccoin.zcash.ui.design.component.AssetCardState
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.CurrencySymbolLocation
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber
import co.electriccoin.zcash.ui.design.util.stringResByNumber
import java.math.BigDecimal
import java.math.MathContext

internal class SwapMapper {

    fun createState(
        internalState: InternalState,
        onBack: () -> Unit,
        onSwapInfoClick: () -> Unit,
        onSwapAssetPickerClick: () -> Unit,
        onSwapCurrencyTypeClick: () -> Unit,
        onSlippageClick: () -> Unit,
        onPrimaryClick: () -> Unit,
        onAddressChange: (String) -> Unit,
        onSwapModeChange: (SwapMode) -> Unit
    ): SwapState {
        val textFieldState = createAmountTextFieldState(
            internalState = internalState,
            onSwapCurrencyTypeClick = onSwapCurrencyTypeClick,
        )
        return SwapState(
            amountTextField = textFieldState,
            slippage = createSlippageState(
                internalState = internalState,
                onSlippageClick = onSlippageClick
            ),
            amountText = createAmountTextState(
                internalState = internalState,
                onSwapAssetPickerClick = onSwapAssetPickerClick
            ),
            primaryButton = createPrimaryButtonState(
                textField = textFieldState,
                internalState = internalState,
                onPrimaryClick = onPrimaryClick
            ),
            swapModeSelectorState = SwapModeSelectorState(
                swapMode = SWAP,
                onClick = onSwapModeChange
            ),
            address = createAddressState(
                internalState = internalState,
                onAddressChange = onAddressChange
            ),
            isAddressBookHintVisible = internalState.isAddressBookHintVisible,
            onBack = onBack,
            swapInfoButton = IconButtonState(R.drawable.ic_help, onClick = onSwapInfoClick),
            infoItems = emptyList()
        )
    }

    @Suppress("CyclomaticComplexMethod")
    private fun createAmountTextFieldState(
        internalState: InternalState,
        onSwapCurrencyTypeClick: () -> Unit,
    ): SwapAmountTextFieldState {
        val amountFiat = internalState.getAmountFiat()
        val totalSpendableFiat = internalState.totalSpendableFiatBalance
        val zatoshiAmount = internalState.getZatoshiAmount()
        return SwapAmountTextFieldState(
            title = stringRes("From"),
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
            token = AssetCardState(
                ticker = stringRes(cash.z.ecc.sdk.ext.R.string.zcash_token_zec),
                bigIcon = imageRes(R.drawable.ic_zec_round_full),
                smallIcon = imageRes(R.drawable.ic_receive_shield),
                onClick = null
            ),
            textFieldPrefix =
                when (internalState.currencyType) {
                    CurrencyType.TOKEN -> null
                    CurrencyType.FIAT -> imageRes(R.drawable.ic_send_usd)
                },
            textField = internalState.amountTextState,
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
            max = internalState.totalSpendableBalance?.let {
                stringRes("Max: ") + stringRes(it, CurrencySymbolLocation.HIDDEN)
            },
            onSwapChange = onSwapCurrencyTypeClick
        )
    }

    private fun createAmountTextState(
        internalState: InternalState,
        onSwapAssetPickerClick: (() -> Unit)?
    ): SwapAmountTextState {
        return SwapAmountTextState(
            token = AssetCardState(
                ticker = internalState.swapAsset?.tokenTicker?.let { stringRes(it) } ?: stringRes("Select token"),
                bigIcon = internalState.swapAsset?.tokenIcon,
                smallIcon = internalState.swapAsset?.chainIcon,
                onClick = onSwapAssetPickerClick
            ),
            title = stringRes("To"),
            subtitle = null,
            text = stringResByDynamicCurrencyNumber(
                amount = internalState.getTargetAssetAmount() ?: 0,
                ticker = internalState.swapAsset?.tokenTicker.orEmpty(),
                symbolLocation = CurrencySymbolLocation.HIDDEN
            ),
            secondaryText = stringResByDynamicCurrencyNumber(
                amount = internalState.getAmountFiat() ?: 0,
                ticker = FiatCurrency.USD.symbol
            ),
        )
    }

    @Suppress("MagicNumber")
    private fun createSlippageState(
        internalState: InternalState,
        onSlippageClick: () -> Unit
    ): ButtonState {
        val amount = BigDecimal.valueOf(internalState.slippage.toDouble()) / BigDecimal(10.0)
        return ButtonState(
            text = stringResByNumber(amount) + stringRes("%"),
            icon = R.drawable.ic_swap_slippage,
            onClick = onSlippageClick
        )
    }

    private fun createPrimaryButtonState(
        textField: SwapAmountTextFieldState,
        internalState: InternalState,
        onPrimaryClick: () -> Unit
    ): ButtonState {
        val amount = textField.textField.amount
        return ButtonState(
            text = stringRes("Confirm"),
            onClick = onPrimaryClick,
            isEnabled = !textField.isError &&
                amount != null &&
                amount > BigDecimal(0) &&
                internalState.addressText.isNotBlank()
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
            onValueChange = onAddressChange
        )
    }
}