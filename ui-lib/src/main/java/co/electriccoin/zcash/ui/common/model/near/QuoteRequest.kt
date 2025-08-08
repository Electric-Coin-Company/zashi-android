package co.electriccoin.zcash.ui.common.model.near

import co.electriccoin.zcash.ui.common.serialization.BigDecimalSerializer
import co.electriccoin.zcash.ui.common.serialization.NearRecipientTypeSerializer
import co.electriccoin.zcash.ui.common.serialization.NearRefundTypeSerializer
import co.electriccoin.zcash.ui.common.serialization.NearSwapTypeSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class QuoteRequest(
    @SerialName("dry")
    val dry: Boolean,
    @SerialName("swapType")
    @Serializable(NearSwapTypeSerializer::class)
    val swapType: SwapType?,
    @SerialName("slippageTolerance")
    val slippageTolerance: Int,
    @SerialName("originAsset")
    val originAsset: String,
    @SerialName("depositType")
    @Serializable(NearRefundTypeSerializer::class)
    val depositType: RefundType?,
    @SerialName("destinationAsset")
    val destinationAsset: String,
    @SerialName("amount")
    @Serializable(BigDecimalSerializer::class)
    val amount: BigDecimal,
    @SerialName("refundTo")
    val refundTo: String,
    @SerialName("refundType")
    @Serializable(NearRefundTypeSerializer::class)
    val refundType: RefundType?,
    @SerialName("recipient")
    val recipient: String,
    @SerialName("recipientType")
    @Serializable(NearRecipientTypeSerializer::class)
    val recipientType: RecipientType?,
    @SerialName("deadline")
    val deadline: Instant,
    @SerialName("quoteWaitingTimeMs")
    val quoteWaitingTimeMs: Int,
)

enum class RefundType(val apiValue: String) {
    ORIGIN_CHAIN("ORIGIN_CHAIN"),
    INTENTS("INTENTS")
}

enum class SwapType(val apiValue: String) {
    EXACT_INPUT("EXACT_INPUT"),
    EXACT_OUTPUT("EXACT_OUTPUT"),
}

enum class RecipientType(val apiValue: String) {
    DESTINATION_CHAIN("DESTINATION_CHAIN"),
    INTENTS("INTENTS")
}
