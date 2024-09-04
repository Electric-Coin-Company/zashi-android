package co.electriccoin.zcash.preference

import co.electriccoin.zcash.preference.api.PreferenceProvider
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

abstract class PreferenceHolder {
    private var preferenceProvider: PreferenceProvider? = null

    private val mutex = Mutex()

    suspend operator fun invoke(): PreferenceProvider =
        mutex.withLock {
            val preferenceProvider = preferenceProvider

            if (preferenceProvider != null) {
                return preferenceProvider
            }

            val newPreferenceProvider = create()
            this.preferenceProvider = newPreferenceProvider
            return newPreferenceProvider
        }

    protected abstract suspend fun create(): PreferenceProvider
}
