@file:OptIn(ExperimentalSerializationApi::class)

package co.electriccoin.zcash.ui.common.model.near

import co.electriccoin.zcash.ui.common.serialization.BigDecimalSerializer
import co.electriccoin.zcash.ui.common.serialization.SwapTypeSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import java.math.BigDecimal

@JsonIgnoreUnknownKeys
@Serializable
data class QuoteResponseDto(
    @SerialName("quoteRequest")
    val quoteRequest: QuoteRequestDetails,
    @SerialName("quote")
    val quote: QuoteDetails
)

@JsonIgnoreUnknownKeys
@Serializable
data class QuoteRequestDetails(
    @SerialName("dry")
    val dry: Boolean,
    @SerialName("swapType")
    @Serializable(with = SwapTypeSerializer::class)
    val swapType: SwapType,
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
    // @SerialName("referral")
    // val referral: String,
    // @SerialName("quoteWaitingTimeMs")
    // val quoteWaitingTimeMs: Int,
    // @SerialName("appFees")
    // val appFees: List<AppFeeDetails>
)

@JsonIgnoreUnknownKeys
@Serializable
data class AppFeeDetails(
    @SerialName("recipient")
    val recipient: String,
    @SerialName("fee")
    val fee: Int
)

@JsonIgnoreUnknownKeys
@Serializable
data class QuoteDetails(
    @SerialName("depositAddress")
    val depositAddress: String,
    @SerialName("amountIn")
    @Serializable(with = BigDecimalSerializer::class)
    val amountIn: BigDecimal,
    @SerialName("amountInFormatted")
    @Serializable(with = BigDecimalSerializer::class)
    val amountInFormatted: BigDecimal,
    @SerialName("amountInUsd")
    @Serializable(with = BigDecimalSerializer::class)
    val amountInUsd: BigDecimal,
    @SerialName("minAmountIn")
    @Serializable(with = BigDecimalSerializer::class)
    val minAmountIn: BigDecimal,
    @SerialName("amountOut")
    @Serializable(with = BigDecimalSerializer::class)
    val amountOut: BigDecimal,
    @SerialName("amountOutFormatted")
    @Serializable(with = BigDecimalSerializer::class)
    val amountOutFormatted: BigDecimal,
    @SerialName("amountOutUsd")
    @Serializable(with = BigDecimalSerializer::class)
    val amountOutUsd: BigDecimal,
    @SerialName("minAmountOut")
    @Serializable(with = BigDecimalSerializer::class)
    val minAmountOut: BigDecimal,
)
