package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.model.entry.BooleanPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.NullableBooleanPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey

interface BooleanStorageProvider : StorageProvider<Boolean> {
    suspend fun flip()
}

interface NullableBooleanStorageProvider : NullableStorageProvider<Boolean>

abstract class BaseBooleanStorageProvider(
    key: PreferenceKey,
    default: Boolean = false
) : BaseStorageProvider<Boolean>(),
    BooleanStorageProvider {
    override val default = BooleanPreferenceDefault(key = key, defaultValue = default)

    override suspend fun flip() {
        store(!get())
    }
}

abstract class BaseNullableBooleanStorageProvider(
    key: PreferenceKey
) : BaseNullableStorageProvider<Boolean>(),
    NullableBooleanStorageProvider {
    override val default = NullableBooleanPreferenceDefault(key = key, defaultValue = null)
}
