package co.electriccoin.zcash.preference.model.entry

import co.electriccoin.zcash.preference.api.PreferenceProvider

data class IntegerPreferenceDefault(
    override val key: Key,
    private val defaultValue: Int
) : PreferenceDefault<Int> {

    override suspend fun getValue(preferenceProvider: PreferenceProvider) = preferenceProvider.getString(key)?.let {
        try {
            it.toInt()
        } catch (e: NumberFormatException) {
            // [TODO #32]: Log coercion failure instead of just silently returning default
            defaultValue
        }
    } ?: defaultValue

    override suspend fun putValue(preferenceProvider: PreferenceProvider, newValue: Int) {
        preferenceProvider.putString(key, newValue.toString())
    }
}
