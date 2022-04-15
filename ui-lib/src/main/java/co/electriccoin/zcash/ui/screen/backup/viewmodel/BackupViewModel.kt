package co.electriccoin.zcash.ui.screen.backup.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.ext.collectWith
import co.electriccoin.zcash.ui.screen.backup.model.BackupStage
import co.electriccoin.zcash.ui.screen.backup.state.BackupState

class BackupViewModel(application: Application, savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {
    val backupState: BackupState = run {
        val initialValue = if (savedStateHandle.contains(KEY_STAGE)) {
            savedStateHandle.get<BackupStage>(KEY_STAGE)
        } else {
            null
        }

        if (null == initialValue) {
            BackupState()
        } else {
            BackupState(initialValue)
        }
    }

    init {
        // viewModelScope is constructed with Dispatchers.Main.immediate, so this will
        // update the save state as soon as a change occurs.
        backupState.current.collectWith(viewModelScope) {
            savedStateHandle.set(KEY_STAGE, it)
        }
    }

    companion object {
        private const val KEY_STAGE = "stage" // $NON-NLS
    }
}
