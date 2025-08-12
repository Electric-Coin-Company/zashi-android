package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.model.CompositeSwapQuote
import co.electriccoin.zcash.ui.common.repository.SwapQuoteData
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class GetCompositeSwapQuoteUseCase(
    private val swapRepository: SwapRepository,
) {
    fun observe() = combine(
        swapRepository.slippage,
        swapRepository.mode,
        swapRepository.quote.filterNotNull(),
        swapRepository.selectedAsset.filterNotNull(),
        swapRepository.assets.map { it.zecAsset }.filterNotNull()
    ) { slippage, mode, quote, destinationAsset, originAsset ->
        when (quote) {
            SwapQuoteData.Loading -> SwapQuoteCompositeData.Loading
            is SwapQuoteData.Error -> SwapQuoteCompositeData.Error(quote.exception)
            is SwapQuoteData.Success -> SwapQuoteCompositeData.Success(
                CompositeSwapQuote(
                    slippage = slippage,
                    mode = mode,
                    quote = quote.quote,
                    destinationAsset = destinationAsset,
                    originAsset = originAsset
                )
            )
        }
    }.distinctUntilChanged()
}

sealed interface SwapQuoteCompositeData {
    data class Success(val quote: CompositeSwapQuote) : SwapQuoteCompositeData

    data class Error(val exception: Exception) : SwapQuoteCompositeData

    data object Loading : SwapQuoteCompositeData
}
