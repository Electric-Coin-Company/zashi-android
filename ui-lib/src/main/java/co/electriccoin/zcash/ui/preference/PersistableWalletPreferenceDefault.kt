package co.electriccoin.zcash.ui.preference

import cash.z.ecc.sdk.model.PersistableWallet
import co.electriccoin.zcash.preference.api.PreferenceProvider
import co.electriccoin.zcash.preference.model.entry.Key
import co.electriccoin.zcash.preference.model.entry.PreferenceDefault
import org.json.JSONObject

data class PersistableWalletPreferenceDefault(
    override val key: Key
) : PreferenceDefault<PersistableWallet?> {

    override suspend fun getValue(preferenceProvider: PreferenceProvider) =
        preferenceProvider.getString(key)?.let { PersistableWallet.from(JSONObject(it)) }

    override suspend fun putValue(
        preferenceProvider: PreferenceProvider,
        newValue: PersistableWallet?
    ) = preferenceProvider.putString(key, newValue?.toJson()?.toString())
}
