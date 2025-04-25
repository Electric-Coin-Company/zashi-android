package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.sdk.model.SeedPhrase
import java.util.Locale

class ValidateSeedUseCase {
    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    operator fun invoke(words: List<String>): SeedPhrase? =
        try {
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
