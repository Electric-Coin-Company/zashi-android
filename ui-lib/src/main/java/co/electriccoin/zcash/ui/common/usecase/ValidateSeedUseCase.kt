package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.sdk.model.SeedPhrase

class ValidateSeedUseCase {
    @Suppress("TooGenericExceptionCaught")
    operator fun invoke(words: List<String>): SeedPhrase? =
        try {
            val seed = words.joinToString(" ") { it.trim() }
            Mnemonics.MnemonicCode(seed).validate()
            SeedPhrase.new(seed)
        } catch (_: Mnemonics.InvalidWordException) {
            null
        } catch (_: Mnemonics.ChecksumException) {
            null
        } catch (_: Mnemonics.WordCountException) {
            null
        } catch (_: Exception) {
            null
        }
}
