package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

interface IsExchangeRateEnabledStorageProvider : NullableBooleanStorageProvider

class IsExchangeRateEnabledStorageProviderImpl(
    preferenceHolder: StandardPreferenceProvider,
) : IsExchangeRateEnabledStorageProvider {
    private val newProvider = NewIsExchangeRateEnabledStorageProvider(preferenceHolder)
    private val legacyProvider = LegacyIsExchangeRateEnabledStorageProvider(preferenceHolder)

    override suspend fun get(): Boolean? {
        legacyProvider.clear()
        return newProvider.get()
    }

    override suspend fun store(amount: Boolean) {
        legacyProvider.clear()
        newProvider.store(amount)
    }

    override fun observe(): Flow<Boolean?> =
        channelFlow {
            launch {
                newProvider.observe().collect { send(it) }
            }

            launch {
                legacyProvider.clear()
            }

            awaitClose()
        }

    override suspend fun clear() {
        legacyProvider.clear()
        newProvider.clear()
    }
}

private class NewIsExchangeRateEnabledStorageProvider(
    override val preferenceHolder: StandardPreferenceProvider,
) : BaseNullableBooleanStorageProvider(key = PreferenceKey("EXCHANGE_RATE_OPTED_IN_2")),
    IsExchangeRateEnabledStorageProvider

private class LegacyIsExchangeRateEnabledStorageProvider(
    override val preferenceHolder: StandardPreferenceProvider,
) : BaseNullableBooleanStorageProvider(key = PreferenceKey("EXCHANGE_RATE_OPTED_IN")),
    IsExchangeRateEnabledStorageProvider
