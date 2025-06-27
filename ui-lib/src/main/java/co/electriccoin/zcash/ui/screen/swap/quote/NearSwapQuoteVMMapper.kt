package co.electriccoin.zcash.ui.screen.swap.quote

import cash.z.ecc.android.sdk.ext.convertZatoshiToZec
import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.model.FiatCurrency
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.datasource.SendTransactionProposal
import co.electriccoin.zcash.ui.common.model.NearSwapAsset
import co.electriccoin.zcash.ui.common.model.NearSwapQuote
import co.electriccoin.zcash.ui.common.repository.SwapMode
import co.electriccoin.zcash.ui.common.repository.SwapMode.PAY
import co.electriccoin.zcash.ui.common.repository.SwapMode.SWAP
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByAddress
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber
import co.electriccoin.zcash.ui.design.util.stringResByNumber
import java.math.BigDecimal
import java.math.MathContext

internal class NearSwapQuoteVMMapper : SwapQuoteVMMapper {
    override fun createState(
        state: InternalState,
        onBack: () -> Unit,
        onSubmitQuoteClick: () -> Unit
    ): SwapQuoteState.Success = with(state) {
        return SwapQuoteState.Success(
            title = when (mode) {
                SWAP -> stringRes("Swap Now")
                PAY -> stringRes("Pay now")
            },
            rotateIcon = mode == PAY,
            from = createFromState(this),
            to = createToState(this),
            items = createItems(this),
            amount = createTotalAmountState(this),
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

    private fun createItems(state: InternalState): List<SwapQuoteInfoItem> = with(state) {
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
                title = stringResByAddress(recipient, true),
                subtitle = null
            ),
            SwapQuoteInfoItem(
                description = stringRes("ZEC transaction fee"),
                title = stringRes(proposal.proposal.totalFeeRequired()),
                subtitle = stringResByDynamicCurrencyNumber(
                    zecFeeUsd,
                    FiatCurrency.USD.symbol,
                    maxDecimals = amountInDecimals
                )
            ),
            SwapQuoteInfoItem(
                description = stringRes("Swap Provider fee"),
                title = stringRes(swapProviderFee),
                subtitle = stringResByDynamicCurrencyNumber(swapProviderFeeUsd, FiatCurrency.USD.symbol)
            ),
        )
    }

    private fun createTotalAmountState(state: InternalState): SwapQuoteInfoItem = with(state) {
        return SwapQuoteInfoItem(
            description = stringRes("Total Amount"),
            title = stringRes(totalZec.convertZecToZatoshi()),
            subtitle = stringResByDynamicCurrencyNumber(totalUsd, FiatCurrency.USD.symbol)
        )
    }

    private fun createFromState(state: InternalState): SwapTokenAmountState = with(state) {
        require(originAsset is NearSwapAsset)
        require(destinationAsset is NearSwapAsset)

        return when (mode) {
            SWAP -> {
                SwapTokenAmountState(
                    bigIcon = imageRes(R.drawable.ic_zec_round_full),
                    smallIcon = imageRes(R.drawable.ic_receive_shield),
                    title = stringResByNumber(amountInZec, amountInDecimals),
                    subtitle = stringResByDynamicCurrencyNumber(amountInUsd, FiatCurrency.USD.symbol)
                )
            }

            PAY -> SwapTokenAmountState(
                bigIcon = destinationAsset.tokenIcon,
                smallIcon = destinationAsset.chainIcon,
                title = stringResByNumber(amountOutFormatted, amountOutMaxDecimals),
                subtitle = stringResByDynamicCurrencyNumber(amountOutUsd, FiatCurrency.USD.symbol)
            )
        }
    }

    private fun createToState(state: InternalState): SwapTokenAmountState = with(state) {
        return when (mode) {
            SWAP -> SwapTokenAmountState(
                bigIcon = destinationAsset.tokenIcon,
                smallIcon = destinationAsset.chainIcon,
                title = stringResByNumber(amountOutFormatted, amountOutMaxDecimals),
                subtitle = stringResByDynamicCurrencyNumber(amountOutUsd, FiatCurrency.USD.symbol)
            )

            PAY -> SwapTokenAmountState(
                bigIcon = imageRes(R.drawable.ic_zec_round_full),
                smallIcon = imageRes(R.drawable.ic_receive_shield),
                title = stringResByNumber(amountInZec),
                subtitle = stringResByDynamicCurrencyNumber(amountInUsd, FiatCurrency.USD.symbol)
            )
        }
    }
}

internal data class NearInternalState(
    override val mode: SwapMode,
    override val originAsset: NearSwapAsset,
    override val destinationAsset: NearSwapAsset,
    override val slippage: BigDecimal,
    override val proposal: SendTransactionProposal,
    val quote: NearSwapQuote,
) : InternalState {
    private val data = quote.response.quote

    override val zecExchangeRate: BigDecimal = data.amountInUsd.divide(data.amountInFormatted, MathContext.DECIMAL128)
    override val zecFee: BigDecimal = proposal.proposal.totalFeeRequired().convertZatoshiToZec()
    override val zecFeeUsd: BigDecimal = zecExchangeRate.multiply(zecFee, MathContext.DECIMAL128)

    override val swapProviderFee: Zatoshi = (data.amountInUsd - data.amountOutUsd)
        .divide(zecExchangeRate, MathContext.DECIMAL128)
        .convertZecToZatoshi()
    override val swapProviderFeeUsd: BigDecimal = data.amountInUsd - data.amountOutUsd

    override val amountInZec: BigDecimal = data.amountInFormatted
    override val amountInDecimals: Int = originAsset.token.decimals
    override val amountInUsd: BigDecimal = data.amountInUsd

    override val amountOutFormatted: BigDecimal = data.amountOutFormatted
    override val amountOutMaxDecimals: Int = destinationAsset.token.decimals
    override val amountOutUsd: BigDecimal = data.amountOutUsd

    override val recipient: String = quote.response.quoteRequest.recipient

    override val totalZec: BigDecimal = amountInZec + zecFee

    override val totalUsd: BigDecimal = data.amountInUsd + zecFeeUsd
}
