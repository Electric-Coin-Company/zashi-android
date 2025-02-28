package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.spackle.Twig

class ResetSharedPrefsDataUseCase(
    private val encryptedPreferenceProvider: EncryptedPreferenceProvider,
    private val standardPreferenceProvider: StandardPreferenceProvider,
) {
    suspend operator fun invoke(): Boolean {
        val standardPrefsCleared = standardPreferenceProvider().clearPreferences()
        val encryptedPrefsCleared = encryptedPreferenceProvider().clearPreferences()

        Twig.info { "Both preferences cleared: ${standardPrefsCleared && encryptedPrefsCleared}" }

        return standardPrefsCleared && encryptedPrefsCleared
    }
}
