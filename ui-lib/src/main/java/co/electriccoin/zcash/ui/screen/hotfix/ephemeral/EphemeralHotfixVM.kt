package co.electriccoin.zcash.ui.screen.hotfix.ephemeral

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.usecase.FixEphemeralAddressUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EphemeralHotfixVM(
    args: EphemeralHotfixArgs,
    private val navigationRouter: NavigationRouter,
    private val fixEphemeralAddressUseCase: FixEphemeralAddressUseCase,
) : ViewModel() {

    private val text = MutableStateFlow(args.address)

    private val isError = MutableStateFlow(false)

    val state: StateFlow<EphemeralHotfixState?> = combine(
        text,
        fixEphemeralAddressUseCase.observeIsLoading(),
        isError
    ) { text, isLoading, isError ->
        EphemeralHotfixState(
            address = TextFieldState(
                value = stringRes(text.orEmpty()),
                onValueChange = { new -> this.text.update { new } },
                isEnabled = !isLoading,
                error = stringRes("").takeIf { isError }
            ),
            button = ButtonState(
                stringRes("Recover Funds"),
                onClick = ::onRecoverFundsClick,
                isEnabled = !text.isNullOrBlank() && !isLoading
            ),
            onBack = ::onBack
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
        initialValue = null
    )

    private fun onBack() = navigationRouter.back()

    private fun onRecoverFundsClick() = fixEphemeralAddressUseCase(text.value.orEmpty())
}