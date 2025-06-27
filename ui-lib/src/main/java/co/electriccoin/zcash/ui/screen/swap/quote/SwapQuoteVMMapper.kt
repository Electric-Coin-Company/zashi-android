package co.electriccoin.zcash.ui.screen.swap.quote

import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.datasource.SendTransactionProposal
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.repository.SwapMode
import java.math.BigDecimal

internal interface SwapQuoteVMMapper {
    fun createState(
        state: InternalState,
        onBack: () -> Unit,
        onSubmitQuoteClick: () -> Unit
    ): SwapQuoteState.Success
}

internal sealed interface InternalState {
    val mode: SwapMode
    val originAsset: SwapAsset
    val destinationAsset: SwapAsset
    val slippage: BigDecimal
    val proposal: SendTransactionProposal

    val zecExchangeRate: BigDecimal
    val zecFee: BigDecimal
    val zecFeeUsd: BigDecimal

    val recipient: String

    val swapProviderFee: Zatoshi

    val swapProviderFeeUsd: BigDecimal
    val amountInZec: BigDecimal
    val amountInDecimals: Int

    val amountInUsd: BigDecimal
    val amountOutFormatted: BigDecimal
    val amountOutMaxDecimals: Int

    val amountOutUsd: BigDecimal

    val totalZec: BigDecimal
    val totalUsd: BigDecimal
}
