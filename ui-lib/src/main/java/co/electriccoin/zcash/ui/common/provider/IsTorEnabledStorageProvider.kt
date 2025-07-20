package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceKey

interface IsTorEnabledStorageProvider : NullableBooleanStorageProvider

class IsTorEnabledStorageProviderImpl(
    override val preferenceHolder: EncryptedPreferenceProvider,
) : BaseNullableBooleanStorageProvider(
        key = PreferenceKey("is_tor_Enabled"),
    ), IsTorEnabledStorageProvider
