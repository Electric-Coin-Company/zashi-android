package co.electriccoin.zcash.configuration

import co.electriccoin.zcash.configuration.api.ConfigurationProvider
import co.electriccoin.zcash.configuration.api.MergingConfigurationProvider
import co.electriccoin.zcash.configuration.internal.intent.IntentConfigurationProvider
import kotlinx.collections.immutable.toPersistentList

object AndroidConfigurationFactory {
    fun newInstance(): ConfigurationProvider {
        val configurationProviders =
            buildList<ConfigurationProvider> {
                // For ordering, ensure the IntentConfigurationProvider is first so that it can
                // override any other configuration providers.
                if (BuildConfig.DEBUG) {
                    add(IntentConfigurationProvider)
                }

                // In the future, add a third party cloud-based configuration provider
            }

        return MergingConfigurationProvider(configurationProviders.toPersistentList())
    }
}
