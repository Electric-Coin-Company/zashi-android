package co.electriccoin.zcash.configuration.test

import co.electriccoin.zcash.configuration.model.entry.ConfigKey
import co.electriccoin.zcash.configuration.model.exception.ConfigurationParseException
import co.electriccoin.zcash.configuration.model.map.Configuration
import kotlinx.datetime.Instant

/**
 * @param configurationMapping A mapping of key-value pairs to be returned
 * by [.getString].  Note: this map is not defensively copied, allowing users of this class to
 * mutate the configuration by mutating the original map. The mapping is stored in a val field
 * though, making the initial mapping thread-safe.
 */
class MockConfiguration(private val configurationMapping: Map<String, String> = emptyMap()) : Configuration {
    override val updatedAt: Instant? = null

    @Throws(ConfigurationParseException::class)
    override fun getBoolean(
        key: ConfigKey,
        defaultValue: Boolean
    ) = configurationMapping[key.key]?.let {
        try {
            it.toBooleanStrict()
        } catch (e: IllegalArgumentException) {
            throw ConfigurationParseException(
                "Failed while parsing String value to Boolean. This could mean " +
                    "someone made an error in the remote config console",
                e
            )
        }
    } ?: defaultValue

    @Throws(ConfigurationParseException::class)
    override fun getInt(
        key: ConfigKey,
        defaultValue: Int
    ) = configurationMapping[key.key]?.let {
        try {
            it.toInt()
        } catch (e: IllegalArgumentException) {
            throw ConfigurationParseException(
                "Failed while parsing String value to Int. This could mean " +
                    "someone made an error in the remote config console",
                e
            )
        }
    } ?: defaultValue

    override fun getString(
        key: ConfigKey,
        defaultValue: String
    ) = configurationMapping.getOrElse(key.key) { defaultValue }

    override fun hasKey(key: ConfigKey) = configurationMapping.containsKey(key.key)
}
