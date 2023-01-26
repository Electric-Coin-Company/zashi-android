package co.electriccoin.zcash.ui.screen.settings.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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

    val isAnalyticsEnabled: StateFlow<Boolean?> = flow<Boolean?> {
        val preferenceProvider = StandardPreferenceSingleton.getInstance(application)
        emitAll(StandardPreferenceKeys.IS_ANALYTICS_ENABLED.observe(preferenceProvider))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT), null)

    fun setAnalyticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val prefs = StandardPreferenceSingleton.getInstance(getApplication())
            mutex.withLock {
                // Note that the Application object observes this and performs the actual side effects
                StandardPreferenceKeys.IS_ANALYTICS_ENABLED.putValue(prefs, enabled)
            }
        }
    }
}
