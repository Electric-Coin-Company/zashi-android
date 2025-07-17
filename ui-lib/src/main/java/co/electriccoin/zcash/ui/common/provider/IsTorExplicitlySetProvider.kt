package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceKey

interface IsTorExplicitlySetProvider : BooleanStorageProvider

class IsTorExplicitlySetProviderImpl(
    override val preferenceHolder: EncryptedPreferenceProvider
) : BaseBooleanStorageProvider(key = PreferenceKey("is_tor_explicitly_set"), default = false),
    IsTorExplicitlySetProvider
