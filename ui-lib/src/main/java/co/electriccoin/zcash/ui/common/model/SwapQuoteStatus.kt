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
import kotlinx.datetime.toJavaInstant
import java.math.BigDecimal
import java.math.MathContext
import java.time.Instant
import kotlin.time.Duration.Companion.minutes

interface SwapQuoteStatus {
    val swapQuote: SwapQuote

    val timestamp: Instant

    val originAssetId: String
    val destinationAssetId: String

    val status: SwapStatus

    val isSlippageRealized: Boolean
    val maxSlippage: BigDecimal
    val recipient: String
    val mode: SwapMode

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
    val origin: SwapAsset,
    val destination: SwapAsset
) : SwapQuoteStatus {
    override val swapQuote: SwapQuote =
        NearSwapQuote(
            response = response.quoteResponse,
            originAsset = origin,
            destinationAsset = destination,
        )

    override val timestamp: Instant = response.quoteResponse.timestamp.toJavaInstant()

    override val originAssetId: String = origin.assetId
    override val destinationAssetId: String = destination.assetId

    override val status: SwapStatus
        get() =
            if (
                response.status == PENDING_DEPOSIT &&
                Instant.now() > (response.quoteResponse.quote.deadline - 5.minutes).toJavaInstant()
            ) {
                SwapStatus.EXPIRED
            } else {
                when (response.status) {
                    KNOWN_DEPOSIT_TX -> SwapStatus.PENDING
                    PENDING_DEPOSIT -> SwapStatus.PENDING
                    INCOMPLETE_DEPOSIT -> SwapStatus.INCOMPLETE_DEPOSIT
                    PROCESSING -> SwapStatus.PROCESSING
                    SUCCESS -> SwapStatus.SUCCESS
                    REFUNDED -> SwapStatus.REFUNDED
                    FAILED -> SwapStatus.FAILED
                    null -> SwapStatus.PENDING
                }
            }

    override val isSlippageRealized: Boolean = response.swapDetails?.slippage != null

    @Suppress("MagicNumber")
    override val maxSlippage: BigDecimal =
        response.swapDetails
            ?.slippage
            ?.let {
                BigDecimal(it).divide(BigDecimal(100), MathContext.DECIMAL128)
            }
            ?: swapQuote.slippage

    override val recipient: String = swapQuote.recipient

    override val mode: SwapMode = swapQuote.mode

    override val amountIn: BigDecimal = response.swapDetails?.amountIn ?: swapQuote.amountIn

    override val amountInFormatted: BigDecimal = response.swapDetails?.amountInFormatted ?: swapQuote.amountInFormatted

    override val amountInFee: BigDecimal =
        amountInFormatted
            .multiply(
                BigDecimal(AFFILIATE_FEE_BPS).divide(BigDecimal("10000"), MathContext.DECIMAL128),
                MathContext.DECIMAL128
            )

    override val amountInUsd: BigDecimal = response.swapDetails?.amountInUsd ?: swapQuote.amountInUsd

    override val amountOut: BigDecimal = response.swapDetails?.amountOut ?: swapQuote.amountOut

    override val amountOutFormatted: BigDecimal =
        response.swapDetails?.amountOutFormatted ?: swapQuote.amountOutFormatted

    override val amountOutUsd: BigDecimal = response.swapDetails?.amountOutUsd ?: swapQuote.amountOutUsd

    override val refunded: BigDecimal? = response.swapDetails?.refundedAmount

    override val refundedFormatted: BigDecimal? = response.swapDetails?.refundedAmountFormatted

    override val zecExchangeRate: BigDecimal = amountInUsd.divide(amountInFormatted, MathContext.DECIMAL128)
}
