package co.electriccoin.zcash.ui.screen.transactiondetail

import cash.z.ecc.android.sdk.model.FiatCurrency
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.model.SwapQuoteStatus
import co.electriccoin.zcash.ui.common.model.getQuoteChainIcon
import co.electriccoin.zcash.ui.common.model.getQuoteTokenIcon
import co.electriccoin.zcash.ui.common.usecase.SwapHandle
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ButtonStyle
import co.electriccoin.zcash.ui.design.component.SwapQuoteHeaderState
import co.electriccoin.zcash.ui.design.component.SwapTokenAmountState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByDateTime
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber
import co.electriccoin.zcash.ui.design.util.stringResByDynamicNumber
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import java.time.Instant
import java.time.ZoneId

class CommonTransactionDetailMapper {

    fun createTransactionDetailTimestamp(timestamp: Instant?) =
        timestamp
            ?.atZone(ZoneId.systemDefault())
            ?.let {
                stringResByDateTime(
                    zonedDateTime = it,
                    useFullFormat = true
                )
            } ?: stringRes(R.string.transaction_detail_pending)

    fun createTransactionDetailErrorFooter(error: Exception?): ErrorFooter? {
        if (error == null) return null

        val isServiceUnavailableError = error is ResponseException &&
            error.response.status == HttpStatusCode.ServiceUnavailable

        return ErrorFooter(
            title =
                if (isServiceUnavailableError) {
                    stringRes(co.electriccoin.zcash.ui.design.R.string.general_service_unavailable)
                } else {
                    stringRes(co.electriccoin.zcash.ui.design.R.string.general_unexpected_error)
                },
            subtitle =
                if (isServiceUnavailableError) {
                    stringRes(co.electriccoin.zcash.ui.design.R.string.general_please_try_again)
                } else {
                    stringRes(co.electriccoin.zcash.ui.design.R.string.general_check_connection)
                }
        )
    }

    fun createTransactionDetailErrorButtonState(error: Exception?, swapHandle: SwapHandle): ButtonState? {
        val isServiceUnavailableError = error is ResponseException &&
            error.response.status == HttpStatusCode.ServiceUnavailable

        return if (isServiceUnavailableError) {
            null
        } else {
            ButtonState(
                text = stringRes(co.electriccoin.zcash.ui.design.R.string.general_try_again),
                onClick = { swapHandle.requestReload() },
                style = ButtonStyle.DESTRUCTIVE1
            )
        }
    }

    fun createTransactionDetailQuoteHeaderState(
        swap: SwapQuoteStatus?,
        originAsset: SwapAsset?,
        destinationAsset: SwapAsset?
    ): SwapQuoteHeaderState {
        if (swap == null) return SwapQuoteHeaderState(null, null)

        return SwapQuoteHeaderState(
            from =
                SwapTokenAmountState(
                    bigIcon = originAsset?.getQuoteTokenIcon(),
                    smallIcon = originAsset?.getQuoteChainIcon(isOriginAsset = true),
                    title = stringResByDynamicNumber(swap.amountInFormatted),
                    subtitle = stringResByDynamicCurrencyNumber(swap.amountInUsd, FiatCurrency.USD.symbol)
                ),
            to =
                SwapTokenAmountState(
                    bigIcon = destinationAsset?.getQuoteTokenIcon(),
                    smallIcon = destinationAsset?.getQuoteChainIcon(isOriginAsset = false),
                    title = stringResByDynamicNumber(swap.amountOutFormatted),
                    subtitle = stringResByDynamicCurrencyNumber(swap.amountOutUsd, FiatCurrency.USD.symbol)
                )
        )
    }
}