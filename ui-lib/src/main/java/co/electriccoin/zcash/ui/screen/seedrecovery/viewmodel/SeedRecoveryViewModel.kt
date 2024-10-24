package co.electriccoin.zcash.ui.screen.seedrecovery.viewmodel

import androidx.lifecycle.ViewModel
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.ObservePersistableWalletUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.seedrecovery.model.SeedRecoveryState
import co.electriccoin.zcash.ui.screen.seedrecovery.model.SeedSecret
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class SeedRecoveryViewModel(
    observePersistableWallet: ObservePersistableWalletUseCase
) : ViewModel() {

    private val isRevealed = MutableStateFlow(false)

    val state = combine(isRevealed, observePersistableWallet()) { isRevealed, wallet ->
        SeedRecoveryState(
            revealButton = ButtonState(
                text = stringRes(R.string.seed_recovery_reveal_button),
                onClick = ::onRevealOrHideClicked,
                isEnabled = wallet != null,
                isLoading = wallet == null
            ),
            isRevealed = isRevealed,
            seed = SeedSecret(
                wallet?.seedPhrase?.joinToString().orEmpty(),
                ::onSeedCopyClicked
            ),
            birthday = SeedSecret(
                wallet?.birthday?.value?.toString().orEmpty(),
                ::onBirthdayCopyClicked
            )
        )
    }

    private fun onRevealOrHideClicked() {
    }

    private fun onSeedCopyClicked() {
    }

    private fun onBirthdayCopyClicked() {
    }
}