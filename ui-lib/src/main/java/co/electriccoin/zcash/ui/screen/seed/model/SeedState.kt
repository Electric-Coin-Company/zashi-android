package co.electriccoin.zcash.ui.screen.seed.model

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.StringResource

data class SeedState(
    val seed: SeedSecretState,
    val birthday: SeedSecretState,
    val button: ButtonState,
    val onBack: (() -> Unit)?
)

data class SeedSecretState(
    val title: StringResource,
    val text: StringResource,
    val isRevealed: Boolean,
    val isRevealPhraseVisible: Boolean,
    val mode: Mode,
    val tooltip: SeedSecretStateTooltip?,
    val onClick: () -> Unit,
) {
    enum class Mode {
        SEED, BIRTHDAY
    }
}

data class SeedSecretStateTooltip(
    val title: StringResource,
    val message: StringResource,
)
