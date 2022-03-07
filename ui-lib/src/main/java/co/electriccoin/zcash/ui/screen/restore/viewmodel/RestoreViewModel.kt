package co.electriccoin.zcash.ui.screen.restore.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.sdk.ext.collectWith
import co.electriccoin.zcash.ui.common.ANDROID_STATE_FLOW_TIMEOUT_MILLIS
import co.electriccoin.zcash.ui.screen.restore.state.WordList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.TreeSet

class RestoreViewModel(application: Application, savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {

    /**
     * The complete word list that the user can choose from; useful for autocomplete
     */
    // This is a hack to prevent disk IO on the main thread
    val completeWordList = flow<CompleteWordSetState> {
        // Using IO context because of https://github.com/zcash/kotlin-bip39/issues/13
        val completeWordList = withContext(Dispatchers.IO) {
            Mnemonics.getCachedWords(Locale.ENGLISH.language)
        }

        emit(CompleteWordSetState.Loaded(TreeSet(completeWordList)))
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT_MILLIS),
        CompleteWordSetState.Loading
    )

    val userWordList: WordList = run {
        val initialValue = if (savedStateHandle.contains(KEY_WORD_LIST)) {
            savedStateHandle.get<ArrayList<String>>(KEY_WORD_LIST)
        } else {
            null
        }

        if (null == initialValue) {
            WordList()
        } else {
            WordList(initialValue)
        }
    }

    init {
        // viewModelScope is constructed with Dispatchers.Main.immediate, so this will
        // update the save state as soon as a change occurs.
        userWordList.current.collectWith(viewModelScope) {
            savedStateHandle.set(KEY_WORD_LIST, it)
        }
    }

    companion object {
        private const val KEY_WORD_LIST = "word_list" // $NON-NLS
    }
}

sealed class CompleteWordSetState {
    object Loading : CompleteWordSetState()
    data class Loaded(val list: Set<String>) : CompleteWordSetState()
}
