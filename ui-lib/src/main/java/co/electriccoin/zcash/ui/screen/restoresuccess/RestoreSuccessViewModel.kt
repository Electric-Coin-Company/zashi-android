package co.electriccoin.zcash.ui.screen.restoresuccess

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.common.provider.IsKeepScreenOnDuringRestoreProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RestoreSuccessViewModel(
    application: Application,
    private val isKeepScreenOnDuringRestoreProvider: IsKeepScreenOnDuringRestoreProvider
) : AndroidViewModel(application) {
    private val keepScreenOn = MutableStateFlow(true)

    val state =
        keepScreenOn
            .map { createState(keepScreenOn = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = createState(true)
            )

    private fun createState(keepScreenOn: Boolean) =
        RestoreSuccessViewState(
            isKeepScreenOnChecked = keepScreenOn,
            onCheckboxClick = { this.keepScreenOn.update { !keepScreenOn } },
            onPositiveClick = {
                setKeepScreenOnWhileSyncing(keepScreenOn)
            }
        )

    private fun setKeepScreenOnWhileSyncing(enabled: Boolean) =
        viewModelScope.launch { isKeepScreenOnDuringRestoreProvider.store(enabled) }
}
