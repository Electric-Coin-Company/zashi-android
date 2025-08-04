package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceKey

interface IsExchangeRateEnabledStorageProvider : NullableBooleanStorageProvider

class IsExchangeRateEnabledStorageProviderImpl(
    override val preferenceHolder: StandardPreferenceProvider,
) : BaseNullableBooleanStorageProvider(
        key = PreferenceKey("EXCHANGE_RATE_OPTED_IN"),
    ),
    IsExchangeRateEnabledStorageProvider
