package co.electriccoin.zcash.ui.screen.exchangerate.optin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.usecase.OptInExchangeRateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExchangeRateOptInVM(
    private val optInExchangeRate: OptInExchangeRateUseCase,
    private val navigationRouter: NavigationRouter
) : ViewModel() {
    val state: StateFlow<ExchangeRateOptInState> =
        MutableStateFlow(
            ExchangeRateOptInState(
                onBack = ::dismissOptInExchangeRateUsd,
                onEnableClick = ::optInExchangeRateUsd,
                onSkipClick = ::onSkipClick
            )
        )

    private fun onSkipClick() = viewModelScope.launch { optInExchangeRate(false) }

    private fun optInExchangeRateUsd() = viewModelScope.launch { optInExchangeRate(true) }

    private fun dismissOptInExchangeRateUsd() = navigationRouter.back()
}
