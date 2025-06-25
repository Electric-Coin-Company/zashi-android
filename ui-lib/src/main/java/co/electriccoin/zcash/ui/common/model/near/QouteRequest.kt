package co.electriccoin.zcash.ui.common.model.near

import co.electriccoin.zcash.ui.common.serialization.RefundTypeSerializer
import co.electriccoin.zcash.ui.common.serialization.SwapTypeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QouteRequest(
    @SerialName("dry")
    val dry: Boolean,
    @SerialName("swapType")
    @Serializable(SwapTypeSerializer::class)
    val swapType: SwapType,
    @SerialName("slippageTolerance")
    val slippageTolerance: Int,
    @SerialName("originAsset")
    val originAsset: String,
    @SerialName("depositType")
    @Serializable(RefundTypeSerializer::class)
    val depositType: RefundType,
    @SerialName("destinationAsset")
    val destinationAsset: String,
    @SerialName("amount")
    val amount: String,
    @SerialName("refundTo")
    val refundTo: String,
    @SerialName("refundType")
    @Serializable(RefundTypeSerializer::class)
    val refundType: RefundType,
    @SerialName("recipient")
    val recipient: String,
    @SerialName("recipientType")
    val recipientType: String,
    @SerialName("deadline")
    val deadline: String,
    @SerialName("referral")
    val referral: String,
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
