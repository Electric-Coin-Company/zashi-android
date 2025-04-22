package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import co.electriccoin.zcash.preference.model.entry.TimestampPreferenceDefault
import java.time.Instant

interface TimestampStorageProvider : NullableStorageProvider<Instant>

abstract class BaseTimestampStorageProvider(
    key: PreferenceKey
) : BaseNullableStorageProvider<Instant>(),
    TimestampStorageProvider {
    override val default = TimestampPreferenceDefault(key)
}
