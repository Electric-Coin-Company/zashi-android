package co.electriccoin.zcash.ui.screen.security.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.common.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.common.isBioMetricEnabledOnMobile
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.preference.StandardPreferenceSingleton
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SecurityViewModel(val context: Application) : AndroidViewModel(application = context) {

    @OptIn(ExperimentalCoroutinesApi::class)
    val lastEnteredPin = flow {
        val preferenceProvider = StandardPreferenceSingleton.getInstance(context)
        emit(StandardPreferenceKeys.LAST_ENTERED_PIN.observe(preferenceProvider))
    }.flatMapLatest { it }
        .stateIn(viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val isTouchIdOrFaceIdEnabled = flow {
        val preferenceProvider = StandardPreferenceSingleton.getInstance(context)
        emit(StandardPreferenceKeys.IS_TOUCH_ID_OR_FACE_ID_ENABLED.observe(preferenceProvider))
    }.flatMapLatest { it }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
        null
    )

    fun updateTouchIdFaceIdStatus(enable: Boolean) {
        viewModelScope.launch {
            val preferenceProvider = StandardPreferenceSingleton.getInstance(context)
            StandardPreferenceKeys.IS_TOUCH_ID_OR_FACE_ID_ENABLED.putValue(preferenceProvider, enable)
        }
    }

    fun disablePin() {
        viewModelScope.launch {
            val preferenceProvider = StandardPreferenceSingleton.getInstance(context)
            StandardPreferenceKeys.LAST_ENTERED_PIN.putValue(preferenceProvider, "")
        }
    }

    fun isBioMetricEnabledOnMobile() = context.isBioMetricEnabledOnMobile()
}
