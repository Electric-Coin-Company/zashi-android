package co.electriccoin.zcash.ui.screen.swap.quote

import cash.z.ecc.android.sdk.ext.convertZatoshiToZec
import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.model.FiatCurrency
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.datasource.RegularTransactionProposal
import co.electriccoin.zcash.ui.common.model.NearSwapQuote
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.repository.SwapMode
import co.electriccoin.zcash.ui.common.repository.SwapMode.*
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByAddress
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber
import co.electriccoin.zcash.ui.design.util.stringResByNumber
import java.math.BigDecimal
import java.math.MathContext

class NearSwapQuoteSuccessMapper {
    fun createState(
        mode: SwapMode,
        destinationAsset: SwapAsset,
        quote: NearSwapQuote,
        slippage: BigDecimal,
        proposal: RegularTransactionProposal,
        onBack: () -> Unit,
        onSubmitQuoteClick: () -> Unit
    ): SwapQuoteState.Success {
        val data = quote.response.quote
        val zecExchangeRate = data.amountInUsd.divide(data.amountIn, MathContext.DECIMAL32)
        val zecFee = proposal.proposal.totalFeeRequired().convertZatoshiToZec()
        val zecFeeUsd = zecExchangeRate.multiply(zecFee, MathContext.DECIMAL32)
        val swapProviderFeeUsd = data.amountInUsd - data.amountOutUsd
        val swapProviderFeeZatoshi = swapProviderFeeUsd.divide(zecExchangeRate, MathContext.DECIMAL32)
            .convertZecToZatoshi()

        return SwapQuoteState.Success(
            title = when (mode) {
                SWAP -> stringRes("Swap Now")
                PAY -> stringRes("Pay now")
            },
            rotateIcon = mode == PAY,
            from = createFromState(mode, quote, destinationAsset),
            to = createToState(mode, quote, destinationAsset),
            items = createItems(
                mode = mode,
                quote = quote,
                proposal = proposal,
                // zecExchangeRate = zecExchangeRate,
                zecFeeUsd = zecFeeUsd,
                // zecFee = zecFee,
                swapProviderFeeZatoshi = swapProviderFeeZatoshi,
                swapProviderFeeUsd = swapProviderFeeUsd
            ),
            amount = createTotalAmountState(
                // mode = mode,
                quote = quote,
                // proposal = proposal,
                zecExchangeRate = zecExchangeRate,
                // zecFee = zecFee,
                zecFeeUsd = zecFeeUsd,
                // swapProviderFeeZatoshi = swapProviderFeeZatoshi,
                swapProviderFeeUsd = swapProviderFeeUsd
            ),
            onBack = onBack,
            infoText = stringRes("Total amount includes max slippage of ") +
                stringResByNumber(slippage) +
                stringRes("%"),
            primaryButton = ButtonState(
                text = stringRes("Confirm"),
                onClick = onSubmitQuoteClick
            )
        )
    }

    private fun createItems(
        mode: SwapMode,
        quote: NearSwapQuote,
        proposal: RegularTransactionProposal,
        // zecExchangeRate: BigDecimal,
        zecFeeUsd: BigDecimal,
        // zecFee: BigDecimal,
        swapProviderFeeZatoshi: Zatoshi,
        swapProviderFeeUsd: BigDecimal
    ): List<SwapQuoteInfoItem> {
        return listOf(
            SwapQuoteInfoItem(
                description = when (mode) {
                    SWAP -> stringRes("Swap from")
                    PAY -> stringRes("Pay from")
                },
                title = stringRes("Zashi"),
                subtitle = null
            ),
            SwapQuoteInfoItem(
                description = when (mode) {
                    SWAP -> stringRes("Swap to")
                    PAY -> stringRes("Pay to")
                },
                title = stringResByAddress(quote.response.quoteRequest.recipient, true),
                subtitle = null
            ),
            SwapQuoteInfoItem(
                description = stringRes("ZEC transaction fee"),
                title = stringRes(proposal.proposal.totalFeeRequired()),
                subtitle = stringResByDynamicCurrencyNumber(zecFeeUsd, FiatCurrency.USD.symbol)
            ),
            SwapQuoteInfoItem(
                description = stringRes("Swap Provider fee"),
                title = stringRes(swapProviderFeeZatoshi),
                subtitle = stringResByDynamicCurrencyNumber(swapProviderFeeUsd, FiatCurrency.USD.symbol)
            ),
        )
    }

    private fun createTotalAmountState(
        // mode: SwapMode,
        quote: NearSwapQuote,
        // proposal: RegularTransactionProposal,
        zecExchangeRate: BigDecimal,
        // zecFee: BigDecimal,
        zecFeeUsd: BigDecimal,
        // swapProviderFeeZatoshi: Zatoshi,
        swapProviderFeeUsd: BigDecimal
    ): SwapQuoteInfoItem {
        val data = quote.response.quote
        val totalFeesUsd = swapProviderFeeUsd + zecFeeUsd
        val totalFees = totalFeesUsd.divide(zecExchangeRate)
        // val totalFeesZatoshi = totalFees.convertZecToZatoshi()
        val totalAmountUsd = totalFeesUsd + data.amountInUsd
        val totalAmount = (totalFees + data.amountIn).convertZecToZatoshi()
        return SwapQuoteInfoItem(
            description = stringRes("Total Amount"),
            title = stringRes(totalAmount),
            subtitle = stringResByDynamicCurrencyNumber(totalAmountUsd, FiatCurrency.USD.symbol)
        )
    }

    private fun createFromState(
        mode: SwapMode,
        quote: NearSwapQuote,
        destinationAsset: SwapAsset
    ): SwapTokenAmountState {
        val data = quote.response.quote
        return when (mode) {
            SWAP -> SwapTokenAmountState(
                bigIcon = imageRes(R.drawable.ic_zec_round_full),
                smallIcon = imageRes(R.drawable.ic_receive_shield),
                title = stringResByNumber(data.amountIn),
                subtitle = stringResByDynamicCurrencyNumber(data.amountInUsd, FiatCurrency.USD.symbol)
            )

            PAY -> SwapTokenAmountState(
                bigIcon = destinationAsset.tokenIcon,
                smallIcon = destinationAsset.chainIcon,
                title = stringResByNumber(data.amountOut),
                subtitle = stringResByDynamicCurrencyNumber(data.amountOutUsd, FiatCurrency.USD.symbol)
            )
        }
    }

    private fun createToState(
        mode: SwapMode,
        quote: NearSwapQuote,
        destinationAsset: SwapAsset
    ): SwapTokenAmountState {
        val data = quote.response.quote
        return when (mode) {
            SWAP -> SwapTokenAmountState(
                bigIcon = destinationAsset.tokenIcon,
                smallIcon = destinationAsset.chainIcon,
                title = stringResByNumber(data.amountOut),
                subtitle = stringResByDynamicCurrencyNumber(data.amountOutUsd, FiatCurrency.USD.symbol)
            )

            PAY -> SwapTokenAmountState(
                bigIcon = imageRes(R.drawable.ic_zec_round_full),
                smallIcon = imageRes(R.drawable.ic_receive_shield),
                title = stringResByNumber(data.amountIn),
                subtitle = stringResByDynamicCurrencyNumber(data.amountInUsd, FiatCurrency.USD.symbol)
            )
        }
    }
}