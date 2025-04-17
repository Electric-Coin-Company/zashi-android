package co.electriccoin.zcash.ui.preference

import cash.z.ecc.android.sdk.model.PersistableWallet
import co.electriccoin.zcash.preference.api.PreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import org.json.JSONObject

data class PersistableWalletPreferenceDefault(
    override val key: PreferenceKey
) : PreferenceDefault<PersistableWallet?> {
    override suspend fun getValue(preferenceProvider: PreferenceProvider) =
        preferenceProvider.getString(key)?.let { PersistableWallet.from(JSONObject(it)) }

    override suspend fun putValue(
        preferenceProvider: PreferenceProvider,
        newValue: PersistableWallet?
    ) = preferenceProvider.putString(key, newValue?.toJson()?.toString())

    suspend fun remove(preferenceProvider: PreferenceProvider) {
        preferenceProvider.remove(key)
    }
}
