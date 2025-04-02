package co.electriccoin.zcash.ui.screen.seed

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.SeedTextState
import co.electriccoin.zcash.ui.design.util.StringResource

data class SeedRecoveryState(
    val seed: SeedTextState,
    val birthday: SeedSecretState,
    val info: IconButtonState,
    val button: ButtonState,
    val onBack: (() -> Unit)?
)

data class SeedSecretState(
    val title: StringResource,
    val text: StringResource,
    val isRevealed: Boolean,
    val tooltip: SeedSecretStateTooltip?,
    val onClick: (() -> Unit)?,
)

data class SeedSecretStateTooltip(
    val title: StringResource,
    val message: StringResource,
)
