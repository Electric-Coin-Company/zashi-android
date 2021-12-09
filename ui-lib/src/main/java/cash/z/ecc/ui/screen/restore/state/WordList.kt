package cash.z.ecc.ui.screen.restore.state

import cash.z.ecc.sdk.model.SeedPhraseValidation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class WordList(initial: List<String> = emptyList()) {
    private val mutableState = MutableStateFlow(initial)

    val current: StateFlow<List<String>> = mutableState

    fun set(list: List<String>) {
        mutableState.value = ArrayList(list)
    }

    fun append(words: List<String>) {
        mutableState.value = ArrayList(current.value) + words
    }

    // Custom toString to prevent leaking word list
    override fun toString() = "WordList"
}

fun WordList.wordValidation() = current
    .map { SeedPhraseValidation.new(it) }
