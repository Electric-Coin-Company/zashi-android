package co.electriccoin.zcash.ui.preference

import co.electriccoin.zcash.preference.model.entry.BooleanPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.IntegerPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.LongPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import co.electriccoin.zcash.ui.common.model.OnboardingState

object StandardPreferenceKeys {
    /**
     * State defining whether the user has completed any of the onboarding wallet states.
     */
    val ONBOARDING_STATE =
        IntegerPreferenceDefault(
            PreferenceKey("onboarding_state"),
            OnboardingState.NONE.toNumber()
        )

    val IS_BACKGROUND_SYNC_ENABLED = BooleanPreferenceDefault(PreferenceKey("is_background_sync_enabled"), true)

    /**
     * Screens or flows protected by required authentication
     */
    val IS_APP_ACCESS_AUTHENTICATION =
        BooleanPreferenceDefault(
            PreferenceKey("IS_APP_ACCESS_AUTHENTICATION"),
            true
        )
    val IS_DELETE_WALLET_AUTHENTICATION =
        BooleanPreferenceDefault(
            PreferenceKey("IS_DELETE_WALLET_AUTHENTICATION"),
            true
        )
    val IS_EXPORT_PRIVATE_DATA_AUTHENTICATION =
        BooleanPreferenceDefault(
            PreferenceKey("IS_EXPORT_PRIVATE_DATA_AUTHENTICATION"),
            true
        )
    val IS_HIDE_BALANCES =
        BooleanPreferenceDefault(
            PreferenceKey("IS_HIDE_BALANCES"),
            false
        )
    val LATEST_APP_BACKGROUND_TIME_MILLIS =
        LongPreferenceDefault(
            PreferenceKey("LATEST_APP_BACKGROUND_TIME_MILLIS"),
            Long.MAX_VALUE
        )
}
