package co.electriccoin.zcash.ui.common.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.screen.about.view.WhatsNewSectionState
import co.electriccoin.zcash.ui.screen.about.view.WhatsNewState
import co.electriccoin.zcash.ui.util.stringRes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class WhatsNewViewModel(application: Application) : AndroidViewModel(application) {
    val state: StateFlow<WhatsNewState?> =
        flow {
            val changelog = VersionInfo.new(application).changelog
            emit(
                WhatsNewState(
                    version = stringRes(R.string.whats_new_version, changelog.version),
                    date = changelog.date,
                    sections =
                        listOfNotNull(changelog.added, changelog.changed, changelog.fixed, changelog.removed)
                            .map {
                                WhatsNewSectionState(
                                    stringRes(value = it.title),
                                    stringRes(it.content)
                                )
                            },
                )
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT), null)
}
