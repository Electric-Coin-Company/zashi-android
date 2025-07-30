@file:OptIn(ExperimentalSerializationApi::class)

package co.electriccoin.zcash.ui.common.model.near

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@JsonIgnoreUnknownKeys
@Serializable
data class SwapStatusResponseDto(
    @SerialName("quoteResponse")
    val quoteResponse: QuoteResponseDto,
    @SerialName("status")
    val status: String,
    @SerialName("updatedAt")
    val updatedAt: String,
    @SerialName("swapDetails")
    val swapDetails: SwapDetails
)

@JsonIgnoreUnknownKeys
@Serializable
data class TransactionChainHash(
    @SerialName("hash")
    val hash: String,
    @SerialName("explorerUrl")
    val explorerUrl: String
)

@JsonIgnoreUnknownKeys
@Serializable
data class SwapDetails(
    @SerialName("intentHashes")
    val intentHashes: List<String>,
    @SerialName("nearTxHashes")
    val nearTxHashes: List<String>,
    @SerialName("amountIn")
    val amountIn: String,
    @SerialName("amountInFormatted")
    val amountInFormatted: String,
    @SerialName("amountInUsd")
    val amountInUsd: String,
    @SerialName("amountOut")
    val amountOut: String,
    @SerialName("amountOutFormatted")
    val amountOutFormatted: String,
    @SerialName("amountOutUsd")
    val amountOutUsd: String,
    @SerialName("slippage")
    val slippage: Int,
    @SerialName("originChainTxHashes")
    val originChainTxHashes: List<TransactionChainHash>,
    @SerialName("destinationChainTxHashes")
    val destinationChainTxHashes: List<TransactionChainHash>,
    @SerialName("refundedAmount")
    val refundedAmount: String,
    @SerialName("refundedAmountFormatted")
    val refundedAmountFormatted: String,
    @SerialName("refundedAmountUsd")
    val refundedAmountUsd: String
)
