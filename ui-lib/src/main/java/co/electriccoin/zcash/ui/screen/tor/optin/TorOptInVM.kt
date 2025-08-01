package co.electriccoin.zcash.ui.screen.tor.optin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.usecase.OptInTorUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TorOptInVM(
    private val optInExchangeRateAndTor: OptInTorUseCase,
    private val navigationRouter: NavigationRouter
) : ViewModel() {
    val state: StateFlow<TorOptInState> =
        MutableStateFlow(
            TorOptInState(
                onBack = ::onBack,
                onEnableClick = ::onOptInClick,
                onSkipClick = ::onSkipClick
            )
        )

    private fun onSkipClick() = viewModelScope.launch { optInExchangeRateAndTor(false) { back() } }

    private fun onOptInClick() = viewModelScope.launch { optInExchangeRateAndTor(true) { back() } }

    private fun onBack() = navigationRouter.back()
}
