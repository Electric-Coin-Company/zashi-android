package co.electriccoin.zcash.ui.preference

import co.electriccoin.zcash.preference.model.entry.BooleanPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.Key

object StandardPreferenceKeys {

    /**
     * Whether the user has completed the backup flow for a newly created wallet.
     */
    val IS_USER_BACKUP_COMPLETE = BooleanPreferenceDefault(Key("is_user_backup_complete"), false)
}
