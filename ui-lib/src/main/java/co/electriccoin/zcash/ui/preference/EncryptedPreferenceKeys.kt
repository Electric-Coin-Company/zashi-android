package co.electriccoin.zcash.ui.preference

import co.electriccoin.zcash.preference.model.entry.PreferenceKey

object EncryptedPreferenceKeys {

    val PERSISTABLE_WALLET = PersistableWalletPreferenceDefault(PreferenceKey("persistable_wallet"))

    val SYNC_INTERVAL_OPTION = SyncIntervalOptionPreferenceDefault(PreferenceKey("sync_interval_option"))
}
