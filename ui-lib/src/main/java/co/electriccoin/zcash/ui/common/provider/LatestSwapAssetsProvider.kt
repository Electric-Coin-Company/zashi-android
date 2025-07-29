package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface LatestSwapAssetsProvider {
    fun observe(): Flow<Set<SimpleSwapAsset>?>

    suspend fun get(): Set<SimpleSwapAsset>?

    suspend fun add(tokenTicker: String, chainTicker: String)
}

data class SimpleSwapAsset(
    val tokenTicker: String,
    val chainTicker: String
)

class LatestSwapAssetsProviderImpl(
    encryptedPreferenceProvider: EncryptedPreferenceProvider
) : LatestSwapAssetsProvider {
    private val storage = LatestSwapAssetStorageProviderImpl(encryptedPreferenceProvider)

    override fun observe(): Flow<Set<SimpleSwapAsset>?> = storage.observe().map { it?.toSimpleAssetSet() }

    override suspend fun get(): Set<SimpleSwapAsset>? = storage.get()?.toSimpleAssetSet()

    @Suppress("MagicNumber")
    override suspend fun add(tokenTicker: String, chainTicker: String) {
        val current = storage.get()?.toSimpleAssetSet().orEmpty()
        val newAsset =
            SimpleSwapAsset(
                tokenTicker = tokenTicker.lowercase(),
                chainTicker = chainTicker.lowercase()
            )

        val newList = current.toMutableList()
        if (newList.contains(newAsset)) newList.remove(newAsset)
        newList.add(0, newAsset)
        val finalSet =
            newList
                .take(10)
                .mapIndexed { index, asset ->
                    "$index:${asset.tokenTicker}:${asset.chainTicker}"
                }.toSet()
        storage.store(finalSet)
    }

    private fun Set<String>.toSimpleAssetSet() =
        this
            .map {
                val data = it.split(":")
                val order = data[0]
                order to
                    SimpleSwapAsset(
                        tokenTicker = data[1],
                        chainTicker = data[2]
                    )
            }.sortedBy { (order, _) -> order }
            .map { (_, asset) -> asset }
            .toSet()
}

private class LatestSwapAssetStorageProviderImpl(
    override val preferenceHolder: EncryptedPreferenceProvider,
) : BaseNullableSetStorageProvider(key = PreferenceKey("latest_swap_assets"))
