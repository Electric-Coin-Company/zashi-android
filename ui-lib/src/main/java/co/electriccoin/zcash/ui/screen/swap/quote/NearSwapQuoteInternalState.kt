package co.electriccoin.zcash.ui.screen.swap.quote

import cash.z.ecc.android.sdk.ext.convertZatoshiToZec
import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.datasource.SendTransactionProposal
import co.electriccoin.zcash.ui.common.model.NearSwapAsset
import co.electriccoin.zcash.ui.common.model.NearSwapQuote
import co.electriccoin.zcash.ui.common.repository.SwapMode
import java.math.BigDecimal
import java.math.MathContext

internal data class NearSwapQuoteInternalState(
    override val mode: SwapMode,
    override val originAsset: NearSwapAsset,
    override val destinationAsset: NearSwapAsset,
    override val slippage: BigDecimal,
    override val proposal: SendTransactionProposal,
    val quote: NearSwapQuote,
) : SwapQuoteInternalState {
    private val data = quote.response.quote

    override val zecExchangeRate: BigDecimal = data.amountInUsd.divide(data.amountInFormatted, MathContext.DECIMAL128)
    override val zecFee: BigDecimal = proposal.proposal.totalFeeRequired().convertZatoshiToZec()
    override val zecFeeUsd: BigDecimal = zecExchangeRate.multiply(zecFee, MathContext.DECIMAL128)

    override val swapProviderFee: Zatoshi = (data.amountInUsd - data.amountOutUsd)
        .divide(zecExchangeRate, MathContext.DECIMAL128)
        .convertZecToZatoshi()
    override val swapProviderFeeUsd: BigDecimal = data.amountInUsd - data.amountOutUsd

    override val amountInZatoshi: Zatoshi = Zatoshi(data.amountIn.toLong())
    override val amountInZec: BigDecimal = data.amountInFormatted
    override val amountInDecimals: Int = originAsset.token.decimals
    override val amountInUsd: BigDecimal = data.amountInUsd

    override val amountOutFormatted: BigDecimal = data.amountOutFormatted
    override val amountOutDecimals: Int = destinationAsset.token.decimals
    override val amountOutUsd: BigDecimal = data.amountOutUsd

    override val recipient: String = quote.response.quoteRequest.recipient

    override val totalZec: BigDecimal = amountInZec + zecFee

    override val totalUsd: BigDecimal = data.amountInUsd + zecFeeUsd
}