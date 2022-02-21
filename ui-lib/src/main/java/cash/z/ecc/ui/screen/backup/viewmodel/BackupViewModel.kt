package cash.z.ecc.ui.screen.backup.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.ext.collectWith
import cash.z.ecc.ui.screen.backup.model.BackupStage
import cash.z.ecc.ui.screen.backup.state.BackupState
import cash.z.ecc.ui.screen.backup.state.TestChoices
import co.electriccoin.zcash.spackle.model.Index

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

    val testChoices: TestChoices = run {
        val initialValue = if (savedStateHandle.contains(KEY_TEST_CHOICES)) {
            savedStateHandle.get<HashMap<Index, String?>>(KEY_TEST_CHOICES)
        } else {
            null
        }

        if (null == initialValue) {
            TestChoices()
        } else {
            TestChoices(initialValue)
        }
    }

    init {
        // viewModelScope is constructed with Dispatchers.Main.immediate, so this will
        // update the save state as soon as a change occurs.
        backupState.current.collectWith(viewModelScope) {
            savedStateHandle.set(KEY_STAGE, it)
        }

        testChoices.current.collectWith(viewModelScope) {
            // copy as explicit HashMap, since HashMap can be stored in a Bundle
            savedStateHandle.set(KEY_TEST_CHOICES, HashMap(it))
        }
    }

    companion object {
        private const val KEY_STAGE = "stage" // $NON-NLS
        private const val KEY_TEST_CHOICES = "test_choices" // $NON-NLS
    }
}
