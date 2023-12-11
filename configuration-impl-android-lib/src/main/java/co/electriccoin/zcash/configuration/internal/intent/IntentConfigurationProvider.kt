package co.electriccoin.zcash.configuration.internal.intent

import co.electriccoin.zcash.configuration.api.ConfigurationProvider
import co.electriccoin.zcash.configuration.model.map.Configuration
import co.electriccoin.zcash.configuration.model.map.StringConfiguration
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal object IntentConfigurationProvider : ConfigurationProvider {
    private val configurationStateFlow = MutableStateFlow(StringConfiguration(persistentMapOf(), null))

    override fun peekConfiguration() = configurationStateFlow.value

    override fun getConfigurationFlow(): Flow<Configuration> = configurationStateFlow

    override fun hintToRefresh() {
        // Do nothing
    }

    /**
     * Sets the configuration to the provided value.
     *
     * @see IntentConfigurationProvider
     */
    internal fun setConfiguration(configuration: StringConfiguration) {
        configurationStateFlow.value = configuration
    }
}
