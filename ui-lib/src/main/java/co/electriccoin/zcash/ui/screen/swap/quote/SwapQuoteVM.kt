package co.electriccoin.zcash.ui.screen.swap.quote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.datasource.QuoteLowAmountException
import co.electriccoin.zcash.ui.common.datasource.SwapTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.TransactionProposal
import co.electriccoin.zcash.ui.common.model.CompositeSwapQuote
import co.electriccoin.zcash.ui.common.provider.ResponseWithErrorException
import co.electriccoin.zcash.ui.common.usecase.CancelSwapQuoteUseCase
import co.electriccoin.zcash.ui.common.usecase.CancelSwapUseCase
import co.electriccoin.zcash.ui.common.usecase.ConfirmProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.GetCompositeSwapQuoteUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.SwapQuoteCompositeData
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal

internal class SwapQuoteVM(
    observeProposal: ObserveProposalUseCase,
    getCompositeSwapQuote: GetCompositeSwapQuoteUseCase,
    private val cancelSwapQuote: CancelSwapQuoteUseCase,
    private val cancelSwap: CancelSwapUseCase,
    private val swapQuoteSuccessMapper: SwapQuoteVMMapper,
    private val confirmProposal: ConfirmProposalUseCase,
) : ViewModel() {
    val state: StateFlow<SwapQuoteState?> =
        combine(
            getCompositeSwapQuote.observe(),
            observeProposal.observeNullable(),
        ) { quote, proposal->
            when (quote) {
                SwapQuoteCompositeData.Loading -> null
                is SwapQuoteCompositeData.Error -> createErrorState(quote)
                is SwapQuoteCompositeData.Success ->
                    createState(
                        proposal = proposal,
                        quote = quote
                    )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    private fun createState(
        proposal: TransactionProposal?,
        quote: SwapQuoteCompositeData.Success
    ): SwapQuoteState.Success? {
        val swapQuote = quote.quote
        return when {
                proposal is SwapTransactionProposal ->
                swapQuoteSuccessMapper.createState(
                    state =
                        NearSwapQuoteInternalState(
                            quote = swapQuote,
                            proposal = proposal,
                        ),
                    onBack = ::onBack,
                    onSubmitQuoteClick = ::onSubmitQuoteClick,
                )

            else -> null
        }
    }

    private fun createErrorState(quote: SwapQuoteCompositeData.Error): SwapQuoteState.Error {
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
    val zatoshiFee: Zatoshi
    val zecFeeUsd: BigDecimal
    val totalZec: BigDecimal
    val totalUsd: BigDecimal
    val quote: CompositeSwapQuote
    val totalFeesZatoshi: Zatoshi
    val totalFeesUsd: BigDecimal
}
