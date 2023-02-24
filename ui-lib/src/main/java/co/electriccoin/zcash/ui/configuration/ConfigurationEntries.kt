package co.electriccoin.zcash.ui.configuration

import co.electriccoin.zcash.configuration.model.entry.BooleanConfigurationEntry
import co.electriccoin.zcash.configuration.model.entry.ConfigKey

object ConfigurationEntries {
    val IS_APP_UPDATE_CHECK_ENABLED = BooleanConfigurationEntry(ConfigKey("is_update_check_enabled"), true)

    /*
     * The full onboarding flow is functional and tested, but it is disabled by default for an initially minimal feature set.
     */
    val IS_FULL_ONBOARDING_ENABLED = BooleanConfigurationEntry(ConfigKey("is_full_onboarding_enabled"), false)

    /*
     * A troubleshooting step. If we fix our bugs, this should be unnecessary.
     */
    val IS_RESCAN_ENABLED = BooleanConfigurationEntry(ConfigKey("is_rescan_enabled"), true)
}
