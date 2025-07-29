package co.electriccoin.zcash.ui.screen.swap.quote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.datasource.QuoteLowAmountException
import co.electriccoin.zcash.ui.common.datasource.SendTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.SwapTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.TransactionProposal
import co.electriccoin.zcash.ui.common.model.NearSwapAsset
import co.electriccoin.zcash.ui.common.model.NearSwapQuote
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.model.SwapMode
import co.electriccoin.zcash.ui.common.provider.ResponseWithErrorException
import co.electriccoin.zcash.ui.common.repository.SwapQuoteData
import co.electriccoin.zcash.ui.common.usecase.CancelSwapQuoteUseCase
import co.electriccoin.zcash.ui.common.usecase.CancelSwapUseCase
import co.electriccoin.zcash.ui.common.usecase.ConfirmProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedSwapAssetUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSlippageUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSwapModeUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSwapQuoteUseCase
import co.electriccoin.zcash.ui.common.usecase.GetZecSwapAssetUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveProposalUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.combine
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal

internal class SwapQuoteVM(
    getSwapQuote: GetSwapQuoteUseCase,
    observeProposal: ObserveProposalUseCase,
    getSwapMode: GetSwapModeUseCase,
    getSlippage: GetSlippageUseCase,
    getSelectedSwapAsset: GetSelectedSwapAssetUseCase,
    getZecSwapAsset: GetZecSwapAssetUseCase,
    private val cancelSwapQuote: CancelSwapQuoteUseCase,
    private val cancelSwap: CancelSwapUseCase,
    private val swapQuoteSuccessMapper: SwapQuoteVMMapper,
    private val confirmProposal: ConfirmProposalUseCase,
) : ViewModel() {
    val state: StateFlow<SwapQuoteState?> =
        combine(
            getSlippage.observe(),
            getSwapMode.observe(),
            getSwapQuote.observe().filterNotNull(),
            observeProposal.observeNullable(),
            getSelectedSwapAsset.observe().filterNotNull(),
            getZecSwapAsset.observe().filterNotNull()
        ) { slippage, mode, quote, proposal, destinationAsset, originAsset ->
            when (quote) {
                SwapQuoteData.Loading -> null
                is SwapQuoteData.Error -> createErrorState(quote)
                is SwapQuoteData.Success ->
                    createState(
                        proposal = proposal,
                        quote = quote,
                        originAsset = originAsset,
                        slippage = slippage,
                        destinationAsset = destinationAsset,
                        mode = mode
                    )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    private fun createState(
        proposal: TransactionProposal?,
        quote: SwapQuoteData.Success,
        originAsset: SwapAsset,
        slippage: BigDecimal,
        destinationAsset: SwapAsset,
        mode: SwapMode
    ): SwapQuoteState.Success? {
        val swapQuote = quote.quote
        return when {
            swapQuote is NearSwapQuote &&
                proposal is SwapTransactionProposal &&
                originAsset is NearSwapAsset &&
                destinationAsset is NearSwapAsset ->
                swapQuoteSuccessMapper.createState(
                    state =
                        NearSwapQuoteInternalState(
                            originAsset = originAsset,
                            quote = swapQuote,
                            proposal = proposal,
                            slippage = slippage,
                            destinationAsset = destinationAsset,
                            mode = mode,
                        ),
                    onBack = ::onBack,
                    onSubmitQuoteClick = ::onSubmitQuoteClick,
                )

            else -> null
        }
    }

    private fun createErrorState(quote: SwapQuoteData.Error): SwapQuoteState.Error {
        val message =
            when {
                quote.exception is QuoteLowAmountException &&
                    quote.exception.amountFormatted != null ->
                    stringRes(
                        "Amount is too low " +
                            "for bridge, try at least "
                    ) +
                        stringResByDynamicCurrencyNumber(
                            amount = quote.exception.amountFormatted,
                            ticker = quote.exception.asset.tokenTicker
                        )

                quote.exception is QuoteLowAmountException ->
                    stringRes("Amount is too low for bridge, try higher amount.")

                quote.exception is ResponseWithErrorException ->
                    stringRes(quote.exception.error.message)

                else ->
                    stringRes(
                        "We tried but couldn’t get a quote for a payment with your parameters. You can try to adjust the payment details or try again later."
                    )
            }

        return SwapQuoteState.Error(
            icon = imageRes(R.drawable.ic_swap_quote_error),
            title = stringRes("Quote Unavailable"),
            subtitle = message,
            negativeButton =
                ButtonState(
                    text = stringRes("Cancel payment"),
                    onClick = ::onCancelPaymentClick
                ),
            positiveButton =
                ButtonState(
                    text = stringRes("Edit payment"),
                    onClick = ::onEditPaymentClick
                ),
            onBack = ::onBackDuringError
        )
    }

    private fun onEditPaymentClick() = cancelSwapQuote()

    private fun onBack() = cancelSwapQuote()

    private fun onBackDuringError() = cancelSwapQuote()

    private fun onSubmitQuoteClick() = viewModelScope.launch { confirmProposal() }

    private fun onCancelPaymentClick() = cancelSwap()
}

internal sealed interface SwapQuoteInternalState {
    val mode: SwapMode
    val originAsset: SwapAsset
    val destinationAsset: SwapAsset
    val slippage: BigDecimal
    val proposal: SendTransactionProposal

    val zecExchangeRate: BigDecimal
    val zecFee: BigDecimal
    val zecFeeUsd: BigDecimal

    val recipient: String

    val swapProviderFee: Zatoshi

    val swapProviderFeeUsd: BigDecimal

    val amountInZatoshi: Zatoshi
    val amountInZec: BigDecimal
    val amountInDecimals: Int
    val amountInUsd: BigDecimal

    val amountOutFormatted: BigDecimal
    val amountOutDecimals: Int
    val amountOutUsd: BigDecimal

    val totalZec: BigDecimal
    val totalUsd: BigDecimal
}
