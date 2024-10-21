package co.electriccoin.zcash.ui.screen.paymentrequest.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.SdkSynchronizer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.Proposal
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.R
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
import co.electriccoin.zcash.ui.screen.support.model.SupportInfo
import co.electriccoin.zcash.ui.screen.support.model.SupportInfoType
import co.electriccoin.zcash.ui.screen.support.viewmodel.SupportViewModel
import co.electriccoin.zcash.ui.util.EmailUtil
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PaymentRequestViewModel(
    private val application: Application,
    private val arguments: PaymentRequestArguments,
    private val authenticationViewModel: AuthenticationViewModel,
    private val createTransactionsViewModel: CreateTransactionsViewModel,
    getMonetarySeparators: GetMonetarySeparatorProvider,
    private val getSpendingKeyUseCase: GetSpendingKeyUseCase,
    private val getSynchronizer: GetSynchronizerUseCase,
    private val supportViewModel: SupportViewModel,
    walletViewModel: WalletViewModel,
    observeAddressBookContacts: ObserveAddressBookContactsUseCase,
) : ViewModel() {

    private val stage = MutableStateFlow<PaymentRequestStage>(PaymentRequestStage.Initial)

    internal val state =
        combine(
            walletViewModel.exchangeRateUsd,
            observeAddressBookContacts(),
            stage,
            supportViewModel.supportInfo.mapNotNull { it },
        ) { rate, contacts, currentStage, supportInfo ->
            PaymentRequestState.Prepared(
                arguments = arguments,
                contact = contacts?.find { it.address == arguments.address?.address },
                exchangeRateState = rate,
                monetarySeparators = getMonetarySeparators(),
                onAddToContacts = { onAddToContacts(it) },
                onBack = ::onBack,
                onClose = ::onClose,
                onContactSupport = { message -> onContactSupport(message, supportInfo) },
                onSend = { onSend(it) },
                stage = currentStage,
                zecSend = arguments.toZecSend(),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = PaymentRequestState.Loading
        )

    internal val backNavigationCommand = MutableSharedFlow<Unit>()

    internal val closeNavigationCommand = MutableSharedFlow<Unit>()

    internal val addContactNavigationCommand = MutableSharedFlow<String>()

    internal val homeNavigationCommand = MutableSharedFlow<Unit>()

    internal val authenticationNavigationCommand = MutableSharedFlow<Proposal>()

    internal val sendReportFailedNavigationCommand = MutableSharedFlow<Unit>()

    internal fun onClose() = viewModelScope.launch {
        closeNavigationCommand.emit(Unit)
    }

    internal fun onBack() = viewModelScope.launch {
        backNavigationCommand.emit(Unit)
    }

    private fun onHome() = viewModelScope.launch {
        homeNavigationCommand.emit(Unit)
    }

    internal fun setStage(newStage: PaymentRequestStage) = viewModelScope.launch {
        stage.emit(newStage)
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
        setStage(PaymentRequestStage.Sending)

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
                setStage(PaymentRequestStage.Confirmed)
                onHome()
            }
            is SubmitResult.SimpleTrxFailure.SimpleTrxFailureSubmit -> {
                setStage(PaymentRequestStage.Failure(submitResult.toErrorMessage(), submitResult.toErrorStacktrace()))
            }
            is SubmitResult.SimpleTrxFailure.SimpleTrxFailureGrpc -> {
                setStage(PaymentRequestStage.FailureGrpc)
            }
            is SubmitResult.SimpleTrxFailure.SimpleTrxFailureOther -> {
                setStage(PaymentRequestStage.Failure(submitResult.toErrorMessage(), submitResult.toErrorStacktrace()))
            }
            is SubmitResult.MultipleTrxFailure -> {
                Twig.error { "$submitResult is currently unsupported submission result" }
            }
        }
    }

    private fun onContactSupport(message: String?, supportInfo: SupportInfo) = viewModelScope.launch {
        val fullMessage = EmailUtil.formatMessage(
            body = message,
            supportInfo = supportInfo.toSupportString(
                SupportInfoType.entries.toSet()
            )
        )
        val mailIntent =
            EmailUtil.newMailActivityIntent(
                application.applicationContext.getString(R.string.support_email_address),
                application.applicationContext.getString(R.string.app_name),
                fullMessage
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        runCatching {
            application.startActivity(mailIntent)
        }.onSuccess {
            setStage(PaymentRequestStage.Initial)
        }.onFailure {
            setStage(PaymentRequestStage.Initial)
            sendReportFailedNavigationCommand.tryEmit(Unit)
        }
    }
}