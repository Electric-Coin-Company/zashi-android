package co.electriccoin.zcash.ui.common.repository

import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.configuration.api.ConfigurationProvider
import co.electriccoin.zcash.configuration.model.map.Configuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

interface ConfigurationRepository {
    val configurationFlow: StateFlow<Configuration?>
}

class ConfigurationRepositoryImpl(androidConfigurationProvider: ConfigurationProvider) : ConfigurationRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val configurationFlow: StateFlow<Configuration?> =
        androidConfigurationProvider.getConfigurationFlow()
            .stateIn(
                scope,
                SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT.inWholeMilliseconds),
                null
            )
}
