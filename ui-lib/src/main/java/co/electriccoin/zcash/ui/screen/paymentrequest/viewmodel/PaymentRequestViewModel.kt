package co.electriccoin.zcash.ui.screen.paymentrequest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.SdkSynchronizer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.Proposal
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.provider.GetMonetarySeparatorProvider
import co.electriccoin.zcash.ui.common.usecase.GetSpendingKeyUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSynchronizerUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveAddressBookContactsUseCase
import co.electriccoin.zcash.ui.common.viewmodel.AuthenticationViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.paymentrequest.model.PaymentRequestArguments
import co.electriccoin.zcash.ui.screen.paymentrequest.model.PaymentRequestStage
import co.electriccoin.zcash.ui.screen.paymentrequest.model.PaymentRequestState
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SubmitResult
import co.electriccoin.zcash.ui.screen.sendconfirmation.viewmodel.CreateTransactionsViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PaymentRequestViewModel(
    private val arguments: PaymentRequestArguments,
    private val authenticationViewModel: AuthenticationViewModel,
    private val createTransactionsViewModel: CreateTransactionsViewModel,
    getMonetarySeparators: GetMonetarySeparatorProvider,
    private val getSpendingKeyUseCase: GetSpendingKeyUseCase,
    private val getSynchronizer: GetSynchronizerUseCase,
    walletViewModel: WalletViewModel,
    observeAddressBookContacts: ObserveAddressBookContactsUseCase,
) : ViewModel() {

    private val stage = MutableStateFlow<PaymentRequestStage>(PaymentRequestStage.Initial)

    internal val state =
        combine(
            walletViewModel.exchangeRateUsd,
            observeAddressBookContacts(),
            stage
        ) { rate, contacts, currentStage ->
            PaymentRequestState.Prepared(
                arguments = arguments,
                contact = contacts?.find { it.address == arguments.address?.address },
                exchangeRateState = rate,
                monetarySeparators = getMonetarySeparators(),
                onAddToContacts = { onAddToContacts(it) },
                onClose = ::onClose,
                onSend = { onSend(it) },
                stage = currentStage,
                zecSend = arguments.toZecSend(),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = PaymentRequestState.Loading
        )

    internal val closeNavigationCommand = MutableSharedFlow<Unit>()

    internal val addContactNavigationCommand = MutableSharedFlow<String>()

    internal val homeNavigationCommand = MutableSharedFlow<Unit>()

    internal val authenticationNavigationCommand = MutableSharedFlow<Proposal>()

    internal fun onClose() = viewModelScope.launch {
        closeNavigationCommand.emit(Unit)
    }

    private fun onHome() = viewModelScope.launch {
        homeNavigationCommand.emit(Unit)
    }

    private fun onAddToContacts(address: String) = viewModelScope.launch {
        addContactNavigationCommand.emit(address)
    }

    private fun onSend(proposal: Proposal) = viewModelScope.launch {
         authenticationViewModel.isSendFundsAuthenticationRequired
             .filterNotNull()
             .collect { isProtected ->
                 if (isProtected) {
                     authenticationNavigationCommand.emit(proposal)
                 } else {
                     onSendAllowed(proposal)
                 }
             }
    }

    internal fun onSendAllowed(proposal: Proposal) = viewModelScope.launch {
        runSendFundsAction(
            createTransactionsViewModel = createTransactionsViewModel,
            // The not-null assertion operator is necessary here even if we check its
            // nullability before due to property is declared in different module. See more
            // details on the Kotlin forum
            proposal = proposal,
            spendingKey = getSpendingKeyUseCase(),
            synchronizer = getSynchronizer(),
        )
    }

    private suspend fun runSendFundsAction(
        createTransactionsViewModel: CreateTransactionsViewModel,
        proposal: Proposal,
        spendingKey: UnifiedSpendingKey,
        synchronizer: Synchronizer,
    ) {
        stage.value = PaymentRequestStage.Sending

        val submitResult =
            submitTransactions(
                createTransactionsViewModel = createTransactionsViewModel,
                proposal = proposal,
                synchronizer = synchronizer,
                spendingKey = spendingKey
            )

        Twig.debug { "Transactions submitted with result: $submitResult" }

        processSubmissionResult(submitResult = submitResult)
    }

    private suspend fun submitTransactions(
        createTransactionsViewModel: CreateTransactionsViewModel,
        proposal: Proposal,
        synchronizer: Synchronizer,
        spendingKey: UnifiedSpendingKey
    ): SubmitResult {
        Twig.debug { "Sending transactions..." }

        val result =
            createTransactionsViewModel.runCreateTransactions(
                synchronizer = synchronizer,
                spendingKey = spendingKey,
                proposal = proposal
            )

        // Triggering the transaction history and balances refresh to be notified immediately
        // about the wallet's updated state
        (synchronizer as SdkSynchronizer).run {
            refreshTransactions()
            refreshAllBalances()
        }

        return result
    }

    private fun processSubmissionResult(submitResult: SubmitResult) {
        when (submitResult) {
            SubmitResult.Success -> {
                stage.value = PaymentRequestStage.Confirmed
                onHome()
            }
            is SubmitResult.SimpleTrxFailure.SimpleTrxFailureSubmit -> {
                stage.value = PaymentRequestStage.Failure(submitResult.toErrorMessage(), submitResult.toErrorStacktrace())
            }
            is SubmitResult.SimpleTrxFailure.SimpleTrxFailureGrpc -> {
                stage.value = PaymentRequestStage.FailureGrpc
            }
            is SubmitResult.SimpleTrxFailure.SimpleTrxFailureOther -> {
                stage.value = PaymentRequestStage.Failure(submitResult.toErrorMessage(), submitResult.toErrorStacktrace())
            }
            is SubmitResult.MultipleTrxFailure -> {
                Twig.error { "$submitResult is currently unsupported submission result" }
            }
        }
    }
}