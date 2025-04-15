package co.electriccoin.zcash.ui.screen.home.reporting

import androidx.lifecycle.ViewModel
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CrashReportOptInViewModel(
    private val exchangeRateRepository: ExchangeRateRepository,
    private val navigationRouter: NavigationRouter
): ViewModel() {
    val state: StateFlow<CrashReportOptInState> = MutableStateFlow(
        CrashReportOptInState(
            onBack = ::dismissOptInExchangeRateUsd,
            onOptInClick = ::optInExchangeRateUsd,
            onOptOutClick = ::onSkipClick
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