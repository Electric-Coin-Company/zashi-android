package co.electriccoin.zcash.ui.configuration

import co.electriccoin.zcash.configuration.model.entry.BooleanConfigurationEntry
import co.electriccoin.zcash.configuration.model.entry.ConfigKey

object ConfigurationEntries {
    val IS_APP_UPDATE_CHECK_ENABLED = BooleanConfigurationEntry(ConfigKey("is_update_check_enabled"), true)

    /*
     * The full onboarding flow is functional and tested, but it is disabled by default for an initially minimal feature set.
     */
    val IS_SHORT_ONBOARDING_UX = BooleanConfigurationEntry(ConfigKey("is_short_onboarding_ux"), true)

    /*
     * The full new wallet flow is functional and tested, but it is disabled by default for an initially minimal feature set.
     */
    val IS_SHORT_NEW_WALLET_BACKUP_UX = BooleanConfigurationEntry(ConfigKey("is_short_new_wallet_backup_ux"), true)

    /*
     * This isn't fully implemented yet, so it is disabled from being shown.
     */
    val IS_FIAT_CONVERSION_ENABLED = BooleanConfigurationEntry(ConfigKey("is_fiat_conversion_enabled"), false)

    /*
     * A troubleshooting step. If we fix our bugs, this should be unnecessary.
     */
    val IS_RESCAN_ENABLED = BooleanConfigurationEntry(ConfigKey("is_rescan_enabled"), true)
}
