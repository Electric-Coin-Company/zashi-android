@file:OptIn(ExperimentalSerializationApi::class)

package co.electriccoin.zcash.ui.common.model.near

import co.electriccoin.zcash.ui.common.serialization.BigDecimalSerializer
import co.electriccoin.zcash.ui.common.serialization.NearRecipientTypeSerializer
import co.electriccoin.zcash.ui.common.serialization.NearRefundTypeSerializer
import co.electriccoin.zcash.ui.common.serialization.NearSwapTypeSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import java.math.BigDecimal

@JsonIgnoreUnknownKeys
@Serializable
data class QuoteRequest(
    @SerialName("dry")
    val dry: Boolean,
    @SerialName("swapType")
    @Serializable(NearSwapTypeSerializer::class)
    val swapType: SwapType? = null,
    @SerialName("slippageTolerance")
    val slippageTolerance: Int,
    @SerialName("originAsset")
    val originAsset: String,
    @SerialName("depositType")
    @Serializable(NearRefundTypeSerializer::class)
    val depositType: RefundType? = null,
    @SerialName("destinationAsset")
    val destinationAsset: String,
    @SerialName("amount")
    @Serializable(BigDecimalSerializer::class)
    val amount: BigDecimal,
    @SerialName("refundTo")
    val refundTo: String,
    @SerialName("refundType")
    @Serializable(NearRefundTypeSerializer::class)
    val refundType: RefundType? = null,
    @SerialName("recipient")
    val recipient: String,
    @SerialName("recipientType")
    @Serializable(NearRecipientTypeSerializer::class)
    val recipientType: RecipientType? = null,
    @SerialName("deadline")
    val deadline: Instant,
    @SerialName("quoteWaitingTimeMs")
    val quoteWaitingTimeMs: Int? = null,
    @SerialName("appFees")
    val appFees: List<AppFee>,
    @SerialName("referral")
    val referral: String? = null
)

@JsonIgnoreUnknownKeys
@Serializable
data class AppFee(
    @SerialName("recipient")
    val recipient: String,
    @SerialName("fee")
    val fee: Int
)

enum class RefundType(
    val apiValue: String
) {
    ORIGIN_CHAIN("ORIGIN_CHAIN"),
    INTENTS("INTENTS")
}

enum class SwapType(
    val apiValue: String
) {
    EXACT_INPUT("EXACT_INPUT"),
    EXACT_OUTPUT("EXACT_OUTPUT"),
}

enum class RecipientType(
    val apiValue: String
) {
    DESTINATION_CHAIN("DESTINATION_CHAIN"),
    INTENTS("INTENTS")
}
