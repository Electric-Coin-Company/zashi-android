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
import co.electriccoin.zcash.ui.common.provider.ApplicationStateProvider
import co.electriccoin.zcash.ui.common.provider.ResponseWithErrorException
import co.electriccoin.zcash.ui.common.repository.SwapQuoteData
import co.electriccoin.zcash.ui.common.repository.SwapRepository
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.math.BigDecimal
import kotlin.time.Duration.Companion.seconds

internal class SwapQuoteVM(
    observeProposal: ObserveProposalUseCase,
    getCompositeSwapQuote: GetCompositeSwapQuoteUseCase,
    applicationStateProvider: ApplicationStateProvider,
    private val swapRepository: SwapRepository,
    private val cancelSwapQuote: CancelSwapQuoteUseCase,
    private val cancelSwap: CancelSwapUseCase,
    private val swapQuoteSuccessMapper: SwapQuoteVMMapper,
    private val confirmProposal: ConfirmProposalUseCase,
) : ViewModel() {
    val state: StateFlow<SwapQuoteState?> =
        combine(
            getCompositeSwapQuote.observe(),
            observeProposal.observeNullable(),
        ) { quote, proposal ->
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

    init {
        applicationStateProvider
            .observeOnForeground()
            .onEach {
                val quote = (swapRepository.quote.value as? SwapQuoteData.Success)?.quote ?: return@onEach

                if ((Clock.System.now() - quote.timestamp) >= 10.seconds) {
                    cancelSwapQuote()
                }
            }.launchIn(viewModelScope)
    }

    private fun createState(
        proposal: TransactionProposal?,
        quote: SwapQuoteCompositeData.Success
    ): SwapQuoteState.Success? {
        val swapQuote = quote.quote
        return when {
            proposal is SwapTransactionProposal ->
                swapQuoteSuccessMapper.createState(
                    state = SwapQuoteInternalState(proposal, swapQuote),
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
                        R.string.swap_quote_error_too_low_try_at_least,
                        stringResByDynamicCurrencyNumber(
                            amount = quote.exception.amountFormatted,
                            ticker = quote.exception.asset.tokenTicker
                        )
                    )

                quote.exception is QuoteLowAmountException -> stringRes(R.string.swap_quote_error_too_low_try_higher)
                quote.exception is ResponseWithErrorException -> stringRes(quote.exception.error.message)
                else -> stringRes(R.string.swap_quote_error_getting_quote)
            }

        return SwapQuoteState.Error(
            icon = imageRes(R.drawable.ic_swap_quote_error),
            title = stringRes(R.string.swap_quote_unavailable),
            subtitle = message,
            negativeButton =
                ButtonState(
                    text = stringRes(R.string.swap_quote_cancel_payment),
                    onClick = ::onCancelPaymentClick
                ),
            positiveButton =
                ButtonState(
                    text = stringRes(R.string.swap_quote_edit_payment),
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

internal data class SwapQuoteInternalState(
    val proposal: SwapTransactionProposal,
    val quote: CompositeSwapQuote,
) {
    val zatoshiFee: Zatoshi = proposal.proposal.totalFeeRequired()
    val zecFeeUsd: BigDecimal = quote.getZecFeeUsd(proposal.proposal)
    val totalZec: BigDecimal = quote.getTotalZec(proposal.proposal)
    val totalUsd: BigDecimal = quote.getTotalUsd(proposal.proposal)
    val totalFeesZatoshi: Zatoshi = quote.getTotalFeesZatoshi(proposal.proposal)
    val totalFeesUsd: BigDecimal = quote.getTotalFeesUsd(proposal.proposal)
}
