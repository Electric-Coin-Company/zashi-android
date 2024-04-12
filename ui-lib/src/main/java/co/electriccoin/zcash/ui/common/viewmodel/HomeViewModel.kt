package co.electriccoin.zcash.ui.common.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.configuration.AndroidConfigurationFactory
import co.electriccoin.zcash.configuration.model.map.Configuration
import co.electriccoin.zcash.preference.model.entry.BooleanPreferenceDefault
import co.electriccoin.zcash.ui.common.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.preference.StandardPreferenceSingleton
import co.electriccoin.zcash.ui.screen.home.HomeScreenIndex
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * Current Home sub-screen index in flow.
     */
    val screenIndex: MutableStateFlow<HomeScreenIndex> = MutableStateFlow(HomeScreenIndex.ACCOUNT)

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
     * A flow of whether the app uses simple or detailed block synchronization status information for the UI
     */
    val isDetailedSyncStatus: StateFlow<Boolean?> =
        booleanStateFlow(StandardPreferenceKeys.IS_DETAILED_SYNC_STATUS)

    /**
     * A flow of whether the app presented the user with an initial restoring dialog
     */
    val isRestoringInitialWarningSeen: StateFlow<Boolean?> =
        booleanStateFlow(StandardPreferenceKeys.IS_RESTORING_INITIAL_WARNING_SEEN)

    fun setRestoringInitialWarningSeen() {
        setBooleanPreference(StandardPreferenceKeys.IS_RESTORING_INITIAL_WARNING_SEEN, true)
    }

    private fun booleanStateFlow(default: BooleanPreferenceDefault): StateFlow<Boolean?> =
        flow<Boolean?> {
            val preferenceProvider = StandardPreferenceSingleton.getInstance(getApplication())
            emitAll(default.observe(preferenceProvider))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            null
        )

    val configurationFlow: StateFlow<Configuration?> =
        AndroidConfigurationFactory.getInstance(application).getConfigurationFlow()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT.inWholeMilliseconds),
                null
            )

    private fun setBooleanPreference(
        default: BooleanPreferenceDefault,
        newState: Boolean
    ) {
        viewModelScope.launch {
            val prefs = StandardPreferenceSingleton.getInstance(getApplication())
            default.putValue(prefs, newState)
        }
    }
}
