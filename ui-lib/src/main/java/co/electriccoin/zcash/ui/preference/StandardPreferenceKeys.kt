package co.electriccoin.zcash.ui.preference

import co.electriccoin.zcash.preference.model.entry.BooleanPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.Key

object StandardPreferenceKeys {

    /**
     * Whether the user has completed the backup flow for a newly created wallet.
     */
    val IS_USER_BACKUP_COMPLETE = BooleanPreferenceDefault(Key("is_user_backup_complete"), false)

    // Default to true until https://github.com/zcash/secant-android-wallet/issues/304
    val IS_ANALYTICS_ENABLED = BooleanPreferenceDefault(Key("is_analytics_enabled"), true)

    /**
     * The fiat currency that the user prefers.
     */
    val PREFERRED_FIAT_CURRENCY = FiatCurrencyPreferenceDefault(Key("preferred_fiat_currency_code"))
}
