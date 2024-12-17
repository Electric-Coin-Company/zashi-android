package co.electriccoin.zcash.ui.screen.seed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.spackle.AndroidApiVersion
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.OnboardingState
import co.electriccoin.zcash.ui.common.repository.WalletRepository
import co.electriccoin.zcash.ui.common.usecase.ObservePersistableWalletUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.seed.SeedNavigationArgs
import co.electriccoin.zcash.ui.screen.seed.model.SeedSecretState
import co.electriccoin.zcash.ui.screen.seed.model.SeedSecretStateTooltip
import co.electriccoin.zcash.ui.screen.seed.model.SeedState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SeedViewModel(
    observePersistableWallet: ObservePersistableWalletUseCase,
    private val args: SeedNavigationArgs,
    private val walletRepository: WalletRepository,
) : ViewModel() {
    private val isRevealed = MutableStateFlow(false)

    private val observableWallet = observePersistableWallet()

    val navigateBack = MutableSharedFlow<Unit>()

    val state =
        combine(isRevealed, observableWallet) { isRevealed, wallet ->
            SeedState(
                button =
                    ButtonState(
                        text =
                            when {
                                args == SeedNavigationArgs.NEW_WALLET -> stringRes(R.string.seed_recovery_next_button)
                                isRevealed -> stringRes(R.string.seed_recovery_hide_button)
                                else -> stringRes(R.string.seed_recovery_reveal_button)
                            },
                        onClick = ::onPrimaryButtonClicked,
                        isEnabled = wallet != null,
                        isLoading = wallet == null,
                        icon =
                            when {
                                args == SeedNavigationArgs.NEW_WALLET -> null
                                isRevealed -> R.drawable.ic_seed_hide
                                else -> R.drawable.ic_seed_show
                            }
                    ),
                seed =
                    SeedSecretState(
                        title = stringRes(R.string.seed_recovery_phrase_title),
                        text = stringRes(wallet?.seedPhrase?.joinToString().orEmpty()),
                        isRevealed = isRevealed,
                        tooltip = null,
                        onClick =
                            when (args) {
                                SeedNavigationArgs.NEW_WALLET -> ::onNewWalletSeedClicked
                                SeedNavigationArgs.RECOVERY ->
                                    if (AndroidApiVersion.isAtLeastS) null else ::onNewWalletSeedClicked
                            },
                        mode = SeedSecretState.Mode.SEED,
                        isRevealPhraseVisible =
                            if (AndroidApiVersion.isAtLeastS) args == SeedNavigationArgs.NEW_WALLET else true,
                    ),
                birthday =
                    SeedSecretState(
                        title = stringRes(R.string.seed_recovery_bday_title),
                        text = stringRes(wallet?.birthday?.value?.toString().orEmpty()),
                        isRevealed = isRevealed,
                        tooltip =
                            SeedSecretStateTooltip(
                                title = stringRes(R.string.seed_recovery_bday_tooltip_title),
                                message = stringRes(R.string.seed_recovery_bday_tooltip_message)
                            ),
                        onClick = null,
                        mode = SeedSecretState.Mode.BIRTHDAY,
                        isRevealPhraseVisible = false,
                    ),
                onBack =
                    when (args) {
                        SeedNavigationArgs.NEW_WALLET -> null
                        SeedNavigationArgs.RECOVERY -> ::onBack
                    }
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT), null)

    private fun onBack() {
        viewModelScope.launch {
            navigateBack.emit(Unit)
        }
    }

    private fun onPrimaryButtonClicked() {
        when (args) {
            SeedNavigationArgs.NEW_WALLET -> walletRepository.persistOnboardingState(OnboardingState.READY)
            SeedNavigationArgs.RECOVERY -> isRevealed.update { !it }
        }
    }

    private fun onNewWalletSeedClicked() {
        viewModelScope.launch {
            isRevealed.update { !it }
        }
    }
}
