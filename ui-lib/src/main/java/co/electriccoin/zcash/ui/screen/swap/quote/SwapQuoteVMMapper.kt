package co.electriccoin.zcash.ui.screen.swap.quote

import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.model.FiatCurrency
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.NearSwapAsset
import co.electriccoin.zcash.ui.common.repository.SwapMode.PAY
import co.electriccoin.zcash.ui.common.repository.SwapMode.SWAP
import co.electriccoin.zcash.ui.design.component.ButtonState
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

    private fun createItems(state: SwapQuoteInternalState): List<SwapQuoteInfoItem> = with(state) {
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
                    zecFeeUsd.setScale(amountInDecimals, RoundingMode.DOWN),
                    FiatCurrency.USD.symbol,
                )
            ),
            SwapQuoteInfoItem(
                description = stringRes("Swap Provider fee"),
                title = stringRes(swapProviderFee),
                subtitle = stringResByDynamicCurrencyNumber(swapProviderFeeUsd, FiatCurrency.USD.symbol)
            ),
        )
    }

    private fun createTotalAmountState(state: SwapQuoteInternalState): SwapQuoteInfoItem = with(state) {
        return SwapQuoteInfoItem(
            description = stringRes("Total Amount"),
            title = stringRes(totalZec.convertZecToZatoshi()),
            subtitle = stringResByDynamicCurrencyNumber(totalUsd, FiatCurrency.USD.symbol)
        )
    }

    private fun createFromState(state: SwapQuoteInternalState): SwapTokenAmountState = with(state) {
        require(originAsset is NearSwapAsset)
        require(destinationAsset is NearSwapAsset)

        return when (mode) {
            SWAP -> {
                SwapTokenAmountState(
                    bigIcon = imageRes(R.drawable.ic_zec_round_full),
                    smallIcon = imageRes(R.drawable.ic_receive_shield),
                    title = stringRes(amountInZatoshi, TickerLocation.HIDDEN),
                    subtitle = stringResByDynamicCurrencyNumber(amountInUsd, FiatCurrency.USD.symbol)
                )
            }

            PAY -> SwapTokenAmountState(
                bigIcon = destinationAsset.tokenIcon,
                smallIcon = destinationAsset.chainIcon,
                title = stringResByDynamicNumber(amountOutFormatted.setScale(amountOutDecimals, RoundingMode.DOWN)),
                subtitle = stringResByDynamicCurrencyNumber(amountOutUsd, FiatCurrency.USD.symbol)
            )
        }
    }

    private fun createToState(state: SwapQuoteInternalState): SwapTokenAmountState = with(state) {
        return when (mode) {
            SWAP -> SwapTokenAmountState(
                bigIcon = destinationAsset.tokenIcon,
                smallIcon = destinationAsset.chainIcon,
                title = stringResByDynamicNumber(amountOutFormatted.setScale(amountOutDecimals, RoundingMode.DOWN)),
                subtitle = stringResByDynamicCurrencyNumber(amountOutUsd, FiatCurrency.USD.symbol)
            )

            PAY -> SwapTokenAmountState(
                bigIcon = imageRes(R.drawable.ic_zec_round_full),
                smallIcon = imageRes(R.drawable.ic_receive_shield),
                title = stringRes(amountInZatoshi, TickerLocation.HIDDEN),
                subtitle = stringResByDynamicCurrencyNumber(amountInUsd, FiatCurrency.USD.symbol)
            )
        }
    }
}

