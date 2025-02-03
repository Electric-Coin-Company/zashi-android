package co.electriccoin.zcash.ui.common.model

import co.electriccoin.zcash.ui.common.serialization.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Metadata(
    @SerialName("version")
    val version: Int,
    @SerialName("lastUpdated")
    @Serializable(InstantSerializer::class)
    val lastUpdated: Instant,
    @SerialName("accountMetadata")
    val accountMetadata: Map<String, AccountMetadata>
)

@Serializable
data class AccountMetadata(
    @SerialName("bookmarked")
    val bookmarked: List<BookmarkMetadata>,
    @SerialName("read")
    val read: List<String>,
    @SerialName("annotations")
    val annotations: List<AnnotationMetadata>
)

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
