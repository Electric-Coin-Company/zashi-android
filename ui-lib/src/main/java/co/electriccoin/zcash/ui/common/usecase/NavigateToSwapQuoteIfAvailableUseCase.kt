package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.model.SwapMode
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_INPUT
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_OUTPUT
import co.electriccoin.zcash.ui.common.repository.SwapQuoteData
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import co.electriccoin.zcash.ui.screen.reviewtransaction.ReviewTransactionArgs
import co.electriccoin.zcash.ui.screen.swap.quote.SwapQuoteArgs

class NavigateToSwapQuoteIfAvailableUseCase(
    private val swapRepository: SwapRepository,
    private val navigationRouter: NavigationRouter
) {
    suspend operator fun invoke(mode: SwapMode, hideBottomSheet: suspend () -> Unit) {
        val value = swapRepository.quote.value

        val isQuoteAvailable = value is SwapQuoteData.Success || value is SwapQuoteData.Error
        if (isQuoteAvailable) {
            hideBottomSheet()
            when (mode) {
                EXACT_INPUT -> navigationRouter.forward(SwapQuoteArgs)
                EXACT_OUTPUT -> navigationRouter.forward(ReviewTransactionArgs)
            }
        }
    }
}
