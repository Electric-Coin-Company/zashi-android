package co.electriccoin.zcash.ui.screen.paymentrequest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.Proposal
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.common.provider.GetMonetarySeparatorProvider
import co.electriccoin.zcash.ui.common.usecase.Zip321ProposalFromUriUseCase
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.paymentrequest.model.PaymentRequestArguments
import co.electriccoin.zcash.ui.screen.paymentrequest.model.PaymentRequestState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PaymentRequestViewModel(
    private val arguments: PaymentRequestArguments,
    getMonetarySeparators: GetMonetarySeparatorProvider,
    walletViewModel: WalletViewModel,
    private val zip321ProposalFromUriUseCase: Zip321ProposalFromUriUseCase,
) : ViewModel() {

    internal val state =
        combine(walletViewModel.synchronizer, walletViewModel.exchangeRateUsd) { synchronizer, rate ->
            PaymentRequestState.Prepared(
                arguments = arguments,
                zecSend = arguments.toZecSend(),
                monetarySeparators = getMonetarySeparators(),
                exchangeRateState = rate,
                onClose = ::onClose,
                onSend = {
                    onSend(it)
                },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = PaymentRequestState.Loading
        )

    val closeNavigationCommand = MutableSharedFlow<Unit>()

    val sendParametersCommand = MutableSharedFlow<Proposal>()

    internal fun onClose() = viewModelScope.launch {
        closeNavigationCommand.emit(Unit)
    }

    private fun onSend(zip321Uri: String) = viewModelScope.launch {
        val proposal = zip321ProposalFromUriUseCase(zip321Uri)
        sendParametersCommand.emit(proposal)
    }
}