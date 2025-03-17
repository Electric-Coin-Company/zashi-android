package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.sdk.model.SeedPhrase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class ValidateSeedUseCase {
    operator fun invoke(words: List<String>): SeedPhrase? {
        return try {
            val seed = words.joinToString(" ") { it.trim() }.trim()
            Mnemonics.MnemonicCode(seed, Locale.ENGLISH.language).validate()
            SeedPhrase(words)
        } catch (e: Mnemonics.InvalidWordException) {
            null
        } catch (e: Mnemonics.ChecksumException) {
            null
        } catch (e: Mnemonics.WordCountException) {
            null
        } catch (e: Exception) {
            null
        }
    }
}
