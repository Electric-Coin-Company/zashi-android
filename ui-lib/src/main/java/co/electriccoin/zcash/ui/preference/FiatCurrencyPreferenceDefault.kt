package co.electriccoin.zcash.ui.preference

import cash.z.ecc.android.sdk.model.FiatCurrency
import co.electriccoin.zcash.preference.api.PreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey

data class FiatCurrencyPreferenceDefault(
    override val key: PreferenceKey
) : PreferenceDefault<FiatCurrency> {
    override suspend fun getValue(preferenceProvider: PreferenceProvider) =
        preferenceProvider.getString(key)?.let { FiatCurrency(it) } ?: FiatCurrency("USD")

    override suspend fun putValue(
        preferenceProvider: PreferenceProvider,
        newValue: FiatCurrency
    ) = preferenceProvider.putString(key, newValue.code)
}
