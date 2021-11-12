package cash.z.ecc.ui.screen.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.SynchronizerCompanion
import cash.z.ecc.sdk.model.PersistableWallet
import cash.z.ecc.ui.common.ANDROID_STATE_FLOW_TIMEOUT_MILLIS
import cash.z.ecc.ui.preference.EncryptedPreferenceKeys
import cash.z.ecc.ui.preference.EncryptedPreferenceSingleton
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// To make this more multiplatform compatible, we need to remove the dependency on Context
// for loading the preferences.
class WalletViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * A flow of the user's stored wallet.  Null indicates that no wallet has been stored.
     */
    /*
     * This is exposed, because loading the value here is faster than loading the entire Zcash SDK.
     *
     * This allows the UI to load the first launch onboarding experience a few hundred milliseconds
     * faster.
     */
    val persistableWallet = flow {
        // EncryptedPreferenceSingleton.getInstance() is a suspending function, which is why we need
        // the flow builder to provide a coroutine context.
        val encryptedPreferenceProvider = EncryptedPreferenceSingleton.getInstance(application)

        emitAll(EncryptedPreferenceKeys.PERSISTABLE_WALLET.observe(encryptedPreferenceProvider))
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(stopTimeoutMillis = ANDROID_STATE_FLOW_TIMEOUT_MILLIS),
        null
    )

    /**
     * A flow of the Zcash SDK initialized with the user's stored wallet.  Null indicates that no
     * wallet has been stored.
     */
    // Note: in the future we might want to convert this to emitting a sealed class with states like:
    // - No wallet
    // - Current wallet
    // - Error loading wallet
    val synchronizer = persistableWallet.map { persistableWallet ->
        persistableWallet?.let { SynchronizerCompanion.load(application, persistableWallet) }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(stopTimeoutMillis = ANDROID_STATE_FLOW_TIMEOUT_MILLIS),
        null
    )

    /**
     * Persists a wallet asynchronously.  Clients observe either [persistableWallet] or [synchronizer]
     * to see the side effects.
     *
     * This method does not prevent multiple calls, so clients should be careful not to call this
     * method multiple times in rapid succession.  While the persistableWallet write is atomic,
     * the ordering of the writes is not specified.  If the same persistableWallet is passed in,
     * then there's no problem.  But if different persistableWallets are passed in, then which one
     * actually gets written is non-deterministic.
     */
    fun persistWallet(persistableWallet: PersistableWallet) {
        viewModelScope.launch {
            val preferenceProvider = EncryptedPreferenceSingleton.getInstance(getApplication())
            EncryptedPreferenceKeys.PERSISTABLE_WALLET.putValue(preferenceProvider, persistableWallet)
        }
    }
}
