package co.electriccoin.zcash.ui.common.model

import co.electriccoin.zcash.ui.common.datasource.AFFILIATE_FEE_BPS
import co.electriccoin.zcash.ui.common.model.near.SwapStatus.FAILED
import co.electriccoin.zcash.ui.common.model.near.SwapStatus.INCOMPLETE_DEPOSIT
import co.electriccoin.zcash.ui.common.model.near.SwapStatus.KNOWN_DEPOSIT_TX
import co.electriccoin.zcash.ui.common.model.near.SwapStatus.PENDING_DEPOSIT
import co.electriccoin.zcash.ui.common.model.near.SwapStatus.PROCESSING
import co.electriccoin.zcash.ui.common.model.near.SwapStatus.REFUNDED
import co.electriccoin.zcash.ui.common.model.near.SwapStatus.SUCCESS
import co.electriccoin.zcash.ui.common.model.near.SwapStatusResponseDto
import co.electriccoin.zcash.ui.common.model.near.SwapType.EXACT_INPUT
import co.electriccoin.zcash.ui.common.model.near.SwapType.EXACT_OUTPUT
import kotlinx.datetime.toJavaInstant
import java.math.BigDecimal
import java.math.MathContext
import java.time.Instant

interface SwapQuoteStatus {

    val timestamp: Instant

    val originAssetId: String
    val destinationAssetId: String

    val status: SwapStatus

    val isSlippageRealized: Boolean
    val maxSlippage: BigDecimal
    val recipient: String
    val swapMode: SwapMode?

    val amountInFee: BigDecimal
    val amountIn: BigDecimal
    val amountInFormatted: BigDecimal
    val amountInUsd: BigDecimal

    val amountOut: BigDecimal
    val amountOutFormatted: BigDecimal
    val amountOutUsd: BigDecimal

    val refunded: BigDecimal?
    val refundedFormatted: BigDecimal?

    val zecExchangeRate: BigDecimal
}

data class NearSwapQuoteStatus(
    val response: SwapStatusResponseDto,
) : SwapQuoteStatus {

    override val timestamp: Instant = response.quoteResponse.timestamp.toJavaInstant()

    override val originAssetId: String = response.quoteResponse.quoteRequest.originAsset
    override val destinationAssetId: String = response.quoteResponse.quoteRequest.destinationAsset

    override val status: SwapStatus =
        when (response.status) {
            KNOWN_DEPOSIT_TX -> SwapStatus.PENDING
            PENDING_DEPOSIT -> SwapStatus.PENDING
            INCOMPLETE_DEPOSIT -> SwapStatus.PENDING
            PROCESSING -> SwapStatus.PROCESSING
            SUCCESS -> SwapStatus.SUCCESS
            REFUNDED -> SwapStatus.REFUNDED
            FAILED -> SwapStatus.FAILED
            null -> SwapStatus.PENDING
        }
    override val isSlippageRealized: Boolean = response.swapDetails?.slippage != null

    @Suppress("MagicNumber")
    override val maxSlippage: BigDecimal =
        (
            response.swapDetails?.slippage
                ?: response.quoteResponse.quoteRequest.slippageTolerance
        ).let { BigDecimal(it).divide(BigDecimal(100)) }

    override val recipient: String = response.quoteResponse.quoteRequest.recipient

    override val swapMode: SwapMode =
        when (response.quoteResponse.quoteRequest.swapType) {
            EXACT_INPUT -> SwapMode.EXACT_INPUT
            EXACT_OUTPUT -> SwapMode.EXACT_OUTPUT
            null -> SwapMode.EXACT_INPUT
        }
    override val amountIn: BigDecimal =
        response.swapDetails?.amountIn
            ?: response.quoteResponse.quote.amountIn

    override val amountInFormatted: BigDecimal =
        response.swapDetails?.amountInFormatted
            ?: response.quoteResponse.quote.amountInFormatted

    override val amountInFee: BigDecimal = amountInFormatted
        .multiply(
            BigDecimal(AFFILIATE_FEE_BPS).divide(BigDecimal("10000"), MathContext.DECIMAL128),
            MathContext.DECIMAL128
        )

    override val amountInUsd: BigDecimal =
        response.swapDetails?.amountInUsd
            ?: response.quoteResponse.quote.amountInUsd

    override val amountOut: BigDecimal =
        response.swapDetails?.amountOut
            ?: response.quoteResponse.quote.amountOut

    override val amountOutFormatted: BigDecimal =
        response.swapDetails?.amountOutFormatted
            ?: response.quoteResponse.quote.amountOutFormatted

    override val amountOutUsd: BigDecimal =
        response.swapDetails?.amountOutUsd
            ?: response.quoteResponse.quote.amountOutUsd

    override val refunded: BigDecimal? = response.swapDetails?.refundedAmount

    override val refundedFormatted: BigDecimal? = response.swapDetails?.refundedAmountFormatted

    override val zecExchangeRate: BigDecimal = amountInUsd.divide(amountInFormatted, MathContext.DECIMAL128)

}
