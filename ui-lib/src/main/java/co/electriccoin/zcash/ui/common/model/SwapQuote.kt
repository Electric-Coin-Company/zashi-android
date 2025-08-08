package co.electriccoin.zcash.ui.common.model

import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.model.near.QuoteResponseDto
import co.electriccoin.zcash.ui.common.model.near.SwapType.EXACT_INPUT
import co.electriccoin.zcash.ui.common.model.near.SwapType.EXACT_OUTPUT

sealed interface SwapQuote {
    val destinationAmount: Zatoshi
    val depositAddress: String
    val origin: String
    val destination: String
    val provider: String
    val type: SwapMode
}

data class NearSwapQuote(
    val response: QuoteResponseDto
) : SwapQuote {
    override val destinationAmount: Zatoshi = Zatoshi(response.quote.amountIn.toLong())
    override val depositAddress: String = response.quote.depositAddress
    override val origin: String = response.quoteRequest.originAsset
    override val destination: String = response.quoteRequest.destinationAsset
    override val provider: String = "near.$origin.$destination"
    override val type: SwapMode = when (response.quoteRequest.swapType) {
        EXACT_INPUT -> SwapMode.EXACT_INPUT
        EXACT_OUTPUT -> SwapMode.EXACT_OUTPUT
        null -> SwapMode.EXACT_INPUT
    }
}
