package co.electriccoin.zcash.ui.screen.pin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.common.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.common.isBioMetricEnabledOnMobile
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.preference.StandardPreferenceSingleton
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PinViewModel(val context: Application): AndroidViewModel(application = context) {

    fun savePin(pin: String) {
        viewModelScope.launch {
            val preferenceProvider = StandardPreferenceSingleton.getInstance(context)
            StandardPreferenceKeys.LAST_ENTERED_PIN.putValue(preferenceProvider, pin)
        }
    }

    val lastSavedPin = flow {
        val preferenceProvider = StandardPreferenceSingleton.getInstance(context)
        emit(StandardPreferenceKeys.LAST_ENTERED_PIN.getValue(preferenceProvider))
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
        null
    )

    val isTouchIdOrFaceIdEnabled = flow {
        val preferenceProvider = StandardPreferenceSingleton.getInstance(context)
        emit(StandardPreferenceKeys.IS_TOUCH_ID_OR_FACE_ID_ENABLED.getValue(preferenceProvider))
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
        null
    )

    fun isBioMetricEnabledOnMobile() = context.isBioMetricEnabledOnMobile()

}
