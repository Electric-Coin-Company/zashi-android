package co.electriccoin.zcash.ui.screen.restore.state

import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.sdk.model.SeedPhraseValidation
import co.electriccoin.zcash.ui.common.extension.first
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class WordList(initial: List<String> = emptyList()) {
    private val mutableState: MutableStateFlow<ImmutableList<String>> = MutableStateFlow(initial.toPersistentList())

    val current: StateFlow<ImmutableList<String>> = mutableState

    fun set(list: List<String>) {
        mutableState.value = list.toPersistentList()
    }

    fun append(words: List<String>) {
        val newList =
            (current.value + words)
                .first(SeedPhrase.SEED_PHRASE_SIZE) // Prevent pasting too many words
                .toPersistentList()

        mutableState.value = newList
    }

    fun removeLast() {
        val newList =
            if (mutableState.value.isNotEmpty()) {
                current.value.subList(0, current.value.size - 1)
            } else {
                current.value
            }.toPersistentList()

        mutableState.value = newList
    }

    // Custom toString to prevent leaking word list
    override fun toString() = "WordList"
}

fun WordList.wordValidation() =
    current
        .map { SeedPhraseValidation.new(it) }
