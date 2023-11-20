package co.electriccoin.zcash.ui.screen.restore.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.sdk.ext.collectWith
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.zcash.ui.common.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.screen.restore.model.RestoreStage
import co.electriccoin.zcash.ui.screen.restore.state.RestoreState
import co.electriccoin.zcash.ui.screen.restore.state.WordList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import java.util.Locale

class RestoreViewModel(application: Application, savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {

    val restoreState: RestoreState = run {
        val initialValue = if (savedStateHandle.contains(KEY_STAGE)) {
            savedStateHandle.get<RestoreStage>(KEY_STAGE)
        } else {
            null
        }

        if (null == initialValue) {
            RestoreState()
        } else {
            RestoreState(initialValue)
        }
    }

    /**
     * The complete word list that the user can choose from; useful for autocomplete
     */
    // This is a hack to prevent disk IO on the main thread
    val completeWordList = flow<CompleteWordSetState> {
        // Using IO context because of https://github.com/Electric-Coin-Company/kotlin-bip39/issues/13
        val completeWordList = withContext(Dispatchers.IO) {
            Mnemonics.getCachedWords(Locale.ENGLISH.language)
        }

        emit(CompleteWordSetState.Loaded(completeWordList.toPersistentSet()))
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
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

    val userBirthdayHeight: MutableStateFlow<BlockHeight?> = run {
        val initialValue: BlockHeight? = savedStateHandle.get<Long>(KEY_BIRTHDAY_HEIGHT)?.let {
            BlockHeight.new(ZcashNetwork.fromResources(application), it)
        }
        MutableStateFlow(initialValue)
    }

    init {
        // viewModelScope is constructed with Dispatchers.Main.immediate, so this will
        // update the save state as soon as a change occurs.
        userWordList.current.collectWith(viewModelScope) {
            savedStateHandle[KEY_WORD_LIST] = ArrayList(it)
        }

        userBirthdayHeight.collectWith(viewModelScope) {
            savedStateHandle[KEY_BIRTHDAY_HEIGHT] = it?.value
        }
    }

    companion object {
        private const val KEY_STAGE = "stage" // $NON-NLS

        private const val KEY_WORD_LIST = "word_list" // $NON-NLS

        private const val KEY_BIRTHDAY_HEIGHT = "birthday_height" // $NON-NLS
    }
}

sealed class CompleteWordSetState {
    object Loading : CompleteWordSetState()
    data class Loaded(val list: ImmutableSet<String>) : CompleteWordSetState()
}
