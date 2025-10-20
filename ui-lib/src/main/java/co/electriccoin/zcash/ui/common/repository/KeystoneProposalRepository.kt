package co.electriccoin.zcash.ui.common.repository

import cash.z.ecc.android.sdk.exception.PcztException
import cash.z.ecc.android.sdk.model.Pczt
import cash.z.ecc.android.sdk.model.ZecSend
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.ExactInputSwapTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.ExactOutputSwapTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.ProposalDataSource
import co.electriccoin.zcash.ui.common.datasource.SwapDataSource
import co.electriccoin.zcash.ui.common.datasource.SwapTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.TransactionProposal
import co.electriccoin.zcash.ui.common.datasource.TransactionProposalNotCreatedException
import co.electriccoin.zcash.ui.common.datasource.Zip321TransactionProposal
import co.electriccoin.zcash.ui.common.model.SubmitResult
import co.electriccoin.zcash.ui.common.model.SwapQuote
import co.electriccoin.zcash.ui.common.model.SwapStatus
import com.keystone.sdk.KeystoneSDK
import com.keystone.sdk.KeystoneZcashSDK
import com.sparrowwallet.hummingbird.UR
import com.sparrowwallet.hummingbird.UREncoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("TooManyFunctions")
interface KeystoneProposalRepository {
    val transactionProposal: Flow<TransactionProposal?>

    val submitState: Flow<SubmitProposalState?>

    @Throws(TransactionProposalNotCreatedException::class)
    suspend fun createProposal(zecSend: ZecSend)

    @Throws(TransactionProposalNotCreatedException::class)
    suspend fun createExactInputSwapProposal(zecSend: ZecSend, quote: SwapQuote): ExactInputSwapTransactionProposal

    @Throws(TransactionProposalNotCreatedException::class)
    suspend fun createExactOutputSwapProposal(zecSend: ZecSend, quote: SwapQuote): ExactOutputSwapTransactionProposal

    @Throws(TransactionProposalNotCreatedException::class)
    suspend fun createZip321Proposal(zip321Uri: String): Zip321TransactionProposal

    @Throws(TransactionProposalNotCreatedException::class)
    suspend fun createShieldProposal()

    @Throws(PcztException.CreatePcztFromProposalException::class)
    suspend fun createPCZTFromProposal()

    @Throws(IllegalStateException::class)
    suspend fun createPCZTEncoder(): UREncoder

    @Throws(ParsePCZTException::class)
    suspend fun parsePCZT(ur: UR)

    fun extractPCZT()

    fun clear()

    suspend fun getTransactionProposal(): TransactionProposal

    fun getProposalPCZT(): Pczt?
}

class ParsePCZTException : Exception()

sealed interface SubmitProposalState {
    data object Submitting : SubmitProposalState

    data class Result(
        val submitResult: SubmitResult
    ) : SubmitProposalState
}

@Suppress("TooManyFunctions")
class KeystoneProposalRepositoryImpl(
    private val accountDataSource: AccountDataSource,
    private val proposalDataSource: ProposalDataSource,
    private val swapDataSource: SwapDataSource,
    private val metadataRepository: MetadataRepository
) : KeystoneProposalRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val transactionProposal = MutableStateFlow<TransactionProposal?>(null)

    override val submitState = MutableStateFlow<SubmitProposalState?>(null)

    private val pcztWithProofs = MutableStateFlow(PcztState(isLoading = false, pczt = null))
    private var proposalPczt: Pczt? = null
    private var pcztWithSignatures: Pczt? = null

    private val keystoneSDK: KeystoneSDK by lazy { KeystoneSDK() }
    private val keystoneZcashSDK: KeystoneZcashSDK
        get() = keystoneSDK.zcash
    private var pcztWithProofsJob: Job? = null
    private var extractPCZTJob: Job? = null

    override suspend fun createProposal(zecSend: ZecSend) {
        createProposalInternal {
            proposalDataSource.createProposal(
                account = accountDataSource.getSelectedAccount(),
                send = zecSend
            )
        }
    }

    override suspend fun createExactInputSwapProposal(
        zecSend: ZecSend,
        quote: SwapQuote,
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
        quote: SwapQuote,
    ): ExactOutputSwapTransactionProposal =
        createProposalInternal {
            proposalDataSource.createExactOutputProposal(
                account = accountDataSource.getSelectedAccount(),
                send = zecSend,
                quote = quote
            )
        }

    override suspend fun createZip321Proposal(zip321Uri: String): Zip321TransactionProposal =
        createProposalInternal {
            proposalDataSource.createZip321Proposal(
                account = accountDataSource.getSelectedAccount(),
                zip321Uri = zip321Uri
            )
        }

    override suspend fun createShieldProposal() {
        createProposalInternal {
            proposalDataSource.createShieldProposal(
                account = accountDataSource.getSelectedAccount(),
            )
        }
    }

    override suspend fun createPCZTFromProposal() {
        val result =
            proposalDataSource.createPcztFromProposal(
                account = accountDataSource.getSelectedAccount(),
                proposal = getTransactionProposal().proposal
            )
        proposalPczt = result
        addProofsToPczt(result)
    }

    private fun addProofsToPczt(proposalPczt: Pczt) {
        pcztWithProofsJob?.cancel()
        pcztWithProofsJob =
            scope.launch {
                pcztWithProofs.update { PcztState(isLoading = true, pczt = null) }
                try {
                    val result = proposalDataSource.addProofsToPczt(proposalPczt.clonePczt())
                    pcztWithProofs.update { PcztState(isLoading = false, pczt = result) }
                } catch (_: PcztException.AddProofsToPcztException) {
                    pcztWithProofs.update { PcztState(isLoading = false, pczt = null) }
                }
            }
    }

    @Suppress("UseCheckOrError")
    override suspend fun createPCZTEncoder(): UREncoder =
        withContext(Dispatchers.IO) {
            val pczt = proposalPczt ?: throw IllegalStateException("Proposal not created")
            val redactedPczt = proposalDataSource.redactPcztForSigner(pczt.clonePczt())
            keystoneZcashSDK.generatePczt(pczt = redactedPczt.toByteArray())
        }

    override suspend fun parsePCZT(ur: UR) =
        withContext(Dispatchers.IO) {
            try {
                pcztWithSignatures = Pczt(keystoneZcashSDK.parsePczt(ur))
            } catch (_: Exception) {
                throw ParsePCZTException()
            }
        }

    @Suppress("UseCheckOrError", "ThrowingExceptionsWithoutMessageOrCause")
    override fun extractPCZT() {
        extractPCZTJob?.cancel()
        extractPCZTJob =
            scope.launch {
                val transactionProposal = transactionProposal.value
                val pcztWithSignatures = pcztWithSignatures

                if (transactionProposal == null || pcztWithSignatures == null) {
                    submitState.update {
                        SubmitProposalState.Result(
                            SubmitResult.Failure(
                                txIds = emptyList(),
                                code = 0,
                                description = "Transaction proposal is null"
                            )
                        )
                    }
                } else {
                    submitState.update { SubmitProposalState.Submitting }
                    val pcztWithProofs = pcztWithProofs.filter { !it.isLoading }.first().pczt
                    if (pcztWithProofs == null) {
                        submitState.update {
                            SubmitProposalState.Result(
                                SubmitResult.Failure(
                                    txIds = emptyList(),
                                    code = 0,
                                    description = "PCZT with proofs is null"
                                )
                            )
                        }
                    } else {
                        val result =
                            proposalDataSource.submitTransaction(
                                pcztWithProofs = pcztWithProofs,
                                pcztWithSignatures = pcztWithSignatures
                            )
                        runSwapPipeline(transactionProposal, result)
                        submitState.update { SubmitProposalState.Result(result) }
                    }
                }
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
                metadataRepository.markTxAsSwap(
                    depositAddress = transactionProposal.destination.address,
                    provider = transactionProposal.quote.provider,
                    totalFees = transactionProposal.totalFees,
                    totalFeesUsd = transactionProposal.totalFeesUsd,
                    amountOutFormatted = transactionProposal.quote.amountOutFormatted,
                    mode = transactionProposal.quote.mode,
                    status = SwapStatus.PENDING,
                    origin = transactionProposal.quote.originAsset,
                    destination = transactionProposal.quote.destinationAsset
                )
                txIds.forEach { submitDepositTransaction(it, transactionProposal) }
            }
        }

    override suspend fun getTransactionProposal(): TransactionProposal = transactionProposal.filterNotNull().first()

    override fun getProposalPCZT(): Pczt? = proposalPczt

    override fun clear() {
        extractPCZTJob?.cancel()
        extractPCZTJob = null
        pcztWithProofsJob?.cancel()
        pcztWithProofsJob = null
        pcztWithProofs.update { PcztState(isLoading = false, pczt = null) }

        transactionProposal.update { null }
        submitState.update { null }
        proposalPczt = null
        pcztWithSignatures = null
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

private data class PcztState(
    val isLoading: Boolean,
    val pczt: Pczt?
)
