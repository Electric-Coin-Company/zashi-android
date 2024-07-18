package co.electriccoin.zcash.ui.screen.whatsnew.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.common.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.screen.whatsnew.model.WhatsNewState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class WhatsNewViewModel(application: Application) : AndroidViewModel(application) {
    val state: StateFlow<WhatsNewState?> =
        flow {
            val changelog = VersionInfo.new(application).changelog
            emit(WhatsNewState.new(changelog))
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT), null)
}
