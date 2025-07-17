package co.electriccoin.zcash.ui.common.provider

import cash.z.ecc.android.sdk.model.PersistableWallet
import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.api.PreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import org.json.JSONObject
import kotlin.time.Duration

interface PersistableWalletProvider {
    val persistableWallet: Flow<PersistableWallet?>

    suspend fun store(persistableWallet: PersistableWallet)

    suspend fun getPersistableWallet(): PersistableWallet?

    suspend fun requirePersistableWallet(): PersistableWallet
}

class PersistableWalletProviderImpl(preferenceHolder: EncryptedPreferenceProvider): PersistableWalletProvider {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val persistableWalletStorageProvider = PersistableWalletStorageProviderImpl(preferenceHolder)

    override val persistableWallet: Flow<PersistableWallet?> =
        persistableWalletStorageProvider
            .observe()
            .shareIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(Duration.ZERO, Duration.ZERO),
                replay = 1
            )

    override suspend fun store(persistableWallet: PersistableWallet) {
        persistableWalletStorageProvider.store(persistableWallet)
    }

    override suspend fun getPersistableWallet() = persistableWalletStorageProvider.get()

    override suspend fun requirePersistableWallet() = checkNotNull(persistableWalletStorageProvider.get())
}

private interface PersistableWalletStorageProvider : NullableStorageProvider<PersistableWallet>

private class PersistableWalletStorageProviderImpl(
    override val preferenceHolder: EncryptedPreferenceProvider,
) : BaseNullableStorageProvider<PersistableWallet>(),
    PersistableWalletStorageProvider {
    override val default = PersistableWalletPreferenceDefault(PreferenceKey("persistable_wallet"))
}

private class PersistableWalletPreferenceDefault(
    override val key: PreferenceKey
) : PreferenceDefault<PersistableWallet?> {
    override suspend fun getValue(preferenceProvider: PreferenceProvider) =
        preferenceProvider.getString(key)?.let { PersistableWallet.from(JSONObject(it)) }

    override suspend fun putValue(
        preferenceProvider: PreferenceProvider,
        newValue: PersistableWallet?
    ) = preferenceProvider.putString(key, newValue?.toJson()?.toString())
}
