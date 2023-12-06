package co.electriccoin.zcash.ui.preference

import co.electriccoin.zcash.preference.model.entry.BooleanPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.IntegerPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import co.electriccoin.zcash.ui.common.model.OnboardingState

object StandardPreferenceKeys {

    /**
     * State defining whether the user has completed any of the onboarding wallet states.
     */
    val ONBOARDING_STATE = IntegerPreferenceDefault(
        PreferenceKey("onboarding_state"),
        OnboardingState.NONE.toNumber()
    )

    // Default to true until https://github.com/Electric-Coin-Company/zashi-android/issues/304
    val IS_ANALYTICS_ENABLED = BooleanPreferenceDefault(PreferenceKey("is_analytics_enabled"), true)

    val IS_BACKGROUND_SYNC_ENABLED = BooleanPreferenceDefault(PreferenceKey("is_background_sync_enabled"), true)

    val IS_KEEP_SCREEN_ON_DURING_SYNC = BooleanPreferenceDefault(PreferenceKey("is_keep_screen_on_during_sync"), true)

    /**
     * The fiat currency that the user prefers.
     */
    val PREFERRED_FIAT_CURRENCY = FiatCurrencyPreferenceDefault(PreferenceKey("preferred_fiat_currency_code"))
}
