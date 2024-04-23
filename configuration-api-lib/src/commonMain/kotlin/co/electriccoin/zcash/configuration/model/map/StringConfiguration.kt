package co.electriccoin.zcash.configuration.model.map

import co.electriccoin.zcash.configuration.model.entry.ConfigKey
import co.electriccoin.zcash.configuration.model.exception.ConfigurationParseException
import kotlinx.collections.immutable.PersistentMap
import kotlinx.datetime.Instant

// The configurationMapping is intended to be a public API for configuration implementations rather
// than a public API for configuration clients.
data class StringConfiguration(
    val configurationMapping: PersistentMap<String, String>,
    override val updatedAt: Instant?
) : Configuration {
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
