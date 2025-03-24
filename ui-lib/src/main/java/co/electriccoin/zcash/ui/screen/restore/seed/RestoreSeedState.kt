package co.electriccoin.zcash.ui.screen.restore.seed

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.SeedTextFieldState

data class RestoreSeedState(
    val seed: SeedTextFieldState,
    val onBack: () -> Unit,
    val dialogButton: IconButtonState,
    val nextButton: ButtonState
)

data class RestoreSeedSuggestionsState(
    val isVisible: Boolean,
    val suggestions: List<String>,
)
