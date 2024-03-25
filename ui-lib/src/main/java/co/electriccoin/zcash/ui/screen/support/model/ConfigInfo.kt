package co.electriccoin.zcash.ui.screen.support.model

import co.electriccoin.zcash.configuration.api.ConfigurationProvider
import kotlinx.datetime.Instant

// TODO [#1301]: Localize support text content
// TODO [#1301]: https://github.com/Electric-Coin-Company/zashi-android/issues/1301

data class ConfigInfo(val configurationUpdatedAt: Instant?) {
    fun toSupportString() =
        buildString {
            append("Configuration: $configurationUpdatedAt")
        }

    companion object {
        fun new(configurationProvider: ConfigurationProvider) =
            ConfigInfo(
                configurationProvider.peekConfiguration().updatedAt
            )
    }
}
