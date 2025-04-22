package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceKey

interface RestoreTimestampStorageProvider : TimestampStorageProvider

class RestoreTimestampStorageProviderImpl(
    override val preferenceHolder: EncryptedPreferenceProvider
) : BaseTimestampStorageProvider(PreferenceKey("restore_timestamp")),
    RestoreTimestampStorageProvider
