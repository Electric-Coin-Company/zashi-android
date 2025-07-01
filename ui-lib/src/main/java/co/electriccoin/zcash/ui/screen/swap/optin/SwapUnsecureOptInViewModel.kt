package co.electriccoin.zcash.ui.screen.swap.optin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.usecase.ConfirmSwapOptInUseCase
import co.electriccoin.zcash.ui.common.usecase.ConfirmUnsecureSwapOptInUseCase
import co.electriccoin.zcash.ui.common.usecase.SkipSwapOptInUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.CheckboxState
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SwapUnsecureOptInViewModel(
    private val navigationRouter: NavigationRouter,
    private val confirmUnsecureSwapOptIn: ConfirmUnsecureSwapOptInUseCase,
) : ViewModel() {

    private val isApiRequestsConsentChecked = MutableStateFlow(false)
    private val isUnsecureConnectionConsentChecked = MutableStateFlow(false)

    val state = combine(
        isApiRequestsConsentChecked,
        isUnsecureConnectionConsentChecked
    ) { isApiRequestsConsentChecked, isUnsecureConnectionConsentChecked ->
        createState(isApiRequestsConsentChecked, isUnsecureConnectionConsentChecked)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
        initialValue = createState(
            isApiRequestsConsentChecked = isApiRequestsConsentChecked.value,
            isUnsecureConnectionConsentChecked = isUnsecureConnectionConsentChecked.value
        )
    )

    private fun createState(
        isApiRequestsConsentChecked: Boolean,
        isUnsecureConnectionConsentChecked: Boolean
    ) = SwapOptInState(
        thirdParty = CheckboxState(
            title = stringRes("I understand that Zashi makes API requests to the NEAR API every time I interact with a Swap/Pay transaction. "),
            isChecked = isApiRequestsConsentChecked,
            onClick = ::onApiRequestsConsentClick
        ),
        ipAddressProtection = CheckboxState(
            title = stringRes("I understand that by not enabling Tor connection, my IP address will be leaked by the NEAR API."),
            isChecked = isUnsecureConnectionConsentChecked,
            onClick = ::onUnsecureConnectionConsentClick
        ),
        skip = ButtonState(
            text = stringRes("Go back"),
            onClick = ::onBack
        ),
        confirm = ButtonState(
            text = stringRes("Confirm"),
            onClick = ::onConfirmClick,
            isEnabled = isApiRequestsConsentChecked && isUnsecureConnectionConsentChecked
        ),
        onBack = ::onBack
    )

    private fun onApiRequestsConsentClick() = isApiRequestsConsentChecked.update { !it }

    private fun onUnsecureConnectionConsentClick() = isUnsecureConnectionConsentChecked.update { !it }

    private fun onConfirmClick() = viewModelScope.launch { confirmUnsecureSwapOptIn() }

    private fun onBack() = navigationRouter.back()
}