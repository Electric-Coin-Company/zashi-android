package co.electriccoin.zcash.ui.configuration

import co.electriccoin.zcash.configuration.model.entry.BooleanConfigurationEntry
import co.electriccoin.zcash.configuration.model.entry.ConfigKey

object ConfigurationEntries {
    val IS_FLEXA_AVAILABLE = BooleanConfigurationEntry(ConfigKey("is_flexa_available"), true)
}
