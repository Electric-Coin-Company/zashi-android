package co.electriccoin.zcash.ui.common.model

import co.electriccoin.zcash.ui.common.model.near.QuoteResponseDto

sealed interface SwapQuote

data class NearSwapQuote(
    val response: QuoteResponseDto
) : SwapQuote
