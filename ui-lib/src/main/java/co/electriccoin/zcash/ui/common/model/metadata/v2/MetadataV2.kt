@file:OptIn(ExperimentalSerializationApi::class)

package co.electriccoin.zcash.ui.common.model.metadata.v2

import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.serialization.BigDecimalSerializer
import co.electriccoin.zcash.ui.common.serialization.InstantSerializer
import co.electriccoin.zcash.ui.common.serialization.METADATA_SERIALIZATION_V1_V2
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
data class MetadataV2(
    @SerialName("version")
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    val version: Int = METADATA_SERIALIZATION_V1_V2,
    @SerialName("lastUpdated")
    @Serializable(InstantSerializer::class)
    val lastUpdated: Instant,
    @SerialName("accountMetadata")
    val accountMetadata: AccountMetadataV2
)

@JsonIgnoreUnknownKeys
@Serializable
data class BookmarkMetadataV2(
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
data class AnnotationMetadataV2(
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
data class AccountMetadataV2(
    @SerialName("bookmarked")
    val bookmarked: List<BookmarkMetadataV2>,
    @SerialName("read")
    val read: List<String>,
    @SerialName("annotations")
    val annotations: List<AnnotationMetadataV2>,
    @SerialName("swaps")
    val swaps: SwapsMetadataV2 = SwapsMetadataV2(swapIds = emptyList(), lastUsedAssetHistory = emptySet()),
)

@JsonIgnoreUnknownKeys
@Serializable
data class SwapsMetadataV2(
    @SerialName("swapIds")
    val swapIds: List<SwapMetadataV2>,
    @SerialName("lastUsedAssetHistory")
    val lastUsedAssetHistory: Set<String>
)

@JsonIgnoreUnknownKeys
@Serializable
data class SwapMetadataV2(
    @SerialName("depositAddress")
    val depositAddress: String,
    @SerialName("lastUpdated")
    @Serializable(InstantSerializer::class)
    val lastUpdated: Instant,
    @SerialName("totalFees")
    @Serializable(ZatoshiSerializer::class)
    val totalFees: Zatoshi,
    @SerialName("totalFeesUsd")
    @Serializable(BigDecimalSerializer::class)
    val totalFeesUsd: BigDecimal,
    @SerialName("provider")
    @Serializable(SwapProviderV2Serializer::class)
    val provider: SwapProviderV2,
)

@JsonIgnoreUnknownKeys
@Serializable
data class SwapProviderV2(
    val provider: String,
    val token: String,
    val chain: String,
)
