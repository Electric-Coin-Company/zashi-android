package co.electriccoin.zcash.ui.screen.exchangerate.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.OptInExchangeRateAndTorUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExchangeRateTorSettingsVM(
    private val navigationRouter: NavigationRouter,
    private val optInExchangeRateAndTor: OptInExchangeRateAndTorUseCase
): ViewModel() {
    val state: StateFlow<ExchangeRateTorState> = MutableStateFlow(
        ExchangeRateTorState(
            onBack = ::onBack,
            positive = ButtonState(
                stringRes(R.string.exchange_rate_tor_opt_in_positive),
                onClick = ::onPositiveClick
            ),
            negative = ButtonState(
                stringRes(R.string.exchange_rate_tor_opt_in_negative),
                onClick = ::onNegativeClick
            ),
        )
    )

    private fun onNegativeClick() = navigationRouter.back()

    private fun onPositiveClick() = viewModelScope.launch { optInExchangeRateAndTor() }

    private fun onBack() = navigationRouter.back()
}

