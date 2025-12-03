package co.electriccoin.zcash.ui.common.serialization.metadata

import co.electriccoin.zcash.ui.common.model.metadata.AccountMetadataV3
import co.electriccoin.zcash.ui.common.model.metadata.AnnotationMetadataV3
import co.electriccoin.zcash.ui.common.model.metadata.BookmarkMetadataV3
import co.electriccoin.zcash.ui.common.model.metadata.MetadataSimpleSwapAssetV3
import co.electriccoin.zcash.ui.common.model.metadata.MetadataV3
import co.electriccoin.zcash.ui.common.model.metadata.SwapMetadataV3
import co.electriccoin.zcash.ui.common.model.metadata.SwapsMetadataV3
import co.electriccoin.zcash.ui.common.model.metadata.v2.MetadataV2
import co.electriccoin.zcash.ui.common.serialization.METADATA_SERIALIZATION_V1_V2
import co.electriccoin.zcash.ui.common.serialization.METADATA_SERIALIZATION_V3
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToStream
import kotlinx.serialization.json.int
import java.io.InputStream
import java.io.OutputStream

class MetadataSerializer {
    @OptIn(ExperimentalSerializationApi::class)
    fun serialize(outputStream: OutputStream, metadata: MetadataV3) {
        Json.encodeToStream(metadata, outputStream)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun deserialize(inputStream: InputStream): MetadataV3 {
        val json = inputStream.reader().readText()
        val jsonElement = Json.parseToJsonElement(json)
        require(jsonElement is JsonObject)
        val versionField = jsonElement.firstNotNullOf { (key, value) -> if (key == "version") value else null }
        require(versionField is JsonPrimitive)

        return when (versionField.int) {
            METADATA_SERIALIZATION_V1_V2 -> migrateV2ToV3(json)
            METADATA_SERIALIZATION_V3 -> Json.decodeFromString(json)
            else -> throw IllegalArgumentException("Unknown metadata version: ${versionField.int}")
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun migrateV2ToV3(json: String): MetadataV3 {
        val old = Json.decodeFromString<MetadataV2>(json)
        return MetadataV3(
            lastUpdated = old.lastUpdated,
            accountMetadata =
                AccountMetadataV3(
                    bookmarked =
                        old.accountMetadata.bookmarked.map {
                            BookmarkMetadataV3(
                                txId = it.txId,
                                lastUpdated = it.lastUpdated,
                                isBookmarked = it.isBookmarked
                            )
                        },
                    read = old.accountMetadata.read,
                    annotations =
                        old.accountMetadata.annotations.map {
                            AnnotationMetadataV3(
                                txId = it.txId,
                                content = it.content,
                                lastUpdated = it.lastUpdated
                            )
                        },
                    swaps =
                        SwapsMetadataV3(
                            swapIds =
                                old.accountMetadata.swaps.swapIds.map {
                                    SwapMetadataV3(
                                        depositAddress = it.depositAddress,
                                        provider = it.provider.provider,
                                        totalFees = it.totalFees,
                                        totalUSDFeesInternal = it.totalFeesUsd,
                                        lastUpdated = it.lastUpdated,
                                        fromAsset = MetadataSimpleSwapAssetV3(token = "zec", chain = "zec"),
                                        toAsset =
                                            MetadataSimpleSwapAssetV3(
                                                token = it.provider.token,
                                                chain = it.provider.chain,
                                            ),
                                        exactInput = null,
                                        status = null,
                                        amountOutFormatted = null
                                    )
                                },
                            lastUsedAssetHistory = old.accountMetadata.swaps.lastUsedAssetHistory
                        )
                )
        )
    }
}
