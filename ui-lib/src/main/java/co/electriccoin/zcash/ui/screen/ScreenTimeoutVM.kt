package co.electriccoin.zcash.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.common.usecase.IsScreenTimeoutDisabledDuringRestoreUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn

class ScreenTimeoutVM(
    isScreenTimeoutDisabledDuringSync: IsScreenTimeoutDisabledDuringRestoreUseCase
) : ViewModel() {
    val isScreenTimeoutDisabled =
        isScreenTimeoutDisabledDuringSync
            .observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )
}
