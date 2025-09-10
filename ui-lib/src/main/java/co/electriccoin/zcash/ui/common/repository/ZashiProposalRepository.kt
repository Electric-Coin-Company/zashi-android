package co.electriccoin.zcash.ui.common.repository

import cash.z.ecc.android.sdk.model.ZecSend
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.ExactInputSwapTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.ExactOutputSwapTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.ProposalDataSource
import co.electriccoin.zcash.ui.common.datasource.RegularTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.SwapDataSource
import co.electriccoin.zcash.ui.common.datasource.SwapTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.TransactionProposal
import co.electriccoin.zcash.ui.common.datasource.TransactionProposalNotCreatedException
import co.electriccoin.zcash.ui.common.datasource.ZashiSpendingKeyDataSource
import co.electriccoin.zcash.ui.common.datasource.Zip321TransactionProposal
import co.electriccoin.zcash.ui.common.model.CompositeSwapQuote
import co.electriccoin.zcash.ui.common.model.SubmitResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface ZashiProposalRepository {
    val transactionProposal: StateFlow<TransactionProposal?>

    val submitState: StateFlow<SubmitProposalState?>

    @Throws(TransactionProposalNotCreatedException::class)
    suspend fun createProposal(zecSend: ZecSend): RegularTransactionProposal

    @Throws(TransactionProposalNotCreatedException::class)
    suspend fun createZip321Proposal(zip321Uri: String): Zip321TransactionProposal

    @Throws(TransactionProposalNotCreatedException::class)
    suspend fun createExactInputSwapProposal(
        zecSend: ZecSend,
        quote: CompositeSwapQuote,
    ): ExactInputSwapTransactionProposal

    @Throws(TransactionProposalNotCreatedException::class)
    suspend fun createExactOutputSwapProposal(
        zecSend: ZecSend,
        quote: CompositeSwapQuote,
    ): ExactOutputSwapTransactionProposal

    @Throws(TransactionProposalNotCreatedException::class)
    suspend fun createShieldProposal()

    @Throws(IllegalStateException::class)
    fun submitTransaction()

    suspend fun getTransactionProposal(): TransactionProposal

    fun clear()
}

@Suppress("TooManyFunctions")
class ZashiProposalRepositoryImpl(
    private val accountDataSource: AccountDataSource,
    private val proposalDataSource: ProposalDataSource,
    private val zashiSpendingKeyDataSource: ZashiSpendingKeyDataSource,
    private val swapDataSource: SwapDataSource,
    private val metadataRepository: MetadataRepository
) : ZashiProposalRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val transactionProposal = MutableStateFlow<TransactionProposal?>(null)

    override val submitState = MutableStateFlow<SubmitProposalState?>(null)
    private var submitJob: Job? = null

    override suspend fun createProposal(zecSend: ZecSend): RegularTransactionProposal =
        createProposalInternal {
            proposalDataSource.createProposal(
                account = accountDataSource.getSelectedAccount(),
                send = zecSend
            )
        }

    override suspend fun createZip321Proposal(zip321Uri: String): Zip321TransactionProposal =
        createProposalInternal {
            proposalDataSource.createZip321Proposal(
                account = accountDataSource.getSelectedAccount(),
                zip321Uri = zip321Uri
            )
        }

    override suspend fun createExactInputSwapProposal(
        zecSend: ZecSend,
        quote: CompositeSwapQuote,
    ): ExactInputSwapTransactionProposal =
        createProposalInternal {
            proposalDataSource.createExactInputProposal(
                account = accountDataSource.getSelectedAccount(),
                send = zecSend,
                quote = quote
            )
        }

    override suspend fun createExactOutputSwapProposal(
        zecSend: ZecSend,
        quote: CompositeSwapQuote,
    ): ExactOutputSwapTransactionProposal =
        createProposalInternal {
            proposalDataSource.createExactOutputProposal(
                account = accountDataSource.getSelectedAccount(),
                send = zecSend,
                quote = quote
            )
        }

    override suspend fun createShieldProposal() {
        createProposalInternal {
            proposalDataSource.createShieldProposal(
                account = accountDataSource.getSelectedAccount(),
            )
        }
    }

    @Suppress("UseCheckOrError")
    override fun submitTransaction() {
        submitJob?.cancel()
        val transactionProposal = transactionProposal.value ?: throw IllegalStateException()
        submitJob =
            scope.launch {
                submitState.update { SubmitProposalState.Submitting }
                val result = proposalDataSource.submitTransaction(
                    proposal = transactionProposal.proposal,
                    usk = zashiSpendingKeyDataSource.getZashiSpendingKey()
                )
                runSwapPipeline(transactionProposal, result)
                submitState.update { SubmitProposalState.Result(result) }
            }
    }

    private fun runSwapPipeline(transactionProposal: TransactionProposal, result: SubmitResult) =
        scope.launch {
            if (transactionProposal is SwapTransactionProposal) {
                val txIds: List<String> =
                    when (result) {
                        is SubmitResult.GrpcFailure -> result.txIds
                        is SubmitResult.Failure -> emptyList()
                        is SubmitResult.Partial -> result.txIds
                        is SubmitResult.Success -> result.txIds
                    }.filter { it.isNotEmpty() }
                val depositAddress = transactionProposal.destination.address
                metadataRepository.markTxAsSwap(
                    depositAddress = depositAddress,
                    provider = transactionProposal.quote.provider,
                    totalFees = transactionProposal.totalFees,
                    totalFeesUsd = transactionProposal.totalFeesUsd
                )
                txIds.forEach { submitDepositTransaction(it, transactionProposal) }
            }
        }

    override suspend fun getTransactionProposal(): TransactionProposal = transactionProposal.filterNotNull().first()

    override fun clear() {
        submitJob?.cancel()
        submitJob = null

        transactionProposal.update { null }
        submitState.update { null }
    }

    private inline fun <T : TransactionProposal> createProposalInternal(block: () -> T): T {
        val proposal =
            try {
                block()
            } catch (e: TransactionProposalNotCreatedException) {
                Twig.error(e) { "Unable to create proposal" }
                transactionProposal.update { null }
                throw e
            }
        transactionProposal.update { proposal }
        return proposal
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun submitDepositTransaction(txId: String, transactionProposal: SwapTransactionProposal) {
        try {
            swapDataSource.submitDepositTransaction(
                txHash = txId,
                depositAddress = transactionProposal.destination.address
            )
        } catch (e: Exception) {
            Twig.error(e) { "Unable to submit deposit transaction" }
        }
    }
}
