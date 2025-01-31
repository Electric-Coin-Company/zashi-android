package co.electriccoin.zcash.ui.common.model

import com.google.gson.annotations.SerializedName
import java.time.Instant

data class Metadata(
    @SerializedName("version")
    val version: Int,
    @SerializedName("lastUpdated")
    val lastUpdated: Instant,
    @SerializedName("transactions")
    val transactions: List<TransactionMetadata>
)

data class Note(
    @SerializedName("content")
    val content: String
)

data class TransactionMetadata(
    @SerializedName("txId")
    val txId: String,
    @SerializedName("lastUpdated")
    val lastUpdated: Instant,
    @SerializedName("notes")
    val notes: List<Note>,
    @SerializedName("isMemoRead")
    val isMemoRead: Boolean,
    @SerializedName("isBookmark")
    val isBookmark: Boolean,
)
