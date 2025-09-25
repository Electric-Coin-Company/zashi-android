package co.electriccoin.zcash.ui.screen.swap.quote

import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.model.FiatCurrency
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_INPUT
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_OUTPUT
import co.electriccoin.zcash.ui.common.model.getQuoteChainIcon
import co.electriccoin.zcash.ui.common.model.getQuoteTokenIcon
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.SwapTokenAmountState
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByAddress
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber
import co.electriccoin.zcash.ui.design.util.stringResByDynamicNumber
import co.electriccoin.zcash.ui.design.util.stringResByNumber
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

internal class SwapQuoteVMMapper {
    @Suppress("UseCheckOrError")
    fun createState(
        state: SwapQuoteInternalState,
        onBack: () -> Unit,
        onSubmitQuoteClick: () -> Unit,
        onNavigateToOnRampSwap: () -> Unit
    ): SwapQuoteState.Success =
        with(state) {
            return SwapQuoteState.Success(
                title =
                    when {
                        quote.destinationAsset.tokenTicker.lowercase() == "zec" -> stringRes("Review Quote")
                        quote.mode == EXACT_INPUT -> stringRes(R.string.swap_quote_title)
                        quote.mode == EXACT_OUTPUT -> stringRes(R.string.pay_quote_title)
                        else -> throw IllegalStateException("Unknown swap mode")
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
        if (quote.mode == EXACT_OUTPUT) return null
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
                    when (quote.mode) {
                        EXACT_INPUT -> stringRes(R.string.swap_quote_from)
                        EXACT_OUTPUT -> stringRes(R.string.pay_from)
                    },
                title = stringRes(R.string.swap_quote_zashi),
                subtitle = null
            ).takeIf { quote.destinationAsset.tokenTicker.lowercase() != "zec" },
            SwapQuoteInfoItem(
                description =
                    when (quote.mode) {
                        EXACT_INPUT -> stringRes(R.string.swap_quote_to)
                        EXACT_OUTPUT -> stringRes(R.string.pay_to)
                    },
                title = stringResByAddress(quote.recipient, true),
                subtitle = null
            ).takeIf { quote.destinationAsset.tokenTicker.lowercase() != "zec" },
            SwapQuoteInfoItem(
                description = stringRes(R.string.swap_quote_total_fees),
                title =
                    if (quote.destinationAsset.tokenTicker.lowercase() == "zec") {
                        stringResByDynamicCurrencyNumber(totalFees, quote.originAsset.tokenTicker)
                    } else {
                        stringRes(totalFeesZatoshi)
                    },
                subtitle =
                    stringResByDynamicCurrencyNumber(totalFeesUsd, FiatCurrency.USD.symbol)
                        .takeIf {
                            quote.mode == EXACT_INPUT && quote.destinationAsset.tokenTicker.lowercase() != "zec"
                        }
            ),
            if (quote.mode == EXACT_OUTPUT) {
                val slippage = quote.slippage.divide(BigDecimal(100))
                val slippageUsd = quote.amountOutUsd.multiply(slippage)
                SwapQuoteInfoItem(
                    description =
                        stringRes(
                            R.string.swap_quote_max_slippage,
                            stringResByNumber(quote.slippage, minDecimals = 0) + stringRes("%")
                        ),
                    title =
                        if (quote.destinationAsset.tokenTicker.lowercase() == "zec") {
                            val slippageToken =
                                quote.amountInFormatted
                                    .multiply(
                                        slippage,
                                        MathContext.DECIMAL128
                                    )
                            stringResByDynamicCurrencyNumber(slippageToken, quote.destinationAsset.tokenTicker)
                        } else {
                            val slippageZatoshi =
                                quote.amountInFormatted
                                    .multiply(
                                        slippage,
                                        MathContext.DECIMAL128
                                    ).convertZecToZatoshi()
                            stringRes(slippageZatoshi)
                        },
                    subtitle =
                        stringResByDynamicCurrencyNumber(slippageUsd, FiatCurrency.USD.symbol)
                            .takeIf {
                                quote.mode == EXACT_INPUT && quote.destinationAsset.tokenTicker.lowercase() != "zec"
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

    private fun SwapQuoteInternalState.createFromState(): SwapTokenAmountState =
        SwapTokenAmountState(
            bigIcon = quote.originAsset.getQuoteTokenIcon(),
            smallIcon = quote.originAsset.getQuoteChainIcon(isOriginAsset = true),
            title = stringResByDynamicNumber(quote.amountInFormatted),
            subtitle = stringResByDynamicCurrencyNumber(quote.amountInUsd, FiatCurrency.USD.symbol)
        )

    private fun SwapQuoteInternalState.createToState(): SwapTokenAmountState =
        SwapTokenAmountState(
            bigIcon = quote.destinationAsset.getQuoteTokenIcon(),
            smallIcon = quote.destinationAsset.getQuoteChainIcon(isOriginAsset = false),
            title =
                stringResByDynamicNumber(
                    quote.amountOutFormatted.setScale(quote.destinationAsset.decimals, RoundingMode.DOWN),
                ),
            subtitle = stringResByDynamicCurrencyNumber(quote.amountOutUsd, FiatCurrency.USD.symbol)
        )
}
