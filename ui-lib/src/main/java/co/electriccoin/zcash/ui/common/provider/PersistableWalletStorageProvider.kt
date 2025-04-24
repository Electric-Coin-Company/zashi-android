package co.electriccoin.zcash.ui.common.provider

import cash.z.ecc.android.sdk.model.PersistableWallet
import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.api.PreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import org.json.JSONObject

interface PersistableWalletStorageProvider : NullableStorageProvider<PersistableWallet>

class PersistableWalletStorageProviderImpl(
    override val preferenceHolder: EncryptedPreferenceProvider,
) : BaseNullableStorageProvider<PersistableWallet>(),
    PersistableWalletStorageProvider {
    override val default: PreferenceDefault<PersistableWallet?> =
        PersistableWalletPreferenceDefault(PreferenceKey("persistable_wallet"))
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
