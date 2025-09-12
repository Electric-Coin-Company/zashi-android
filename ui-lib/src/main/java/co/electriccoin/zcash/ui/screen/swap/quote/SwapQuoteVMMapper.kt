package co.electriccoin.zcash.ui.screen.swap.quote

import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.model.FiatCurrency
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_INPUT
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_OUTPUT
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.SwapTokenAmountState
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByAddress
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber
import co.electriccoin.zcash.ui.design.util.stringResByDynamicNumber
import co.electriccoin.zcash.ui.design.util.stringResByNumber
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

internal class SwapQuoteVMMapper {
    fun createState(
        state: SwapQuoteInternalState,
        onBack: () -> Unit,
        onSubmitQuoteClick: () -> Unit,
        onNavigateToOnRampSwap: () -> Unit
    ): SwapQuoteState.Success =
        with(state) {
            return SwapQuoteState.Success(
                title =
                    when  {
                        quote.destinationAsset.tokenTicker.lowercase() == "zec" -> stringRes("Review Quote")
                        quote.type == EXACT_INPUT -> stringRes(R.string.swap_quote_title)
                        quote.type == EXACT_OUTPUT -> stringRes(R.string.pay_quote_title)
                        else -> throw IllegalStateException("Unknown swap mode")
                    },
                rotateIcon = quote.type == EXACT_OUTPUT,
                from = createFromState(),
                to = createToState(),
                items = createItems(),
                amount = createTotalAmountState(),
                onBack = onBack,
                infoText = createInfoText(),
                primaryButton =
                    ButtonState(
                        text = stringRes(co.electriccoin.zcash.ui.design.R.string.general_confirm),
                        onClick = {
                            if (quote.destinationAsset.tokenTicker.lowercase() == "zec") {
                                onNavigateToOnRampSwap()
                            } else {
                                onSubmitQuoteClick()
                            }
                        }
                    )
            )
        }

    @Suppress("MagicNumber")
    private fun SwapQuoteInternalState.createInfoText(): StringResource? {
        if (quote.type == EXACT_OUTPUT) return null
        val slippageUsd = quote.amountOutUsd.multiply(quote.slippage.divide(BigDecimal(100)))
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
                    when (quote.type) {
                        EXACT_INPUT -> stringRes(R.string.swap_quote_from)
                        EXACT_OUTPUT -> stringRes(R.string.pay_from)
                    },
                title = stringRes(R.string.swap_quote_zashi),
                subtitle = null
            ).takeIf { quote.destinationAsset.tokenTicker.lowercase() != "zec" },
            SwapQuoteInfoItem(
                description =
                    when (quote.type) {
                        EXACT_INPUT -> stringRes(R.string.swap_quote_to)
                        EXACT_OUTPUT -> stringRes(R.string.pay_to)
                    },
                title = stringResByAddress(quote.recipient, true),
                subtitle = null
            ).takeIf { quote.destinationAsset.tokenTicker.lowercase() != "zec" },
            SwapQuoteInfoItem(
                description = stringRes(R.string.swap_quote_total_fees),
                title = if (quote.destinationAsset.tokenTicker.lowercase() == "zec") {
                    stringResByDynamicCurrencyNumber(totalFees, quote.originAsset.tokenTicker)
                } else {
                    stringRes(totalFeesZatoshi)
                },
                subtitle =
                    stringResByDynamicCurrencyNumber(totalFeesUsd, FiatCurrency.USD.symbol)
                        .takeIf {
                            quote.type == EXACT_INPUT && quote.destinationAsset.tokenTicker.lowercase() != "zec"
                        }
            ),
            if (quote.type == EXACT_OUTPUT) {
                val slippage = quote.slippage.divide(BigDecimal(100))
                val slippageUsd = quote.amountOutUsd.multiply(slippage)
                SwapQuoteInfoItem(
                    description =
                        stringRes(
                            R.string.swap_quote_max_slippage,
                            stringResByNumber(quote.slippage, minDecimals = 0) + stringRes("%")
                        ),
                    title = if (quote.destinationAsset.tokenTicker.lowercase() == "zec") {
                        val slippageToken = quote.amountInFormatted
                            .multiply(
                                slippage,
                                MathContext.DECIMAL128
                            )
                        stringResByDynamicCurrencyNumber(slippageToken, quote.destinationAsset.tokenTicker)
                    } else {
                        val slippageZatoshi = quote.amountInFormatted.multiply(
                            slippage,
                            MathContext.DECIMAL128
                        ).convertZecToZatoshi()
                        stringRes(slippageZatoshi)
                    },
                    subtitle =
                        stringResByDynamicCurrencyNumber(slippageUsd, FiatCurrency.USD.symbol)
                            .takeIf {
                                quote.type == EXACT_INPUT && quote.destinationAsset.tokenTicker.lowercase() != "zec"
                            }
                )
            } else {
                null
            }
        )

    private fun SwapQuoteInternalState.createTotalAmountState(): SwapQuoteInfoItem =
        SwapQuoteInfoItem(
            description = stringRes(R.string.swap_quote_total_amount),
            title = stringResByDynamicCurrencyNumber(total, quote.originAsset.tokenTicker),
            subtitle = stringResByDynamicCurrencyNumber(totalUsd, FiatCurrency.USD.symbol)
        )

    private fun SwapQuoteInternalState.createFromState(): SwapTokenAmountState {
        return if (quote.destinationAsset.tokenTicker.lowercase() == "zec") {
            SwapTokenAmountState(
                bigIcon = quote.originAsset.tokenIcon,
                smallIcon = quote.originAsset.chainIcon,
                title = stringResByDynamicNumber(quote.amountInFormatted),
                subtitle = stringResByDynamicCurrencyNumber(quote.amountInUsd, FiatCurrency.USD.symbol)
            )
        } else {
            SwapTokenAmountState(
                bigIcon = imageRes(R.drawable.ic_zec_round_full),
                smallIcon = imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_receive_shield),
                title = stringResByDynamicNumber(quote.amountInFormatted),
                subtitle = stringResByDynamicCurrencyNumber(quote.amountInUsd, FiatCurrency.USD.symbol)
            )
        }
    }

    private fun SwapQuoteInternalState.createToState(): SwapTokenAmountState {
        return if (quote.destinationAsset.tokenTicker.lowercase() == "zec") {
            SwapTokenAmountState(
                bigIcon = imageRes(R.drawable.ic_zec_round_full),
                smallIcon = null,
                title =
                    stringResByDynamicNumber(
                        quote.amountOutFormatted.setScale(quote.destinationAsset.decimals, RoundingMode.DOWN),
                    ),
                subtitle = stringResByDynamicCurrencyNumber(quote.amountOutUsd, FiatCurrency.USD.symbol)
            )
        } else {
            SwapTokenAmountState(
                bigIcon = quote.destinationAsset.tokenIcon,
                smallIcon = quote.destinationAsset.chainIcon,
                title =
                    stringResByDynamicNumber(
                        quote.amountOutFormatted.setScale(quote.destinationAsset.decimals, RoundingMode.DOWN),
                    ),
                subtitle = stringResByDynamicCurrencyNumber(quote.amountOutUsd, FiatCurrency.USD.symbol)
            )
        }
    }
}
