package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceKey

interface IsKeepScreenOnDuringRestoreProvider : NullableBooleanStorageProvider

class IsKeepScreenOnDuringRestoreProviderImpl(
    override val preferenceHolder: StandardPreferenceProvider,
) : BaseNullableBooleanStorageProvider(
        key = PreferenceKey("is_keep_screen_on_during_sync"),
    ),
    IsKeepScreenOnDuringRestoreProvider
