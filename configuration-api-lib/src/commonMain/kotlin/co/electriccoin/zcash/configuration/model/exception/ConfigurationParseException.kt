package co.electriccoin.zcash.configuration.model.exception

/**
 * Exception that may occur when parsing a value from the remote configuration. This could mean that someone made an
 * error in the remote config console.
 */
class ConfigurationParseException(
    message: String,
    cause: Throwable?
) : IllegalArgumentException(message, cause)
