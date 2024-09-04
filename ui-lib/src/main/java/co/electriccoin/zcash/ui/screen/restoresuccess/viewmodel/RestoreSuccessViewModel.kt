package co.electriccoin.zcash.ui.screen.restoresuccess.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.BooleanPreferenceDefault
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.screen.restoresuccess.view.RestoreSuccessViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RestoreSuccessViewModel(
    application: Application,
    private val standardPreferenceProvider: StandardPreferenceProvider,
) : AndroidViewModel(application) {
    private val keepScreenOn = MutableStateFlow(DEFAULT_KEEP_SCREEN_ON)

    val state =
        keepScreenOn
            .map { createState(keepScreenOn = it) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                createState(keepScreenOn = DEFAULT_KEEP_SCREEN_ON)
            )

    private fun createState(keepScreenOn: Boolean) =
        RestoreSuccessViewState(
            isKeepScreenOnChecked = keepScreenOn,
            onCheckboxClick = { this.keepScreenOn.update { !keepScreenOn } },
            onPositiveClick = {
                setKeepScreenOnWhileSyncing(keepScreenOn)
            }
        )

    private fun setKeepScreenOnWhileSyncing(enabled: Boolean) {
        setBooleanPreference(StandardPreferenceKeys.IS_KEEP_SCREEN_ON_DURING_SYNC, enabled)
    }

    private fun setBooleanPreference(
        default: BooleanPreferenceDefault,
        newState: Boolean
    ) = viewModelScope.launch {
        default.putValue(standardPreferenceProvider(), newState)
    }
}

private const val DEFAULT_KEEP_SCREEN_ON = true
