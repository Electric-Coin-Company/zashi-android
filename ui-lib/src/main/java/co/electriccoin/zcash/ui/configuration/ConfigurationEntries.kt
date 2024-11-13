package co.electriccoin.zcash.ui.configuration

import co.electriccoin.zcash.configuration.model.entry.BooleanConfigurationEntry
import co.electriccoin.zcash.configuration.model.entry.ConfigKey

object ConfigurationEntries {
    val IS_APP_UPDATE_CHECK_ENABLED = BooleanConfigurationEntry(ConfigKey("is_update_check_enabled"), true)

    /*
     * A troubleshooting step. If we fix our bugs, this should be unnecessary.
     */
    val IS_RESCAN_ENABLED = BooleanConfigurationEntry(ConfigKey("is_rescan_enabled"), true)

    val IS_FLEXA_AVAILABLE = BooleanConfigurationEntry(ConfigKey("is_flexa_available"), false)
}
