package co.electriccoin.zcash.ui.screen.support.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.global.StorageChecker
import co.electriccoin.zcash.ui.common.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.screen.support.model.SupportInfo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration

class SupportViewModel(application: Application) : AndroidViewModel(application) {
    // Technically, some of the support info could be invalidated after a configuration change,
    // such as the user's current locale. However it really doesn't matter here since all we
    // care about is capturing a snapshot of the app, OS, and device state.
    val supportInfo: StateFlow<SupportInfo?> = flow<SupportInfo?> {
        val usableStorage = "${StorageChecker.checkAvailableStorageMegabytes()} MB"
        emit(SupportInfo.new(application, usableStorage))
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT, Duration.ZERO), null)
}
