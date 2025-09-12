package co.electriccoin.zcash.ui.screen.swap.onrampquote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.FiatCurrency
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.SwapQuoteData
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import co.electriccoin.zcash.ui.common.usecase.CancelSwapQuoteUseCase
import co.electriccoin.zcash.ui.common.usecase.CancelSwapUseCase
import co.electriccoin.zcash.ui.common.usecase.CopyToClipboardUseCase
import co.electriccoin.zcash.ui.design.component.BigIconButtonState
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber
import co.electriccoin.zcash.ui.design.util.stringResByNumber
import co.electriccoin.zcash.ui.screen.swap.info.SwapInfoArgs
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.math.BigDecimal

class ORQuoteVM(
    swapRepository: SwapRepository,
    private val cancelSwap: CancelSwapUseCase,
    private val cancelSwapQuote: CancelSwapQuoteUseCase,
    private val copyToClipboard: CopyToClipboardUseCase,
    private val navigationRouter: NavigationRouter
) : ViewModel() {
    val state: StateFlow<ORQuoteState?> = swapRepository.quote
        .filterIsInstance<SwapQuoteData.Success>()
        .map { it.quote }
        .map { quote ->
            ORQuoteState(
                onBack = ::onBack,
                info = IconButtonState(
                    icon = co.electriccoin.zcash.ui.R.drawable.ic_help,
                    onClick = ::onInfoClick
                ),
                bigIcon = quote.originAsset.tokenIcon,
                smallIcon = quote.originAsset.chainIcon,
                amount = stringResByNumber(quote.amountInFormatted),
                amountFiat = stringResByDynamicCurrencyNumber(quote.amountInUsd, FiatCurrency.USD.symbol),
                onAmountClick = { onAmountClick(quote.amountInFormatted) },
                qr = quote.depositAddress,
                copyButton = BigIconButtonState(
                    stringRes("Copy"),
                    co.electriccoin.zcash.ui.R.drawable.ic_copy,
                    onClick = { onCopyAddressClick(quote.depositAddress) }
                ),
                shareButton = BigIconButtonState(
                    stringRes("Share QR"),
                    co.electriccoin.zcash.ui.R.drawable.ic_qr_code_other
                ) {},
                footer = stringRes(
                    "Use your ${quote.originAsset.tokenTicker} on ${quote.originAsset.chainTicker} wallet \n" +
                        "to deposit funds. Depositing other assets may result in loss of funds."
                ),
                primaryButton = ButtonState(
                    stringRes("I’ve sent the funds"),
                    onClick = ::onSentFundsClick
                ),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    private fun onAmountClick(amountInFormatted: BigDecimal) = copyToClipboard(
        tag = "deposit address",
        value = amountInFormatted.toPlainString()
    )

    private fun onCopyAddressClick(depositAddress: String) = copyToClipboard(
        tag = "deposit address",
        value = depositAddress
    )

    private fun onBack() = cancelSwapQuote()

    private fun onSentFundsClick() = cancelSwap()

    private fun onInfoClick() = navigationRouter.forward(SwapInfoArgs)
}