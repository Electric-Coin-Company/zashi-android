package co.electriccoin.zcash.ui.screen.paymentrequest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.Proposal
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.common.provider.GetMonetarySeparatorProvider
import co.electriccoin.zcash.ui.common.usecase.ObserveAddressBookContactsUseCase
import co.electriccoin.zcash.ui.common.usecase.Zip321ProposalFromUriUseCase
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.paymentrequest.model.PaymentRequestArguments
import co.electriccoin.zcash.ui.screen.paymentrequest.model.PaymentRequestState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PaymentRequestViewModel(
    private val arguments: PaymentRequestArguments,
    getMonetarySeparators: GetMonetarySeparatorProvider,
    walletViewModel: WalletViewModel,
    private val zip321ProposalFromUriUseCase: Zip321ProposalFromUriUseCase,
    observeAddressBookContacts: ObserveAddressBookContactsUseCase
) : ViewModel() {

    internal val state =
        combine(
            walletViewModel.synchronizer.mapNotNull { it },
            walletViewModel.exchangeRateUsd,
            observeAddressBookContacts()
        ) { synchronizer, rate, contacts ->
            PaymentRequestState.Prepared(
                arguments = arguments,
                contact = contacts?.find { it.address == arguments.address?.address },
                exchangeRateState = rate,
                monetarySeparators = getMonetarySeparators(),
                onAddToContacts = { onAddToContacts(it) },
                onClose = ::onClose,
                onSend = { onSend(it, synchronizer) },
                zecSend = arguments.toZecSend(),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = PaymentRequestState.Loading
        )

    internal val closeNavigationCommand = MutableSharedFlow<Unit>()

    internal val addContactNavigationCommand = MutableSharedFlow<String>()

    internal val sendParametersCommand = MutableSharedFlow<Proposal>()

    internal fun onClose() = viewModelScope.launch {
        closeNavigationCommand.emit(Unit)
    }

    internal fun onAddToContacts(address: String) = viewModelScope.launch {
        addContactNavigationCommand.emit(address)
    }

    private fun onSend(zip321Uri: String, synchronizer: Synchronizer) = viewModelScope.launch {
        val proposal = zip321ProposalFromUriUseCase(zip321Uri)
        sendParametersCommand.emit(proposal)
    }
}