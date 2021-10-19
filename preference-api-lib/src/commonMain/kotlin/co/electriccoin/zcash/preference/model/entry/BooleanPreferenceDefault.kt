package co.electriccoin.zcash.preference.model.entry

import co.electriccoin.zcash.preference.api.PreferenceProvider

data class BooleanPreferenceDefault(
    override val key: Key,
    private val defaultValue: Boolean
) : PreferenceDefault<Boolean> {

    @Suppress("SwallowedException")
    override suspend fun getValue(preferenceProvider: PreferenceProvider) = preferenceProvider.getString(key)?.let {
        try {
            it.toBooleanStrict()
        } catch (e: IllegalArgumentException) {
            // [TODO #32]: Log coercion failure instead of just silently returning default
            defaultValue
        }
    } ?: defaultValue
}
