package co.electriccoin.zcash.ui.screen.seed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.ObservePersistableWalletUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.SeedTextState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.restore.info.SeedInfo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SeedRecoveryViewModel(
    observePersistableWallet: ObservePersistableWalletUseCase,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    private val isRevealed = MutableStateFlow(false)

    private val observableWallet = observePersistableWallet()

    val navigateBack = MutableSharedFlow<Unit>()

    val state =
        combine(isRevealed, observableWallet) { isRevealed, wallet ->
            SeedRecoveryState(
                button =
                    ButtonState(
                        text =
                            when {
                                isRevealed -> stringRes(R.string.seed_recovery_hide_button)
                                else -> stringRes(R.string.seed_recovery_reveal_button)
                            },
                        onClick = ::onPrimaryButtonClicked,
                        isEnabled = wallet != null,
                        isLoading = wallet == null,
                        icon =
                            when {
                                isRevealed -> R.drawable.ic_seed_hide
                                else -> R.drawable.ic_seed_show
                            }
                    ),
                info =
                    IconButtonState(
                        onClick = ::onInfoClick,
                        icon = R.drawable.ic_help
                    ),
                seed =
                    SeedTextState(
                        seed = wallet?.seedPhrase?.joinToString().orEmpty(),
                        isRevealed = isRevealed,
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
                    ),
                onBack = ::onBack
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT), null)

    private fun onInfoClick() {
        navigationRouter.forward(SeedInfo)
    }

    private fun onBack() {
        viewModelScope.launch {
            navigateBack.emit(Unit)
        }
    }

    private fun onPrimaryButtonClicked() {
        isRevealed.update { !it }
    }
}
