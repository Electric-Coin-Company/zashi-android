package co.electriccoin.zcash.ui.screen.home.viewmodel

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
import kotlinx.coroutines.flow.update

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * A flow of whether background sync is enabled
     */
    val isBackgroundSyncEnabled: StateFlow<Boolean?> = flow {
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

    /**
     * A flow of whether transfer tab is enabled. We disable the transfer tab in sync state
     */
    private val _isTransferTabEnabled = MutableStateFlow(false)
    val isTransferStateEnabled: StateFlow<Boolean> get() = _isTransferTabEnabled

    /**
     * A flow of whether bottom nav bar should show
     */
    private val _isBottomNavBarVisible = MutableStateFlow(true)
    val isBottomNavBarVisible: StateFlow<Boolean> get() = _isBottomNavBarVisible

    fun onTransferTabStateChanged(enable: Boolean) {
        _isTransferTabEnabled.update { enable }
    }

    fun onBottomNavBarVisibilityChanged(show: Boolean) {
        _isBottomNavBarVisible.update { show }
    }
}
