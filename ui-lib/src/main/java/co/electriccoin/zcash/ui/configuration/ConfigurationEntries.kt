package co.electriccoin.zcash.ui.configuration

import co.electriccoin.zcash.configuration.model.entry.BooleanConfigurationEntry
import co.electriccoin.zcash.configuration.model.entry.ConfigKey

object ConfigurationEntries {
    val IS_APP_UPDATE_CHECK_ENABLED = BooleanConfigurationEntry(ConfigKey("is_update_check_enabled"), true)

    /*
     * Disabled because we don't have the URI parser support in the SDK yet.
     *
     */
    val IS_REQUEST_ZEC_ENABLED = BooleanConfigurationEntry(ConfigKey("is_update_check_enabled"), false)
}
