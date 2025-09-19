@file:OptIn(ExperimentalSerializationApi::class)

package co.electriccoin.zcash.ui.common.model

import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.serialization.BigDecimalSerializer
import co.electriccoin.zcash.ui.common.serialization.InstantSerializer
import co.electriccoin.zcash.ui.common.serialization.SwapProviderSerializer
import co.electriccoin.zcash.ui.common.serialization.ZatoshiSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import java.math.BigDecimal
import java.time.Instant

@JsonIgnoreUnknownKeys
@Serializable
data class Metadata(
    @SerialName("version")
    val version: Int,
    @SerialName("lastUpdated")
    @Serializable(InstantSerializer::class)
    val lastUpdated: Instant,
    @SerialName("accountMetadata")
    val accountMetadata: AccountMetadata
)

@JsonIgnoreUnknownKeys
@Serializable
data class AccountMetadata(
    @SerialName("bookmarked")
    val bookmarked: List<BookmarkMetadata>,
    @SerialName("read")
    val read: List<String>,
    @SerialName("annotations")
    val annotations: List<AnnotationMetadata>,
    @SerialName("swaps")
    val swaps: SwapsMetadata = SwapsMetadata(swapIds = emptyList(), lastUsedAssetHistory = emptySet()),
)

@JsonIgnoreUnknownKeys
@Serializable
data class BookmarkMetadata(
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
data class AnnotationMetadata(
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
data class SwapsMetadata(
    @SerialName("swapIds")
    val swapIds: List<SwapMetadata>,
    @SerialName("lastUsedAssetHistory")
    val lastUsedAssetHistory: Set<String>
)

@JsonIgnoreUnknownKeys
@Serializable
data class SwapMetadata(
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
    @Serializable(SwapProviderSerializer::class)
    val provider: SwapProvider,
)

@JsonIgnoreUnknownKeys
@Serializable
data class SwapProvider(
    val provider: String,
    val token: String,
    val chain: String,
)
