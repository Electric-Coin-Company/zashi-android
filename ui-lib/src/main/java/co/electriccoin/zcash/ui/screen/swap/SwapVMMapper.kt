package co.electriccoin.zcash.ui.screen.swap

import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.repository.SwapMode
import co.electriccoin.zcash.ui.design.component.NumberTextFieldInnerState
import java.math.BigDecimal

internal interface SwapVMMapper {
    fun createState(
        internalState: InternalState,
        onBack: () -> Unit,
        onSwapInfoClick: () -> Unit,
        onSwapAssetPickerClick: () -> Unit,
        onSwapCurrencyTypeClick: () -> Unit,
        onSlippageClick: (BigDecimal?) -> Unit,
        onRequestSwapQuoteClick: (BigDecimal, String) -> Unit,
        onAddressChange: (String) -> Unit,
        onSwapModeChange: (SwapMode) -> Unit,
        onTextFieldChange: (NumberTextFieldInnerState) -> Unit
    ): SwapState
}

internal interface InternalState{
    val swapAsset: SwapAsset?
    val currencyType: CurrencyType
    val totalSpendableBalance: Zatoshi?
    val totalSpendableFiatBalance: BigDecimal?
    val amountTextState: NumberTextFieldInnerState
    val addressText: String
    val slippage: BigDecimal
    val isAddressBookHintVisible: Boolean
    val zecSwapAsset: SwapAsset?
    val swapMode: SwapMode
    val isRequestingQuote: Boolean
}

internal data class InternalStateImpl(
    override val swapAsset: SwapAsset?,
    override val currencyType: CurrencyType,
    override val totalSpendableBalance: Zatoshi?,
    override val totalSpendableFiatBalance: BigDecimal?,
    override val amountTextState: NumberTextFieldInnerState,
    override val addressText: String,
    override val slippage: BigDecimal,
    override val isAddressBookHintVisible: Boolean,
    override val zecSwapAsset: SwapAsset?,
    override val swapMode: SwapMode,
    override val isRequestingQuote: Boolean,
): InternalState
