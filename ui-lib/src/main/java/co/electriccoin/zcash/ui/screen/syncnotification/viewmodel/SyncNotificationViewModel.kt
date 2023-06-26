package co.electriccoin.zcash.ui.screen.syncnotification.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.common.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.preference.EncryptedPreferenceKeys.SYNC_INTERVAL_OPTION
import co.electriccoin.zcash.ui.preference.StandardPreferenceSingleton
import co.electriccoin.zcash.work.WorkIds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SyncNotificationViewModel(val context: Application) : AndroidViewModel(application = context) {

    @OptIn(ExperimentalCoroutinesApi::class)
    val syncIntervalOption = flow {
        val preferenceProvider = StandardPreferenceSingleton.getInstance(context)
        emit(SYNC_INTERVAL_OPTION.observe(preferenceProvider))
    }.flatMapLatest { it }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            null
        )

    fun updateSyncIntervalOption(syncIntervalOption: SyncIntervalOption) {
        viewModelScope.launch {
            WorkIds.cancelSyncAppNotificationAndReRegister(syncIntervalOption, context)
            val preferenceProvider = StandardPreferenceSingleton.getInstance(context)
            SYNC_INTERVAL_OPTION.putValue(preferenceProvider, syncIntervalOption)
        }
    }

    enum class SyncIntervalOption(val text: String, val interval: Int) {
        WEEKLY("Weekly", 7),
        MONTHLY("Monthly", 30),
        OFF("OFF", -1);

        companion object {
            fun getSyncIntervalByText(text: String): SyncIntervalOption {
                return when (text.lowercase()) {
                    WEEKLY.text.lowercase() -> WEEKLY
                    MONTHLY.text.lowercase() -> MONTHLY
                    OFF.text.lowercase() -> OFF
                    else -> OFF
                }
            }
        }
    }
}
