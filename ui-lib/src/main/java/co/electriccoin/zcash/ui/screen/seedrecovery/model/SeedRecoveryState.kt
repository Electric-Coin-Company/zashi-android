package co.electriccoin.zcash.ui.screen.seedrecovery.model

import co.electriccoin.zcash.ui.design.component.ButtonState

data class SeedRecoveryState(
    val isRevealed: Boolean,
    val seed: SeedSecret,
    val birthday: SeedSecret,
    val revealButton: ButtonState
)

data class SeedSecret(
    val text: String,
    val onClick: () -> Unit,
)