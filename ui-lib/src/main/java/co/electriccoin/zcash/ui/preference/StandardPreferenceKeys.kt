package co.electriccoin.zcash.ui.preference

import co.electriccoin.zcash.preference.model.entry.BooleanPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.IntegerPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.LongPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.NullableBooleanPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import co.electriccoin.zcash.ui.common.model.OnboardingState
import co.electriccoin.zcash.ui.common.model.WalletRestoringState

object StandardPreferenceKeys {
    /**
     * State defining whether the user has completed any of the onboarding wallet states.
     */
    val ONBOARDING_STATE =
        IntegerPreferenceDefault(
            PreferenceKey("onboarding_state"),
            OnboardingState.NONE.toNumber()
        )

    /**
     * State defining whether the current block synchronization run is in the restoring state or a subsequent
     * synchronization state.
     */
    val WALLET_RESTORING_STATE =
        IntegerPreferenceDefault(
            PreferenceKey("wallet_restoring_state"),
            WalletRestoringState.RESTORING.toNumber()
        )

    val IS_ANALYTICS_ENABLED = BooleanPreferenceDefault(PreferenceKey("is_analytics_enabled"), true)

    val IS_BACKGROUND_SYNC_ENABLED = BooleanPreferenceDefault(PreferenceKey("is_background_sync_enabled"), true)

    val IS_KEEP_SCREEN_ON_DURING_SYNC = BooleanPreferenceDefault(PreferenceKey("is_keep_screen_on_during_sync"), true)

    val IS_RESTORING_INITIAL_WARNING_SEEN =
        BooleanPreferenceDefault(PreferenceKey("IS_RESTORING_INITIAL_WARNING_SEEN"), false)

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
    val IS_SEED_AUTHENTICATION =
        BooleanPreferenceDefault(
            PreferenceKey("IS_SEED_AUTHENTICATION"),
            true
        )
    val IS_SEND_FUNDS_AUTHENTICATION =
        BooleanPreferenceDefault(
            PreferenceKey("IS_SEND_FUNDS_AUTHENTICATION"),
            true
        )
    val IS_HIDE_BALANCES =
        BooleanPreferenceDefault(
            PreferenceKey("IS_HIDE_BALANCES"),
            false
        )
    val EXCHANGE_RATE_OPTED_IN =
        NullableBooleanPreferenceDefault(
            PreferenceKey("EXCHANGE_RATE_OPTED_IN"),
            null
        )
    val LATEST_APP_BACKGROUND_TIME_MILLIS =
        LongPreferenceDefault(
            PreferenceKey("LATEST_APP_BACKGROUND_TIME_MILLIS"),
            Long.MAX_VALUE
        )
}
