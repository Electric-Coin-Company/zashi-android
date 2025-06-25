package co.electriccoin.zcash.ui.screen.swap.quote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.datasource.RegularTransactionProposal
import co.electriccoin.zcash.ui.common.model.NearSwapQuote
import co.electriccoin.zcash.ui.common.repository.SwapQuoteData
import co.electriccoin.zcash.ui.common.usecase.CancelSwapQuoteUseCase
import co.electriccoin.zcash.ui.common.usecase.CancelSwapUseCase
import co.electriccoin.zcash.ui.common.usecase.ConfirmProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedSwapAssetUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSlippageUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSwapModeUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSwapQuoteUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveProposalUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SwapQuoteVM(
    getSwapQuote: GetSwapQuoteUseCase,
    observeProposal: ObserveProposalUseCase,
    getSwapMode: GetSwapModeUseCase,
    getSlippage: GetSlippageUseCase,
    getSelectedSwapAsset: GetSelectedSwapAssetUseCase,
    private val cancelSwapQuote: CancelSwapQuoteUseCase,
    private val cancelSwap: CancelSwapUseCase,
    private val swapQuoteSuccessMapper: NearSwapQuoteSuccessMapper,
    private val confirmProposal: ConfirmProposalUseCase,
) : ViewModel() {

    val state: StateFlow<SwapQuoteState?> = combine(
        getSlippage.observe(),
        getSwapMode.observe(),
        getSwapQuote.observe().filterNotNull(),
        observeProposal.observeNullable(),
        getSelectedSwapAsset.observe().filterNotNull()
    ) { slippage, mode, quote, proposal, asset ->
        when (quote) {
            SwapQuoteData.Loading -> null
            is SwapQuoteData.Error -> SwapQuoteState.Error(
                icon = imageRes(R.drawable.ic_swap_quote_error),
                title = stringRes("Quote Unavailable"),
                subtitle = stringRes("We tried but couldn’t get a quote for a payment with your parameters. You can try to adjust the payment details or try again later."),
                negativeButton = ButtonState(
                    text = stringRes("Cancel payment"),
                    onClick = ::onCancelPaymentClick
                ),
                positiveButton = ButtonState(
                    text = stringRes("Edit payment"),
                    onClick = ::onEditPaymentClick
                ),
                onBack = ::onBackDuringError
            )

            is SwapQuoteData.Success -> {
                if (proposal !is RegularTransactionProposal) {
                    null
                } else {
                    when (quote.quote) {
                        is NearSwapQuote -> swapQuoteSuccessMapper.createState(
                            mode = mode,
                            quote = quote.quote,
                            proposal = proposal,
                            slippage = slippage,
                            onBack = ::onBack,
                            onSubmitQuoteClick = ::onSubmitQuoteClick,
                            destinationAsset = asset,
                        )
                    }
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
        initialValue = null
    )

    private fun onEditPaymentClick() = cancelSwapQuote()

    private fun onBack() = cancelSwapQuote()

    private fun onBackDuringError() = cancelSwapQuote()

    private fun onSubmitQuoteClick() = viewModelScope.launch { confirmProposal() }

    private fun onCancelPaymentClick() = cancelSwap()
}
