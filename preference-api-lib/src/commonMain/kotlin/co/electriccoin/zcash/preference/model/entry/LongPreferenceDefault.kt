package co.electriccoin.zcash.preference.model.entry

import co.electriccoin.zcash.preference.api.PreferenceProvider

data class LongPreferenceDefault(
    override val key: PreferenceKey,
    private val defaultValue: Long
) : PreferenceDefault<Long> {
    override suspend fun getValue(preferenceProvider: PreferenceProvider) =
        preferenceProvider.getString(key)?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                // TODO [#32]: Log coercion failure instead of just silently returning default
                // TODO [#32]: https://github.com/Electric-Coin-Company/zashi-android/issues/32
                defaultValue
            }
        } ?: defaultValue

    override suspend fun putValue(
        preferenceProvider: PreferenceProvider,
        newValue: Long
    ) {
        preferenceProvider.putString(key, newValue.toString())
    }
}
