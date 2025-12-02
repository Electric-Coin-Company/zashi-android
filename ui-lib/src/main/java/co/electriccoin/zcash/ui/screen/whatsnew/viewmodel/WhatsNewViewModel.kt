package co.electriccoin.zcash.ui.screen.whatsnew.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.screen.advancedsettings.debug.db.DebugDBArgs
import co.electriccoin.zcash.ui.screen.whatsnew.model.WhatsNewState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class WhatsNewViewModel(
    application: Application,
    private val navigationRouter: NavigationRouter,
) : AndroidViewModel(application) {
    val state: StateFlow<WhatsNewState?> =
        flow {
            val versionInfo = VersionInfo.new(application)
            emit(
                WhatsNewState.new(
                    changelog = versionInfo.changelog,
                    version = versionInfo.versionName,
                    onVersionLongClick = { navigationRouter.forward(DebugDBArgs) }
                )
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )
}
