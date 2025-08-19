package co.electriccoin.zcash.ui.screen.swap.quote

import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.datasource.SwapTransactionProposal
import co.electriccoin.zcash.ui.common.model.CompositeSwapQuote
import java.math.BigDecimal

internal data class NearSwapQuoteInternalState(
    val proposal: SwapTransactionProposal,
    override val quote: CompositeSwapQuote,
) : SwapQuoteInternalState {
    override val zatoshiFee: Zatoshi = proposal.proposal.totalFeeRequired()
    override val zecFeeUsd: BigDecimal = quote.getZecFeeUsd(proposal.proposal)
    override val totalZec: BigDecimal = quote.getTotalZec(proposal.proposal)
    override val totalUsd: BigDecimal = quote.getTotalUsd(proposal.proposal)
    override val totalFeesZatoshi: Zatoshi = quote.getTotalFeesZatoshi(proposal.proposal)
    override val totalFeesUsd: BigDecimal = quote.getTotalFeesUsd(proposal.proposal)
}
