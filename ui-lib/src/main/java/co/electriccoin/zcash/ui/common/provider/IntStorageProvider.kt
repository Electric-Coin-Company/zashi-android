package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.model.entry.IntegerPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey

interface IntStorageProvider : StorageProvider<Int>

abstract class BaseIntStorageProvider(key: PreferenceKey) : IntStorageProvider, BaseStorageProvider<Int>() {
    override val default = IntegerPreferenceDefault(key = key, defaultValue = 0)
}
