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
                        EXACT_INPUT -> stringRes("Swap Now")
                        EXACT_OUTPUT -> stringRes("Pay now")
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
                        text = stringRes("Confirm"),
                        onClick = onSubmitQuoteClick
                    )
            )
        }

    private fun SwapQuoteInternalState.createInfoText(): StringResource? {
        if (quote.quote.type == EXACT_OUTPUT) return null

        val slippageUsd = quote.quote.amountOutUsd.multiply(quote.slippage.divide(BigDecimal(100)))

        return stringRes("You could receive up to ") +
            stringResByDynamicCurrencyNumber(slippageUsd, FiatCurrency.USD.symbol) + // $2.50
            stringRes(" less based on the ") +
            stringResByNumber(quote.slippage, minDecimals = 0) + stringRes("%") +
            stringRes(" slippage you set.")
    }

    private fun SwapQuoteInternalState.createItems(): List<SwapQuoteInfoItem> = listOfNotNull(
        SwapQuoteInfoItem(
            description =
                when (quote.mode) {
                    EXACT_INPUT -> stringRes("Swap from")
                    EXACT_OUTPUT -> stringRes("Pay from")
                },
            title = stringRes("Zashi"),
            subtitle = null
        ),
        SwapQuoteInfoItem(
            description =
                when (quote.mode) {
                    EXACT_INPUT -> stringRes("Swap to")
                    EXACT_OUTPUT -> stringRes("Pay to")
                },
            title = stringResByAddress(quote.recipient, true),
            subtitle = null
        ),
        SwapQuoteInfoItem(
            description = stringRes("Total fees"),
            title = stringRes(totalFeesZatoshi),
            subtitle = stringResByDynamicCurrencyNumber(totalFeesUsd, FiatCurrency.USD.symbol)
        ),
        if (quote.quote.type == EXACT_OUTPUT) {
            val slippage = quote.slippage.divide(BigDecimal(100))
            val slippageZatoshi = quote.amountInZec.multiply(slippage).convertZecToZatoshi()
            val slippageUsd = quote.quote.amountOutUsd.multiply(slippage)
            SwapQuoteInfoItem(
                description = stringRes("Max slippage ") +
                    stringResByNumber(quote.slippage, minDecimals = 0) + stringRes("%"),
                title = stringRes(slippageZatoshi),
                subtitle = stringResByDynamicCurrencyNumber(slippageUsd, FiatCurrency.USD.symbol)
            )
        } else {
            null
        }
    )

    private fun SwapQuoteInternalState.createTotalAmountState(): SwapQuoteInfoItem {
        return SwapQuoteInfoItem(
            description = stringRes("Total Amount"),
            title = stringRes(totalZec.convertZecToZatoshi()),
            subtitle = stringResByDynamicCurrencyNumber(totalUsd, FiatCurrency.USD.symbol)
        )
    }

    private fun SwapQuoteInternalState.createFromState(): SwapTokenAmountState {
        return when (quote.mode) {
            EXACT_INPUT -> {
                SwapTokenAmountState(
                    bigIcon = imageRes(R.drawable.ic_zec_round_full),
                    smallIcon = imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_receive_shield),
                    title = stringRes(quote.amountInZatoshi, TickerLocation.HIDDEN),
                    subtitle = stringResByDynamicCurrencyNumber(quote.amountInUsd, FiatCurrency.USD.symbol)
                )
            }

            EXACT_OUTPUT ->
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
    }

    private fun SwapQuoteInternalState.createToState(): SwapTokenAmountState {
        return when (quote.mode) {
            EXACT_INPUT ->
                SwapTokenAmountState(
                    bigIcon = quote.destinationAsset.tokenIcon,
                    smallIcon = quote.destinationAsset.chainIcon,
                    title =
                        stringResByDynamicNumber(
                            quote.amountOutFormatted.setScale(quote.amountOutDecimals, RoundingMode.DOWN),
                        ),
                    subtitle = stringResByDynamicCurrencyNumber(quote.amountOutUsd, FiatCurrency.USD.symbol)
                )

            EXACT_OUTPUT ->
                SwapTokenAmountState(
                    bigIcon = imageRes(R.drawable.ic_zec_round_full),
                    smallIcon = imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_receive_shield),
                    title = stringRes(quote.amountInZatoshi, TickerLocation.HIDDEN),
                    subtitle = stringResByDynamicCurrencyNumber(quote.amountInUsd, FiatCurrency.USD.symbol)
                )
        }
    }
}
