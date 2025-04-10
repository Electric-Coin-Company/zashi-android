package co.electriccoin.zcash.ui.screen.exchangerate.optin

import androidx.lifecycle.ViewModel
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ExchangeRateOptInViewModel(
    private val exchangeRateRepository: ExchangeRateRepository,
    private val navigationRouter: NavigationRouter
): ViewModel() {
    val state: StateFlow<ExchangeRateOptInState> = MutableStateFlow(
        ExchangeRateOptInState(
            onBack = ::dismissOptInExchangeRateUsd,
            onEnableClick = ::optInExchangeRateUsd,
            onSkipClick = ::onSkipClick
        )
    )

    private fun onSkipClick() {
        exchangeRateRepository.optInExchangeRateUsd(false)
        navigationRouter.back()
    }

    private fun optInExchangeRateUsd() {
        exchangeRateRepository.optInExchangeRateUsd(true)
        navigationRouter.back()
    }

    private fun dismissOptInExchangeRateUsd() {
        navigationRouter.back()
    }
}