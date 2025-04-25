package co.electriccoin.zcash.ui.screen.crashreporting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.provider.CrashReportingStorageProvider
import co.electriccoin.zcash.ui.screen.crashreporting.model.CrashReportingOptInState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CrashReportingViewModel(
    private val navigationRouter: NavigationRouter,
    private val crashReportingStorageProvider: CrashReportingStorageProvider,
) : ViewModel() {
    private val isAnalyticsEnabled = crashReportingStorageProvider.observe()

    val state: StateFlow<CrashReportingOptInState?> =
        isAnalyticsEnabled
            .map { currentOptInState ->
                CrashReportingOptInState.new(
                    isOptedIn = currentOptInState == true,
                    onBack = ::onBack,
                    onSaveClicked = {
                        onSaveClicked(it)
                        onBack()
                    }
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    private fun onSaveClicked(enabled: Boolean) = viewModelScope.launch { crashReportingStorageProvider.store(enabled) }

    private fun onBack() = navigationRouter.back()
}
