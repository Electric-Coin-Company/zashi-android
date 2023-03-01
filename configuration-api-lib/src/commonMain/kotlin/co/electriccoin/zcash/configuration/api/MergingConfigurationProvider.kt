package co.electriccoin.zcash.configuration.api

import co.electriccoin.zcash.configuration.model.entry.ConfigKey
import co.electriccoin.zcash.configuration.model.map.Configuration
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Instant

class MergingConfigurationProvider(private val configurationProviders: PersistentList<ConfigurationProvider>) : ConfigurationProvider {
    override fun peekConfiguration(): Configuration {
        return MergingConfiguration(configurationProviders.map { it.peekConfiguration() }.toPersistentList())
    }

    override fun getConfigurationFlow(): Flow<Configuration> {
        return if (configurationProviders.isEmpty()) {
            flowOf(MergingConfiguration(persistentListOf<Configuration>()))
        } else {
            combine(configurationProviders.map { it.getConfigurationFlow() }) { configurations ->
                MergingConfiguration(configurations.toList().toPersistentList())
            }
        }
    }

    override fun hintToRefresh() {
        configurationProviders.forEach { it.hintToRefresh() }
    }
}

private data class MergingConfiguration(private val configurations: PersistentList<Configuration>) : Configuration {
    override val updatedAt: Instant?
        get() = configurations.mapNotNull { it.updatedAt }.maxOrNull()

    override fun hasKey(key: ConfigKey): Boolean {
        return null != configurations.firstWithKey(key)
    }

    override fun getBoolean(key: ConfigKey, defaultValue: Boolean): Boolean {
        return configurations.firstWithKey(key)?.let {
            return it.getBoolean(key, defaultValue)
        } ?: defaultValue
    }

    override fun getInt(key: ConfigKey, defaultValue: Int): Int {
        return configurations.firstWithKey(key)?.let {
            return it.getInt(key, defaultValue)
        } ?: defaultValue
    }

    override fun getString(key: ConfigKey, defaultValue: String): String {
        return configurations.firstWithKey(key)?.let {
            return it.getString(key, defaultValue)
        } ?: defaultValue
    }
}

private fun List<Configuration>.firstWithKey(key: ConfigKey): Configuration? = firstOrNull { it.hasKey(key) }
