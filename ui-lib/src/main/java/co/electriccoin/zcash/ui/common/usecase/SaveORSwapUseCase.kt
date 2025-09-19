package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.MetadataRepository
import co.electriccoin.zcash.ui.common.repository.SwapQuoteData
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import co.electriccoin.zcash.ui.screen.swap.detail.SwapDetailArgs

class SaveORSwapUseCase(
    private val swapRepository: SwapRepository,
    private val metadataRepository: MetadataRepository,
    private val navigationRouter: NavigationRouter,
) {
    operator fun invoke() {
        val quote = (swapRepository.quote.value as? SwapQuoteData.Success)?.quote
        if (quote != null) {
            metadataRepository.markTxAsSwap(
                depositAddress = quote.depositAddress,
                provider = quote.provider,
                tokenTicker = quote.destinationAsset.tokenTicker,
                chainTicker = quote.destinationAsset.chainTicker,
                totalFees = Zatoshi(0),
                totalFeesUsd = quote.amountOutFormatted
            )

            swapRepository.clear()
            navigationRouter.replaceAll(SwapDetailArgs(quote.depositAddress))
        }
    }
}
