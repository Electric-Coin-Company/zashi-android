package co.electriccoin.zcash.configuration.model.entry

import co.electriccoin.zcash.configuration.model.map.Configuration

data class BooleanConfigurationEntry(
    override val key: ConfigKey,
    private val defaultValue: Boolean
) : DefaultEntry<Boolean> {
    // TODO [#1373]: Catch and log Configuration Key Coercion Failures
    // TODO [#1373]: https://github.com/Electric-Coin-Company/zashi-android/issues/1373
    override fun getValue(configuration: Configuration) = configuration.getBoolean(key, defaultValue)
}
