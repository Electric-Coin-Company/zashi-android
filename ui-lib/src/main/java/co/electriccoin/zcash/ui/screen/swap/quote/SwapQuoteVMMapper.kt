package co.electriccoin.zcash.ui.screen.swap.quote

import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.model.FiatCurrency
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_INPUT
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_OUTPUT
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.SwapTokenAmountState
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.TickerLocation
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByAddress
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber
import co.electriccoin.zcash.ui.design.util.stringResByDynamicNumber
import co.electriccoin.zcash.ui.design.util.stringResByNumber
import java.math.BigDecimal
import java.math.RoundingMode

internal class SwapQuoteVMMapper {
    fun createState(
        state: SwapQuoteInternalState,
        onBack: () -> Unit,
        onSubmitQuoteClick: () -> Unit
    ): SwapQuoteState.Success =
        with(state) {
            return SwapQuoteState.Success(
                title =
                    when (quote.mode) {
                        EXACT_INPUT -> stringRes(R.string.swap_quote_title)
                        EXACT_OUTPUT -> stringRes(R.string.pay_quote_title)
                    },
                rotateIcon = quote.mode == EXACT_OUTPUT,
                from = createFromState(),
                to = createToState(),
                items = createItems(),
                amount = createTotalAmountState(),
                onBack = onBack,
                infoText = createInfoText(),
                primaryButton =
                    ButtonState(
                        text = stringRes(co.electriccoin.zcash.ui.design.R.string.general_confirm),
                        onClick = onSubmitQuoteClick
                    )
            )
        }

    @Suppress("MagicNumber")
    private fun SwapQuoteInternalState.createInfoText(): StringResource? {
        if (quote.quote.type == EXACT_OUTPUT) return null
        val slippageUsd = quote.quote.amountOutUsd.multiply(quote.slippage.divide(BigDecimal(100)))
        return stringRes(
            R.string.swap_quote_info,
            stringResByDynamicCurrencyNumber(slippageUsd, FiatCurrency.USD.symbol),
            stringResByNumber(quote.slippage, minDecimals = 0) + stringRes("%")
        )
    }

    @Suppress("MagicNumber")
    private fun SwapQuoteInternalState.createItems(): List<SwapQuoteInfoItem> =
        listOfNotNull(
            SwapQuoteInfoItem(
                description =
                    when (quote.mode) {
                        EXACT_INPUT -> stringRes(R.string.swap_quote_from)
                        EXACT_OUTPUT -> stringRes(R.string.pay_from)
                    },
                title = stringRes(R.string.swap_quote_zashi),
                subtitle = null
            ),
            SwapQuoteInfoItem(
                description =
                    when (quote.mode) {
                        EXACT_INPUT -> stringRes(R.string.swap_quote_to)
                        EXACT_OUTPUT -> stringRes(R.string.pay_to)
                    },
                title = stringResByAddress(quote.recipient, true),
                subtitle = null
            ),
            SwapQuoteInfoItem(
                description = stringRes(R.string.swap_quote_total_fees),
                title = stringRes(totalFeesZatoshi),
                subtitle = stringResByDynamicCurrencyNumber(totalFeesUsd, FiatCurrency.USD.symbol)
                    .takeIf {
                        quote.mode == EXACT_INPUT
                    }
            ),
            if (quote.quote.type == EXACT_OUTPUT) {
                val slippage = quote.slippage.divide(BigDecimal(100))
                val slippageZatoshi = quote.amountInZec.multiply(slippage).convertZecToZatoshi()
                val slippageUsd = quote.quote.amountOutUsd.multiply(slippage)
                SwapQuoteInfoItem(
                    description =
                        stringRes(
                            R.string.swap_quote_max_slippage,
                            stringResByNumber(quote.slippage, minDecimals = 0) + stringRes("%")
                        ),
                    title = stringRes(slippageZatoshi),
                    subtitle = stringResByDynamicCurrencyNumber(slippageUsd, FiatCurrency.USD.symbol)
                        .takeIf {
                            quote.mode == EXACT_INPUT
                        }
                )
            } else {
                null
            }
        )

    private fun SwapQuoteInternalState.createTotalAmountState(): SwapQuoteInfoItem =
        SwapQuoteInfoItem(
            description = stringRes(R.string.swap_quote_total_amount),
            title = stringRes(totalZec.convertZecToZatoshi()),
            subtitle = stringResByDynamicCurrencyNumber(totalUsd, FiatCurrency.USD.symbol)
        )

    private fun SwapQuoteInternalState.createFromState(): SwapTokenAmountState =
        SwapTokenAmountState(
            bigIcon = imageRes(R.drawable.ic_zec_round_full),
            smallIcon = imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_receive_shield),
            title = stringRes(quote.amountInZatoshi, TickerLocation.HIDDEN),
            subtitle = stringResByDynamicCurrencyNumber(quote.amountInUsd, FiatCurrency.USD.symbol)
        )

    private fun SwapQuoteInternalState.createToState(): SwapTokenAmountState =
        SwapTokenAmountState(
            bigIcon = quote.destinationAsset.tokenIcon,
            smallIcon = quote.destinationAsset.chainIcon,
            title =
                stringResByDynamicNumber(
                    quote.amountOutFormatted.setScale(quote.amountOutDecimals, RoundingMode.DOWN),
                ),
            subtitle = stringResByDynamicCurrencyNumber(quote.amountOutUsd, FiatCurrency.USD.symbol)
        )
}
