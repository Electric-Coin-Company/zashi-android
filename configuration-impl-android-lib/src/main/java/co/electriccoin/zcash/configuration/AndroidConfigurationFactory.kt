package co.electriccoin.zcash.configuration

import android.content.Context
import co.electriccoin.zcash.configuration.api.ConfigurationProvider
import co.electriccoin.zcash.configuration.api.MergingConfigurationProvider
import co.electriccoin.zcash.configuration.internal.intent.IntentConfigurationProvider
import co.electriccoin.zcash.spackle.LazyWithArgument
import kotlinx.collections.immutable.toPersistentList

object AndroidConfigurationFactory {

    private val instance = LazyWithArgument<Context, ConfigurationProvider> { context ->
        new(context)
    }

    fun getInstance(context: Context): ConfigurationProvider = instance.getInstance(context)

    // Context will be needed for most cloud providers, e.g. to integrate with Firebase or other
    // remote configuration providers.
    private fun new(@Suppress("UNUSED_PARAMETER") context: Context): ConfigurationProvider {
        val configurationProviders = buildList<ConfigurationProvider> {
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
