package cash.z.ecc.sdk.model

import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.sdk.model.SeedPhrase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

// This is a stopgap; would like to see improvements to the SeedPhrase class to have validation moved
// there as part of creating the object
sealed class SeedPhraseValidation {
    object BadCount : SeedPhraseValidation()

    object BadWord : SeedPhraseValidation()

    object FailedChecksum : SeedPhraseValidation()

    class Valid(val seedPhrase: SeedPhrase) : SeedPhraseValidation()

    companion object {
        suspend fun new(list: List<String>): SeedPhraseValidation {
            if (list.size != SeedPhrase.SEED_PHRASE_SIZE) {
                return BadCount
            }

            @Suppress("SwallowedException")
            return try {
                val stringified = list.joinToString(SeedPhrase.DEFAULT_DELIMITER)
                withContext(Dispatchers.Default) {
                    Mnemonics.MnemonicCode(stringified, Locale.ENGLISH.language).validate()
                }

                Valid(SeedPhrase.new(stringified))
            } catch (e: Mnemonics.InvalidWordException) {
                BadWord
            } catch (e: Mnemonics.ChecksumException) {
                FailedChecksum
            }
        }
    }
}
