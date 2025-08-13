package co.electriccoin.zcash.ui.common.model

import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.model.Zatoshi
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
import java.math.BigDecimal
import java.math.MathContext

interface SwapQuoteStatus {
    val destinationAssetId: String

    val status: SwapStatus

    val isSlippageRealized: Boolean
    val maxSlippage: BigDecimal
    val recipient: String
    val swapMode: SwapMode?

    val amountIn: BigDecimal
    val amountInFormatted: BigDecimal
    val amountInUsd: BigDecimal

    val amountOut: BigDecimal
    val amountOutFormatted: BigDecimal
    val amountOutUsd: BigDecimal

    val amountInZatoshi: Zatoshi

    val refunded: BigDecimal?
    val refundedFormatted: BigDecimal?

    val zecExchangeRate: BigDecimal
    val swapProviderFee: Zatoshi
    val swapProviderFeeUsd: BigDecimal
}

data class NearSwapQuoteStatus(
    val response: SwapStatusResponseDto,
) : SwapQuoteStatus {
    override val destinationAssetId: String = response.quoteResponse.quoteRequest.destinationAsset

    override val status: SwapStatus = when (response.status) {
        KNOWN_DEPOSIT_TX -> SwapStatus.PENDING
        PENDING_DEPOSIT -> SwapStatus.PENDING
        INCOMPLETE_DEPOSIT -> SwapStatus.INCOMPLETE_DEPOSIT
        PROCESSING -> SwapStatus.PENDING
        SUCCESS -> SwapStatus.SUCCESS
        REFUNDED -> SwapStatus.REFUNDED
        FAILED -> SwapStatus.FAILED
        null -> SwapStatus.PENDING
    }
    override val isSlippageRealized: Boolean = response.swapDetails?.slippage != null
    override val maxSlippage: BigDecimal = (response.swapDetails?.slippage
        ?: response.quoteResponse.quoteRequest.slippageTolerance
        ).let { BigDecimal(it).divide(BigDecimal(100)) }

    override val recipient: String = response.quoteResponse.quoteRequest.recipient

    override val swapMode: SwapMode = when (response.quoteResponse.quoteRequest.swapType) {
        EXACT_INPUT -> SwapMode.EXACT_INPUT
        EXACT_OUTPUT -> SwapMode.EXACT_OUTPUT
        null -> SwapMode.EXACT_INPUT
    }
    override val amountIn: BigDecimal = response.swapDetails?.amountIn
        ?: response.quoteResponse.quote.amountIn

    override val amountInFormatted: BigDecimal = response.swapDetails?.amountInFormatted
        ?: response.quoteResponse.quote.amountInFormatted

    override val amountInUsd: BigDecimal = response.swapDetails?.amountInUsd
        ?: response.quoteResponse.quote.amountInUsd

    override val amountOut: BigDecimal = response.swapDetails?.amountOut
        ?: response.quoteResponse.quote.amountOut

    override val amountOutFormatted: BigDecimal = response.swapDetails?.amountOutFormatted
        ?: response.quoteResponse.quote.amountOutFormatted

    override val amountOutUsd: BigDecimal = response.swapDetails?.amountOutUsd
        ?: response.quoteResponse.quote.amountOutUsd

    override val amountInZatoshi: Zatoshi = Zatoshi(amountIn.toLong())

    override val refunded: BigDecimal? = response.swapDetails?.refundedAmount

    override val refundedFormatted: BigDecimal? = response.swapDetails?.refundedAmountFormatted

    override val zecExchangeRate: BigDecimal = amountInUsd.divide(amountInFormatted, MathContext.DECIMAL128)

    override val swapProviderFee: Zatoshi =
        (amountInUsd - amountOutUsd).coerceAtLeast(BigDecimal(0))
            .divide(zecExchangeRate, MathContext.DECIMAL128)
            .convertZecToZatoshi()

    override val swapProviderFeeUsd: BigDecimal = (amountInUsd - amountOutUsd).coerceAtLeast(BigDecimal(0))

    // fun getZecFeeUsd(proposal: Proposal): BigDecimal =
    //     zecExchangeRate.multiply(getZecFee(proposal), MathContext.DECIMAL128)

    // private fun getZecFee(proposal: Proposal): BigDecimal = proposal.totalFeeRequired().convertZatoshiToZec()
}
