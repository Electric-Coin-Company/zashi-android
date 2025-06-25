package co.electriccoin.zcash.ui.common.model.near

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuoteResponseDto(
    @SerialName("timestamp")
    val timestamp: String,

    @SerialName("signature")
    val signature: String,

    @SerialName("quoteRequest")
    val quoteRequest: QuoteRequestDetails,

    @SerialName("quote")
    val quote: QuoteDetails
)

@Serializable
data class QuoteRequestDetails(
    @SerialName("dry")
    val dry: Boolean,

    @SerialName("swapType")
    val swapType: String,

    @SerialName("slippageTolerance")
    val slippageTolerance: Int,

    @SerialName("originAsset")
    val originAsset: String,

    @SerialName("depositType")
    val depositType: String,

    @SerialName("destinationAsset")
    val destinationAsset: String,

    @SerialName("amount")
    val amount: String,

    @SerialName("refundTo")
    val refundTo: String,

    @SerialName("refundType")
    val refundType: String,

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

    @SerialName("appFees")
    val appFees: List<AppFeeDetails>
)

@Serializable
data class AppFeeDetails(
    @SerialName("recipient")
    val recipient: String,

    @SerialName("fee")
    val fee: Int
)

@Serializable
data class QuoteDetails(
    @SerialName("depositAddress")
    val depositAddress: String,

    @SerialName("amountIn")
    val amountIn: String,

    @SerialName("amountInFormatted")
    val amountInFormatted: String,

    @SerialName("amountInUsd")
    val amountInUsd: String,

    @SerialName("minAmountIn")
    val minAmountIn: String,

    @SerialName("amountOut")
    val amountOut: String,

    @SerialName("amountOutFormatted")
    val amountOutFormatted: String,

    @SerialName("amountOutUsd")
    val amountOutUsd: String,

    @SerialName("minAmountOut")
    val minAmountOut: String,

    @SerialName("deadline")
    val deadline: String,

    @SerialName("timeWhenInactive")
    val timeWhenInactive: String,

    @SerialName("timeEstimate")
    val timeEstimate: Int
)