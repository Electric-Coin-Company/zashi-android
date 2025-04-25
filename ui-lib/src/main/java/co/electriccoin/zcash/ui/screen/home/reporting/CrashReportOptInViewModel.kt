package co.electriccoin.zcash.ui.screen.home.reporting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.provider.CrashReportingStorageProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CrashReportOptInViewModel(
    private val navigationRouter: NavigationRouter,
    private val crashReportingStorageProvider: CrashReportingStorageProvider,
) : ViewModel() {
    val state: StateFlow<CrashReportOptInState> =
        MutableStateFlow(
            CrashReportOptInState(
                onBack = ::dismissOptInExchangeRateUsd,
                onOptInClick = ::optInExchangeRateUsd,
                onOptOutClick = ::onSkipClick
            )
        )

    private fun onSkipClick() =
        viewModelScope.launch {
            crashReportingStorageProvider.store(false)
            navigationRouter.back()
        }

    private fun optInExchangeRateUsd() =
        viewModelScope.launch {
            crashReportingStorageProvider.store(true)
            navigationRouter.back()
        }

    private fun dismissOptInExchangeRateUsd() {
        navigationRouter.back()
    }
}
