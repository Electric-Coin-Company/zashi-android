package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.model.entry.BooleanPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey

interface BooleanStorageProvider : StorageProvider<Boolean>

abstract class BaseBooleanStorageProvider(
    key: PreferenceKey
) : BaseStorageProvider<Boolean>(),
    BooleanStorageProvider {
    override val default = BooleanPreferenceDefault(key = key, defaultValue = false)
}
