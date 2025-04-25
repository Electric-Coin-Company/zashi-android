package co.electriccoin.zcash.ui.screen.error

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.util.StringResource

data class ErrorState(
    val title: StringResource,
    val message: StringResource,
    val positive: ButtonState,
    val negative: ButtonState,
    override val onBack: () -> Unit
) : ModalBottomSheetState
