package co.electriccoin.zcash.ui.common.model

import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.datasource.AFFILIATE_FEE_BPS
import co.electriccoin.zcash.ui.common.model.near.QuoteResponseDto
import co.electriccoin.zcash.ui.common.model.near.SwapType.EXACT_INPUT
import co.electriccoin.zcash.ui.common.model.near.SwapType.EXACT_OUTPUT
import kotlinx.datetime.Instant
import java.math.BigDecimal
import java.math.MathContext

sealed interface SwapQuote {
    val destinationAmount: Zatoshi
    val depositAddress: String
    val origin: String
    val destinationAssetTicker: String
    val destinationAddress: String
    val provider: String
    val type: SwapMode
    val amountInUsd: BigDecimal
    val amountOutUsd: BigDecimal
    val zecExchangeRate: BigDecimal

    // val swapProviderFee: Zatoshi
    // val swapProviderFeeUsd: BigDecimal
    val amountInZec: BigDecimal
    val amountInZatoshi: Zatoshi
    val amountOutFormatted: BigDecimal
    val recipient: String
    val affiliateFee: Zatoshi
    val affiliateFeeUsd: BigDecimal

    val timestamp: Instant
}

data class NearSwapQuote(
    private val response: QuoteResponseDto,
) : SwapQuote {
    override val destinationAmount: Zatoshi = Zatoshi(response.quote.amountIn.toLong())
    override val depositAddress: String = response.quote.depositAddress
    override val origin: String = response.quoteRequest.originAsset
    override val destinationAssetTicker: String = response.quoteRequest.destinationAsset
    override val destinationAddress: String = response.quoteRequest.recipient
    override val provider: String = "near"
    override val type: SwapMode =
        when (response.quoteRequest.swapType) {
            EXACT_INPUT -> SwapMode.EXACT_INPUT
            EXACT_OUTPUT -> SwapMode.EXACT_OUTPUT
            null -> SwapMode.EXACT_INPUT
        }
    override val zecExchangeRate: BigDecimal =
        response.quote.amountInUsd
            .divide(response.quote.amountInFormatted, MathContext.DECIMAL128)

    // override val swapProviderFee: Zatoshi =
    //     (response.quote.amountInUsd - response.quote.amountOutUsd)
    //         .coerceAtLeast(BigDecimal(0))
    //         .divide(zecExchangeRate, MathContext.DECIMAL128)
    //         .convertZecToZatoshi()
    //
    // override val swapProviderFeeUsd: BigDecimal =
    //     (response.quote.amountInUsd - response.quote.amountOutUsd)
    //         .coerceAtLeast(BigDecimal(0))

    override val amountInUsd: BigDecimal = response.quote.amountInUsd

    override val amountInZec: BigDecimal = response.quote.amountInFormatted
    override val amountInZatoshi: Zatoshi = Zatoshi(response.quote.amountIn.toLong())
    override val amountOutUsd: BigDecimal = response.quote.amountOutUsd
    override val amountOutFormatted: BigDecimal = response.quote.amountOutFormatted
    override val recipient: String = response.quoteRequest.recipient

    override val affiliateFee: Zatoshi =
        response.quote.amountInUsd
            .coerceAtLeast(BigDecimal(0))
            .multiply(
                BigDecimal(AFFILIATE_FEE_BPS).divide(BigDecimal("10000"), MathContext.DECIMAL128),
                MathContext.DECIMAL128
            ).divide(zecExchangeRate, MathContext.DECIMAL128)
            .convertZecToZatoshi()

    override val affiliateFeeUsd: BigDecimal =
        response.quote.amountInUsd
            .coerceAtLeast(BigDecimal(0))
            .multiply(
                BigDecimal(AFFILIATE_FEE_BPS).divide(BigDecimal("10000"), MathContext.DECIMAL128),
                MathContext.DECIMAL128
            )

    override val timestamp: Instant = response.timestamp
}
