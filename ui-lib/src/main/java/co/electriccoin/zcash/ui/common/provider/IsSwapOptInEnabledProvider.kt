package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceKey

interface IsSwapOptInEnabledProvider : BooleanStorageProvider

class IsSwapOptInEnabledProviderImpl(
    override val preferenceHolder: EncryptedPreferenceProvider
) : BaseBooleanStorageProvider(key = PreferenceKey("swap_opt_in_enabled"), default = true),
    IsSwapOptInEnabledProvider
