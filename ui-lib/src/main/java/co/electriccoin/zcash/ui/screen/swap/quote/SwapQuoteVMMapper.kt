package co.electriccoin.zcash.ui.screen.swap.quote

import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.model.FiatCurrency
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_INPUT
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_OUTPUT
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.SwapTokenAmountState
import co.electriccoin.zcash.ui.design.util.TickerLocation
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByAddress
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber
import co.electriccoin.zcash.ui.design.util.stringResByDynamicNumber
import co.electriccoin.zcash.ui.design.util.stringResByNumber
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
                from = createFromState(this),
                to = createToState(this),
                items = createItems(this),
                amount = createTotalAmountState(this),
                onBack = onBack,
                infoText =
                    stringRes("Total amount includes max slippage of ") +
                        stringResByNumber(quote.slippage, minDecimals = 0) +
                        stringRes("%"),
                primaryButton =
                    ButtonState(
                        text = stringRes("Confirm"),
                        onClick = onSubmitQuoteClick
                    )
            )
        }

    private fun createItems(state: SwapQuoteInternalState): List<SwapQuoteInfoItem> =
        with(state) {
            return listOf(
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
                    description = stringRes("ZEC transaction fee"),
                    title = stringRes(zatoshiFee),
                    subtitle =
                        stringResByDynamicCurrencyNumber(
                            zecFeeUsd.setScale(quote.amountInDecimals, RoundingMode.DOWN),
                            FiatCurrency.USD.symbol,
                        )
                ),
                SwapQuoteInfoItem(
                    description = stringRes("Swap fee"),
                    title = stringRes(quote.swapProviderFee),
                    subtitle = stringResByDynamicCurrencyNumber(quote.swapProviderFeeUsd, FiatCurrency.USD.symbol)
                ),
            )
        }

    private fun createTotalAmountState(state: SwapQuoteInternalState): SwapQuoteInfoItem =
        with(state) {
            return SwapQuoteInfoItem(
                description = stringRes("Total Amount"),
                title = stringRes(totalZec.convertZecToZatoshi()),
                subtitle = stringResByDynamicCurrencyNumber(totalUsd, FiatCurrency.USD.symbol)
            )
        }

    private fun createFromState(state: SwapQuoteInternalState): SwapTokenAmountState =
        with(state) {
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

    private fun createToState(state: SwapQuoteInternalState): SwapTokenAmountState =
        with(state) {
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
