package co.electriccoin.zcash.ui.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.configuration.api.ConfigurationProvider
import co.electriccoin.zcash.configuration.model.map.Configuration
import co.electriccoin.zcash.preference.api.StandardPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.BooleanPreferenceDefault
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    androidConfigurationProvider: ConfigurationProvider,
    private val standardPreferenceProvider: StandardPreferenceProvider,
) : ViewModel() {
    /**
     * A flow of whether background sync is enabled
     */
    val isBackgroundSyncEnabled: StateFlow<Boolean?> =
        booleanStateFlow(StandardPreferenceKeys.IS_BACKGROUND_SYNC_ENABLED)

    /**
     * A flow of whether keep screen on while syncing is on or off
     */
    val isKeepScreenOnWhileSyncing: StateFlow<Boolean?> =
        booleanStateFlow(StandardPreferenceKeys.IS_KEEP_SCREEN_ON_DURING_SYNC)

    /**
     * A flow of whether the app presented the user with restore success screem
     */
    val isRestoreSuccessSeen: StateFlow<Boolean?> =
        booleanStateFlow(StandardPreferenceKeys.IS_RESTORING_INITIAL_WARNING_SEEN)

    fun setRestoringInitialWarningSeen() {
        setBooleanPreference(StandardPreferenceKeys.IS_RESTORING_INITIAL_WARNING_SEEN, true)
    }

    /**
     * A flow of the wallet balances visibility.
     */
    val isHideBalances: StateFlow<Boolean?> = booleanStateFlow(StandardPreferenceKeys.IS_HIDE_BALANCES)

    fun showOrHideBalances() {
        viewModelScope.launch {
            setBooleanPreference(StandardPreferenceKeys.IS_HIDE_BALANCES, isHideBalances.filterNotNull().first().not())
        }
    }

    val configurationFlow: StateFlow<Configuration?> =
        androidConfigurationProvider.getConfigurationFlow()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT.inWholeMilliseconds),
                null
            )

    //
    // PRIVATE HELPERS
    //

    private fun booleanStateFlow(default: BooleanPreferenceDefault): StateFlow<Boolean?> =
        flow<Boolean?> {
            emitAll(default.observe(standardPreferenceProvider))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            null
        )

    private fun setBooleanPreference(
        default: BooleanPreferenceDefault,
        newState: Boolean
    ) {
        viewModelScope.launch {
            default.putValue(standardPreferenceProvider, newState)
        }
    }
}
