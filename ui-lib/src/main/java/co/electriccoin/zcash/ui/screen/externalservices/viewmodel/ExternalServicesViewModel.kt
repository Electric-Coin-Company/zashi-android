package co.electriccoin.zcash.ui.screen.externalservices.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.common.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.preference.StandardPreferenceSingleton
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExternalServicesViewModel(val context: Application): AndroidViewModel(application = context) {

    val isUnStoppableServiceEnabled = flow {
        val pref = StandardPreferenceSingleton.getInstance(context)
        emitAll(StandardPreferenceKeys.IS_UNSTOPPABLE_SERVICE_ENABLED.observe(pref))
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
        null
    )

    fun updateUnStoppableServiceStatus(isEnabled: Boolean) {
        viewModelScope.launch {
            val pref = StandardPreferenceSingleton.getInstance(context)
            StandardPreferenceKeys.IS_UNSTOPPABLE_SERVICE_ENABLED.putValue(pref, isEnabled)
        }
    }

}
