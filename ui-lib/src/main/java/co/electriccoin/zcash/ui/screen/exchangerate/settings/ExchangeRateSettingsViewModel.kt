package co.electriccoin.zcash.ui.screen.exchangerate.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepository
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ExchangeRateSettingsViewModel(
    private val exchangeRateRepository: ExchangeRateRepository,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    val state =
        exchangeRateRepository.state
            .map {
                createState(it)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = createState(exchangeRateRepository.state.value)
            )

    private fun createState(it: ExchangeRateState) =
        ExchangeRateSettingsState(
            isOptedIn = it is ExchangeRateState.Data,
            onSaveClick = ::onOptInExchangeRateUsdClick,
            onDismiss = ::onBack
        )

    private fun onBack() {
        navigationRouter.back()
    }

    private fun onOptInExchangeRateUsdClick(optInt: Boolean) {
        exchangeRateRepository.optInExchangeRateUsd(optIn = optInt)
        navigationRouter.back()
    }
}
