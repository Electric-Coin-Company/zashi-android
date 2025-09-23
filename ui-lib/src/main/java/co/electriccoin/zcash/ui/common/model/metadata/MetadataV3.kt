@file:OptIn(ExperimentalSerializationApi::class)

package co.electriccoin.zcash.ui.common.model.metadata

import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.model.SwapStatus
import co.electriccoin.zcash.ui.common.serialization.BigDecimalSerializer
import co.electriccoin.zcash.ui.common.serialization.InstantSerializer
import co.electriccoin.zcash.ui.common.serialization.METADATA_SERIALIZATION_V3
import co.electriccoin.zcash.ui.common.serialization.ZatoshiSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import java.math.BigDecimal
import java.time.Instant

@JsonIgnoreUnknownKeys
@Serializable
data class MetadataV3(
    @SerialName("version")
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    val version: Int = METADATA_SERIALIZATION_V3,
    @SerialName("lastUpdated")
    @Serializable(InstantSerializer::class)
    val lastUpdated: Instant,
    @SerialName("accountMetadata")
    val accountMetadata: AccountMetadataV3
)

@JsonIgnoreUnknownKeys
@Serializable
data class AccountMetadataV3(
    @SerialName("bookmarked")
    val bookmarked: List<BookmarkMetadataV3>,
    @SerialName("read")
    val read: List<String>,
    @SerialName("annotations")
    val annotations: List<AnnotationMetadataV3>,
    @SerialName("swaps")
    val swaps: SwapsMetadataV3 = SwapsMetadataV3(swapIds = emptyList(), lastUsedAssetHistory = emptySet()),
)

@JsonIgnoreUnknownKeys
@Serializable
data class BookmarkMetadataV3(
    @SerialName("txId")
    val txId: String,
    @SerialName("lastUpdated")
    @Serializable(InstantSerializer::class)
    val lastUpdated: Instant,
    @SerialName("isBookmarked")
    val isBookmarked: Boolean
)

@JsonIgnoreUnknownKeys
@Serializable
data class AnnotationMetadataV3(
    @SerialName("txId")
    val txId: String,
    @SerialName("content")
    val content: String?,
    @SerialName("lastUpdated")
    @Serializable(InstantSerializer::class)
    val lastUpdated: Instant,
)

@JsonIgnoreUnknownKeys
@Serializable
data class SwapsMetadataV3(
    @SerialName("swapIds")
    val swapIds: List<SwapMetadataV3>,
    @SerialName("lastUsedAssetHistory")
    val lastUsedAssetHistory: Set<String>
)

@JsonIgnoreUnknownKeys
@Serializable
data class SwapMetadataV3(
    @SerialName("depositAddress")
    val depositAddress: String,
    @SerialName("provider")
    val provider: String,
    @SerialName("totalFees")
    @Serializable(ZatoshiSerializer::class)
    val totalFees: Zatoshi,
    @SerialName("totalFeesUsd")
    @Serializable(BigDecimalSerializer::class)
    val totalFeesUsd: BigDecimal,
    @SerialName("lastUpdated")
    @Serializable(InstantSerializer::class)
    val lastUpdated: Instant,
    @SerialName("fromAsset")
    @Serializable(MetadataSimpleSwapAssetV3Serializer::class)
    val fromAsset: MetadataSimpleSwapAssetV3,
    @SerialName("toAsset")
    @Serializable(MetadataSimpleSwapAssetV3Serializer::class)
    val toAsset: MetadataSimpleSwapAssetV3,
    @SerialName("exactInput")
    val exactInput: Boolean?,
    @SerialName("status")
    val status: SwapStatus?,
    @Serializable(BigDecimalSerializer::class)
    @SerialName("amountOutFormatted")
    val amountOutFormatted: BigDecimal?,
)

data class MetadataSimpleSwapAssetV3(val token: String, val chain: String)
