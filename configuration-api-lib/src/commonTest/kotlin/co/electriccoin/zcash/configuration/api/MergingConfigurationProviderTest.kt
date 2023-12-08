package co.electriccoin.zcash.configuration.api

import co.electriccoin.zcash.configuration.model.map.Configuration
import co.electriccoin.zcash.configuration.model.map.StringConfiguration
import co.electriccoin.zcash.configuration.test.fixture.BooleanDefaultEntryFixture
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MergingConfigurationProviderTest {
    @Test
    fun peek_ordering() {
        val configurationProvider =
            MergingConfigurationProvider(
                persistentListOf(
                    MockConfigurationProvider(
                        StringConfiguration(
                            persistentMapOf(BooleanDefaultEntryFixture.KEY.key to true.toString()),
                            null
                        )
                    ),
                    MockConfigurationProvider(
                        StringConfiguration(
                            persistentMapOf(BooleanDefaultEntryFixture.KEY.key to false.toString()),
                            null
                        )
                    )
                )
            )

        assertTrue(BooleanDefaultEntryFixture.newTrueEntry().getValue(configurationProvider.peekConfiguration()))
    }

    @Test
    fun getFlow_ordering() =
        runTest {
            val configurationProvider =
                MergingConfigurationProvider(
                    persistentListOf(
                        MockConfigurationProvider(
                            StringConfiguration(
                                persistentMapOf(BooleanDefaultEntryFixture.KEY.key to true.toString()),
                                null
                            )
                        ),
                        MockConfigurationProvider(
                            StringConfiguration(
                                persistentMapOf(BooleanDefaultEntryFixture.KEY.key to false.toString()),
                                null
                            )
                        )
                    )
                )

            assertTrue(
                BooleanDefaultEntryFixture.newTrueEntry().getValue(configurationProvider.getConfigurationFlow().first())
            )
        }

    @Test
    fun getFlow_empty() =
        runTest {
            val configurationProvider =
                MergingConfigurationProvider(
                    emptyList<ConfigurationProvider>().toPersistentList()
                )

            val firstMergedConfiguration = configurationProvider.getConfigurationFlow().first()

            assertTrue(BooleanDefaultEntryFixture.newTrueEntry().getValue(firstMergedConfiguration))
        }

    @Test
    fun getUpdatedAt_newest() =
        runTest {
            val older = "2023-01-15T08:38:45.415Z".toInstant()
            val newer = "2023-01-17T08:38:45.415Z".toInstant()

            val configurationProvider =
                MergingConfigurationProvider(
                    persistentListOf(
                        MockConfigurationProvider(
                            StringConfiguration(
                                persistentMapOf(BooleanDefaultEntryFixture.KEY.key to true.toString()),
                                older
                            )
                        ),
                        MockConfigurationProvider(
                            StringConfiguration(
                                persistentMapOf(
                                    BooleanDefaultEntryFixture.KEY.key to false.toString()
                                ),
                                newer
                            )
                        )
                    )
                )

            val updatedAt = configurationProvider.getConfigurationFlow().first().updatedAt
            assertEquals(newer, updatedAt)
        }
}

private class MockConfigurationProvider(private val configuration: Configuration) : ConfigurationProvider {
    override fun peekConfiguration(): Configuration {
        return configuration
    }

    override fun getConfigurationFlow(): Flow<Configuration> {
        return flowOf(configuration)
    }

    override fun hintToRefresh() {
        // no-op
    }
}
