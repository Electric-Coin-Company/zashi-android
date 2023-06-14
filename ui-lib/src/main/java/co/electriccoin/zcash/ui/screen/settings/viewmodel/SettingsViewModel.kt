package co.electriccoin.zcash.ui.screen.settings.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.preference.model.entry.BooleanPreferenceDefault
import co.electriccoin.zcash.ui.common.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.preference.StandardPreferenceSingleton
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val mutex = Mutex()

    val isAnalyticsEnabled: StateFlow<Boolean?> = booleanStateFlow(StandardPreferenceKeys.IS_ANALYTICS_ENABLED)

    val isBackgroundSync: StateFlow<Boolean?> = booleanStateFlow(StandardPreferenceKeys.IS_BACKGROUND_SYNC_ENABLED)

    val isKeepScreenOnWhileSyncing: StateFlow<Boolean?> =
        booleanStateFlow(StandardPreferenceKeys.IS_KEEP_SCREEN_ON_DURING_SYNC)

    private fun booleanStateFlow(default: BooleanPreferenceDefault): StateFlow<Boolean?> = flow<Boolean?> {
        val preferenceProvider = StandardPreferenceSingleton.getInstance(getApplication())
        emitAll(default.observe(preferenceProvider))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT), null)

    fun setAnalyticsEnabled(enabled: Boolean) {
        setBooleanPreference(StandardPreferenceKeys.IS_ANALYTICS_ENABLED, enabled)
    }

    fun setBackgroundSyncEnabled(enabled: Boolean) {
        setBooleanPreference(StandardPreferenceKeys.IS_BACKGROUND_SYNC_ENABLED, enabled)
    }

    fun setKeepScreenOnWhileSyncing(enabled: Boolean) {
        setBooleanPreference(StandardPreferenceKeys.IS_KEEP_SCREEN_ON_DURING_SYNC, enabled)
    }

    private fun setBooleanPreference(default: BooleanPreferenceDefault, newState: Boolean) {
        viewModelScope.launch {
            val prefs = StandardPreferenceSingleton.getInstance(getApplication())
            mutex.withLock {
                default.putValue(prefs, newState)
            }
        }
    }
}
