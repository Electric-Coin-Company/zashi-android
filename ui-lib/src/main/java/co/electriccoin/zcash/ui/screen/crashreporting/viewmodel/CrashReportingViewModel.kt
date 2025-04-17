package co.electriccoin.zcash.ui.screen.crashreporting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.BooleanPreferenceDefault
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.screen.crashreporting.model.CrashReportingOptInState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CrashReportingViewModel(
    private val standardPreferenceProvider: StandardPreferenceProvider,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    private val isAnalyticsEnabled = booleanStateFlow(StandardPreferenceKeys.IS_ANALYTICS_ENABLED)

    val state: StateFlow<CrashReportingOptInState> =
        isAnalyticsEnabled
            .filterNotNull()
            .map { currentOptInState ->
                CrashReportingOptInState.new(
                    isOptedIn = currentOptInState,
                    onBack = ::onBack,
                    onSaveClicked = {
                        onSaveClicked(it)
                        onBack()
                    }
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue =
                    CrashReportingOptInState.new(
                        onBack = ::onBack,
                        onSaveClicked = {
                            onSaveClicked(it)
                            onBack()
                        }
                    )
            )

    private fun onSaveClicked(enabled: Boolean) {
        setBooleanPreference(StandardPreferenceKeys.IS_ANALYTICS_ENABLED, enabled)
    }

    private fun setBooleanPreference(
        default: BooleanPreferenceDefault,
        newState: Boolean
    ) {
        viewModelScope.launch {
            default.putValue(standardPreferenceProvider(), newState)
        }
    }

    private fun onBack() = navigationRouter.back()

    private fun booleanStateFlow(default: BooleanPreferenceDefault): StateFlow<Boolean?> =
        flow<Boolean?> {
            emitAll(default.observe(standardPreferenceProvider()))
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT), null)
}
