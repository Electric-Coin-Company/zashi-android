package co.electriccoin.zcash.ui.common.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.configuration.AndroidConfigurationFactory
import co.electriccoin.zcash.configuration.model.map.Configuration
import co.electriccoin.zcash.ui.common.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.preference.StandardPreferenceSingleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * Current Home sub-screen index in flow.
     */
    val screenIndex: MutableStateFlow<Int> = MutableStateFlow(0)

    /**
     * A flow of whether background sync is enabled
     * Current Home sub-screen index in flow.
     */
    val isBackgroundSyncEnabled: StateFlow<Boolean?> =
        flow {
            val preferenceProvider = StandardPreferenceSingleton.getInstance(application)
            emitAll(StandardPreferenceKeys.IS_BACKGROUND_SYNC_ENABLED.observe(preferenceProvider))
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT.inWholeMilliseconds), null)

    val configurationFlow: StateFlow<Configuration?> =
        AndroidConfigurationFactory.getInstance(application).getConfigurationFlow()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT.inWholeMilliseconds),
                null
            )
}
