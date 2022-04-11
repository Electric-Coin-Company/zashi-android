package co.electriccoin.zcash.ui.screen.restore.model

import cash.z.ecc.sdk.model.SeedPhrase
import co.electriccoin.zcash.ui.common.first
import java.util.Locale

internal sealed class ParseResult {
    object Continue : ParseResult()
    data class Add(val words: List<String>) : ParseResult() {
        // Override to prevent logging of user secrets
        override fun toString() = "Add"
    }

    data class Autocomplete(val suggestions: List<String>) : ParseResult() {
        // Override to prevent logging of user secrets
        override fun toString() = "Autocomplete"
    }

    data class Warn(val suggestions: List<String>) : ParseResult() {
        // Override to prevent logging of user secrets
        override fun toString() = "Warn"
    }

    companion object {
        @Suppress("ReturnCount")
        fun new(completeWordList: Set<String>, rawInput: String): ParseResult {
            // Note: This assumes the word list is English words
            val trimmed = rawInput.lowercase(Locale.US).trim()

            if (trimmed.isBlank()) {
                return Continue
            }

            val autocomplete = completeWordList.filter { it.startsWith(trimmed) }

            // we accept the word only in case that there is no other available
            if (completeWordList.contains(trimmed) && autocomplete.size == 1) {
                return Add(listOf(trimmed))
            }

            if (autocomplete.isNotEmpty()) {
                return Autocomplete(autocomplete)
            }

            val multiple = trimmed.split(SeedPhrase.DEFAULT_DELIMITER)
                .filter { completeWordList.contains(it) }
                .first(SeedPhrase.SEED_PHRASE_SIZE)
            if (multiple.isNotEmpty()) {
                return Add(multiple)
            }

            return Warn(findSuggestions(trimmed, completeWordList))
        }
    }
}

internal fun findSuggestions(input: String, completeWordList: Set<String>): List<String> {
    return if (input.isBlank()) {
        emptyList()
    } else {
        completeWordList.filter { it.startsWith(input) }.ifEmpty {
            findSuggestions(input.dropLast(1), completeWordList)
        }
    }
}
