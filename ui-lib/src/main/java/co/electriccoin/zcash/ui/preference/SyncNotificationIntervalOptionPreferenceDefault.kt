package co.electriccoin.zcash.ui.preference

import co.electriccoin.zcash.preference.api.PreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import co.electriccoin.zcash.ui.screen.syncnotification.viewmodel.SyncNotificationViewModel

data class SyncIntervalOptionPreferenceDefault(
    override val key: PreferenceKey
): PreferenceDefault<SyncNotificationViewModel.SyncIntervalOption> {

    override suspend fun getValue(preferenceProvider: PreferenceProvider): SyncNotificationViewModel.SyncIntervalOption {
        return preferenceProvider.getString(key)?.let { SyncNotificationViewModel.SyncIntervalOption.getSyncIntervalByText(it) } ?: SyncNotificationViewModel.SyncIntervalOption.OFF
    }

    override suspend fun putValue(preferenceProvider: PreferenceProvider, newValue: SyncNotificationViewModel.SyncIntervalOption) {
        preferenceProvider.putString(key, newValue.text)
    }
}
