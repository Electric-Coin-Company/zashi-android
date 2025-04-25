package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceKey

interface ShieldFundsInfoProvider : BooleanStorageProvider

class ShieldFundsInfoProviderImpl(
    override val preferenceHolder: EncryptedPreferenceProvider
) : BaseBooleanStorageProvider(key = PreferenceKey("shield_funds_info"), default = true),
    ShieldFundsInfoProvider
