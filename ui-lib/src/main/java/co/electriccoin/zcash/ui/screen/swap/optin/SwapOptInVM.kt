package co.electriccoin.zcash.ui.screen.swap.optin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.usecase.ConfirmSwapOptInUseCase
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

class SwapOptInVM(
    private val navigationRouter: NavigationRouter,
    private val confirmSwapOptIn: ConfirmSwapOptInUseCase,
    private val skipSwapOptInUse: SkipSwapOptInUseCase
) : ViewModel() {

    private val isThirdPartyChecked = MutableStateFlow(false)
    private val isIpAddressProtectionChecked = MutableStateFlow(false)

    val state = combine(
        isThirdPartyChecked,
        isIpAddressProtectionChecked
    ) { isThirdPartyChecked, isIpAddressProtectionChecked ->
        createState(isThirdPartyChecked, isIpAddressProtectionChecked)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
        initialValue = createState(
            isThirdPartyChecked = isThirdPartyChecked.value,
            isIpAddressProtectionChecked = isIpAddressProtectionChecked.value
        )
    )

    private fun createState(
        isThirdPartyChecked: Boolean,
        isIpAddressProtectionChecked: Boolean
    ) = SwapOptInState(
        thirdParty = CheckboxState(
            title = stringRes("Allow Third-Party Requests"),
            subtitle = stringRes("Enable API calls to the NEAR API."),
            isChecked = isThirdPartyChecked,
            onClick = ::onThirdPartyClick
        ),
        ipAddressProtection = CheckboxState(
            title = stringRes("Turn on IP Address Protection"),
            subtitle = stringRes("Protect IP address with Tor connection."),
            isChecked = isIpAddressProtectionChecked,
            onClick = ::onIpAddressProtectionClick
        ),
        skip = ButtonState(
            text = stringRes("Skip"),
            onClick = ::onSkipClick
        ),
        confirm = ButtonState(
            text = stringRes("Confirm"),
            onClick = ::onConfirmClick,
            isEnabled = isThirdPartyChecked && isIpAddressProtectionChecked
        ),
        onBack = ::onBack
    )

    private fun onThirdPartyClick() = isThirdPartyChecked.update { !it }

    private fun onIpAddressProtectionClick() = isIpAddressProtectionChecked.update { !it }

    private fun onSkipClick() = skipSwapOptInUse()

    private fun onConfirmClick() = viewModelScope.launch { confirmSwapOptIn() }

    private fun onBack() = navigationRouter.back()
}