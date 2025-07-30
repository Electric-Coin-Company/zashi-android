package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.model.entry.NullableSetPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey

interface NullableSetStorageProvider : NullableStorageProvider<Set<String>>

abstract class BaseNullableSetStorageProvider(
    key: PreferenceKey
) : BaseNullableStorageProvider<Set<String>>(),
    NullableSetStorageProvider {
    override val default = NullableSetPreferenceDefault(key = key)
}
