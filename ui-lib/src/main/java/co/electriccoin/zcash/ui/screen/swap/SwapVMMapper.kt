package co.electriccoin.zcash.ui.screen.swap

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
        onPrimaryClick: () -> Unit,
        onAddressChange: (String) -> Unit,
        onSwapModeChange: (SwapMode) -> Unit,
        onTextFieldChange: (NumberTextFieldInnerState) -> Unit
    ): SwapState
}
