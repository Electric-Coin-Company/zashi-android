package co.electriccoin.zcash.ui.common.repository

import cash.z.ecc.android.sdk.model.ZecSend
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.ProposalDataSource
import co.electriccoin.zcash.ui.common.datasource.TransactionProposal
import co.electriccoin.zcash.ui.common.datasource.TransactionProposalNotCreatedException
import co.electriccoin.zcash.ui.common.datasource.ZashiSpendingKeyDataSource
import co.electriccoin.zcash.ui.common.model.SubmitResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface ZashiProposalRepository {
    val transactionProposal: Flow<TransactionProposal?>

    val submitState: Flow<SubmitProposalState?>

    @Throws(TransactionProposalNotCreatedException::class)
    suspend fun createProposal(zecSend: ZecSend)

    @Throws(TransactionProposalNotCreatedException::class)
    suspend fun createZip321Proposal(zip321Uri: String)

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
    private val zashiSpendingKeyDataSource: ZashiSpendingKeyDataSource
) : ZashiProposalRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val transactionProposal = MutableStateFlow<TransactionProposal?>(null)

    override val submitState = MutableStateFlow<SubmitProposalState?>(null)

    private var submitJob: Job? = null

    override suspend fun createProposal(zecSend: ZecSend) {
        createProposalInternal {
            proposalDataSource.createProposal(
                account = accountDataSource.getSelectedAccount(),
                send = zecSend
            )
        }
    }

    override suspend fun createZip321Proposal(zip321Uri: String) {
        createProposalInternal {
            proposalDataSource.createZip321Proposal(
                account = accountDataSource.getSelectedAccount(),
                zip321Uri = zip321Uri
            )
        }
    }

    override suspend fun createShieldProposal() {
        createProposalInternal {
            proposalDataSource.createShieldProposal(
                account = accountDataSource.getSelectedAccount(),
            )
        }
    }

    override fun submitTransaction() {
        fun createErrorState(message: String) =
            SubmitProposalState.Result(
                SubmitResult.SimpleTrxFailure.SimpleTrxFailureOther(NullPointerException(message))
            )

        submitJob?.cancel()
        submitJob =
            scope.launch {
                val proposal = transactionProposal.value?.proposal

                if (proposal == null) {
                    submitState.update { createErrorState("proposal is null") }
                    return@launch
                }

                submitState.update { SubmitProposalState.Submitting }

                val spendingKey =
                    runCatching {
                        zashiSpendingKeyDataSource.getZashiSpendingKey()
                    }.getOrNull()

                if (spendingKey == null) {
                    submitState.update { createErrorState("spendingKey is null") }
                    return@launch
                }

                val result =
                    proposalDataSource.submitTransaction(
                        proposal = proposal,
                        usk = spendingKey
                    )
                submitState.update { SubmitProposalState.Result(result) }
            }
    }

    override suspend fun getTransactionProposal(): TransactionProposal = transactionProposal.filterNotNull().first()

    override fun clear() {
        submitJob?.cancel()
        submitJob = null

        transactionProposal.update { null }
        submitState.update { null }
    }

    private inline fun <T : TransactionProposal> createProposalInternal(block: () -> T) {
        val proposal =
            try {
                block()
            } catch (e: TransactionProposalNotCreatedException) {
                transactionProposal.update { null }
                throw e
            }
        transactionProposal.update { proposal }
    }
}
