package co.electriccoin.zcash.ui.common.repository

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.model.Memo
import cash.z.ecc.android.sdk.model.Proposal
import cash.z.ecc.android.sdk.model.TransactionSubmitResult
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.model.proposeSend
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.ZashiSpendingKeyDataSource
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SubmitResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.zecdev.zip321.ZIP321
import kotlin.time.Duration.Companion.seconds

interface KeystoneProposalRepository {
    val completeZecSend: Flow<CompleteZecSend?>

    val submitState: Flow<SubmitProposalState?>

    suspend fun createProposal(zecSend: ZecSend): Boolean

    suspend fun createZip321Proposal(zip321Uri: String): Boolean

    suspend fun getCompleteZecSend(): CompleteZecSend

    fun signAndCompleteProposalTemp()

    fun signAndCompleteProposal(zecSendPart2: ZecSend)

    fun clear()
}

sealed interface SubmitProposalState {
    data object Submitting : SubmitProposalState

    data class Result(val submitResult: SubmitResult) : SubmitProposalState
}

class KeystoneProposalRepositoryImpl(
    private val synchronizerProvider: SynchronizerProvider,
    private val accountDataSource: AccountDataSource,
    private val zashiSpendingKeyDataSource: ZashiSpendingKeyDataSource
) : KeystoneProposalRepository {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val completeZecSend = MutableStateFlow<CompleteZecSend?>(null)
    override val submitState = MutableStateFlow<SubmitProposalState?>(null)

    override suspend fun createProposal(zecSend: ZecSend): Boolean = withContext(NonCancellable) {
        val result = runCatching {
            val newProposal = synchronizerProvider.getSynchronizer().proposeSend(
                account = accountDataSource.getKeystoneAccount().sdkAccount,
                send = zecSend
            )

            RegularZecSend(
                destination = zecSend.destination,
                amount = zecSend.amount,
                memo = zecSend.memo,
                proposal = newProposal,
            )
        }.getOrNull()

        completeZecSend.update { result }
        result != null
    }

    override suspend fun createZip321Proposal(zip321Uri: String): Boolean = withContext(NonCancellable) {
        val synchronizer = synchronizerProvider.getSynchronizer()
        val account = accountDataSource.getSelectedAccount()

        val request =
            runCatching {
                // At this point there should by only a valid Zcash address coming
                ZIP321.request(zip321Uri, null)
            }.onFailure {
                Twig.error(it) { "Failed to validate address" }
            }.getOrNull()
        val payment =
            when (request) {
                // We support only one payment currently
                is ZIP321.ParserResult.Request -> {
                    request.paymentRequest.payments[0]
                }

                else -> {
                    completeZecSend.update { null }
                    return@withContext false
                }
            }
        val proposal = runCatching {
            synchronizer.proposeFulfillingPaymentUri(account.sdkAccount, zip321Uri)
        }.getOrNull()

        if (proposal == null) {
            completeZecSend.update { null }
            return@withContext false
        }

        val result = runCatching {
            Zip321ZecSend(
                destination = synchronizer
                    .validateAddress(payment.recipientAddress.value)
                    .toWalletAddress(payment.recipientAddress.value),
                amount = payment.nonNegativeAmount.value.convertZecToZatoshi(),
                memo = Memo(payment.memo?.let { String(it.data, Charsets.UTF_8) } ?: ""),
                proposal = proposal,
            )
        }.getOrNull()

        completeZecSend.update { result }
        return@withContext result != null
    }

    override suspend fun getCompleteZecSend(): CompleteZecSend = completeZecSend.filterNotNull().first()

    override fun signAndCompleteProposalTemp() {
        scope.launch {
            submitState.update { SubmitProposalState.Submitting }
            // delay(5.seconds)
            // submitState.update { SubmitProposalState.Result(SubmitResult.MultipleTrxFailure(listOf())) }
            // delay(5.seconds)
            // submitState.update {
            //     SubmitProposalState.Result(SubmitResult.SimpleTrxFailure.SimpleTrxFailureOther(RuntimeException()))
            // }
            delay(5.seconds)
            submitState.update { SubmitProposalState.Result(SubmitResult.Success) }
        }
    }

    override fun signAndCompleteProposal(zecSendPart2: ZecSend) {
        scope.launch {
            val proposal = completeZecSend.value?.proposal!!
            submitState.update { SubmitProposalState.Submitting }
            val result = submitTransaction(proposal)
            submitState.update { SubmitProposalState.Result(result) }
        }
    }

    override fun clear() {
        completeZecSend.update { null }
        submitState.update { null }
    }

    private suspend fun submitTransaction(
        proposal: Proposal
    ): SubmitResult {
        val synchronizer = synchronizerProvider.getSdkSynchronizer()

        val submitResult =
            runCreateTransactions(
                synchronizer = synchronizer,
                spendingKey = zashiSpendingKeyDataSource.getZashiSpendingKey(),
                proposal = proposal
            )

        synchronizer.refreshTransactions()
        synchronizer.refreshAllBalances()

        return submitResult
    }

    private suspend fun runCreateTransactions(
        synchronizer: Synchronizer,
        spendingKey: UnifiedSpendingKey,
        proposal: Proposal
    ): SubmitResult {
        val submitResults = mutableListOf<TransactionSubmitResult>()

        return runCatching {
            synchronizer.createProposedTransactions(
                proposal = proposal,
                usk = spendingKey
            ).collect { submitResult ->
                Twig.info { "Transaction submit result: $submitResult" }
                submitResults.add(submitResult)
            }
            if (submitResults.find { it is TransactionSubmitResult.Failure } != null) {
                if (submitResults.size == 1) {
                    // The first transaction submission failed - user might just be able to re-submit the transaction
                    // proposal. Simple error pop up is fine then
                    val result = (submitResults[0] as TransactionSubmitResult.Failure)
                    if (result.grpcError) {
                        SubmitResult.SimpleTrxFailure.SimpleTrxFailureGrpc(result)
                    } else {
                        SubmitResult.SimpleTrxFailure.SimpleTrxFailureSubmit(result)
                    }
                } else {
                    // Any subsequent transaction submission failed - user needs to resolve this manually. Multiple
                    // transaction failure screen presented
                    SubmitResult.MultipleTrxFailure(submitResults)
                }
            } else {
                // All transaction submissions were successful
                SubmitResult.Success
            }
        }.onSuccess {
            Twig.debug { "Transactions submitted successfully" }
        }.onFailure {
            Twig.error(it) { "Transactions submission failed" }
        }.getOrElse {
            SubmitResult.SimpleTrxFailure.SimpleTrxFailureOther(it)
        }
    }

    private suspend fun AddressType.toWalletAddress(value: String) =
        when (this) {
            AddressType.Unified -> WalletAddress.Unified.new(value)
            AddressType.Shielded -> WalletAddress.Sapling.new(value)
            AddressType.Transparent -> WalletAddress.Transparent.new(value)
            else -> error("Invalid address type")
        }
}

sealed interface CompleteZecSend {
    val destination: WalletAddress
    val amount: Zatoshi
    val memo: Memo
    val proposal: Proposal
}

data class RegularZecSend(
    override val destination: WalletAddress,
    override val amount: Zatoshi,
    override val memo: Memo,
    override val proposal: Proposal
) : CompleteZecSend

data class Zip321ZecSend(
    override val destination: WalletAddress,
    override val amount: Zatoshi,
    override val memo: Memo,
    override val proposal: Proposal
) : CompleteZecSend
