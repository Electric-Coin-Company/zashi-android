package co.electriccoin.zcash.ui.common.model

import cash.z.ecc.android.sdk.ext.convertZatoshiToZec
import cash.z.ecc.android.sdk.model.Proposal
import cash.z.ecc.android.sdk.model.Zatoshi
import java.math.BigDecimal
import java.math.MathContext

data class CompositeSwapQuote(
    val mode: SwapMode,
    val originAsset: SwapAsset,
    val destinationAsset: SwapAsset,
    val slippage: BigDecimal,
    val quote: SwapQuote
) : SwapQuote by quote {
    // val amountInDecimals: Int = originAsset.decimals

    val amountOutDecimals: Int = destinationAsset.decimals

    fun getZecFeeUsd(proposal: Proposal): BigDecimal =
        zecExchangeRate.multiply(getZecFee(proposal), MathContext.DECIMAL128)

    fun getTotalZatoshi(proposal: Proposal): Zatoshi = amountInZatoshi + proposal.totalFeeRequired()

    fun getTotalZec(proposal: Proposal): BigDecimal = amountInZec + getZecFee(proposal)

    fun getTotalUsd(proposal: Proposal): BigDecimal = amountInUsd + getZecFeeUsd(proposal)

    fun getTotalFeesUsd(proposal: Proposal): BigDecimal = affiliateFeeUsd + getZecFeeUsd(proposal)

    fun getTotalFeesZatoshi(proposal: Proposal): Zatoshi = proposal.totalFeeRequired() + affiliateFee

    private val tokenTicker = destinationAsset.tokenTicker.lowercase()
    private val chainTicker = destinationAsset.chainTicker.lowercase()

    override val provider: String = "${quote.provider}.$tokenTicker.$chainTicker"

    private fun getZecFee(proposal: Proposal): BigDecimal = proposal.totalFeeRequired().convertZatoshiToZec()
}
