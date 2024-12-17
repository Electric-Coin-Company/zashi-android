package co.electriccoin.zcash.ui.common.repository

import cash.z.ecc.android.sdk.exception.PcztException
import cash.z.ecc.android.sdk.model.Pczt
import cash.z.ecc.android.sdk.model.ZecSend
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.ProposalDataSource
import co.electriccoin.zcash.ui.common.datasource.TransactionProposal
import co.electriccoin.zcash.ui.common.datasource.TransactionProposalNotCreatedException
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SubmitResult
import co.electriccoin.zcash.ui.util.zcash
import com.keystone.sdk.KeystoneSDK
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

interface KeystoneProposalRepository {
    val transactionProposal: Flow<TransactionProposal?>

    val submitState: Flow<SubmitProposalState?>

    @Throws(TransactionProposalNotCreatedException::class)
    suspend fun createProposal(zecSend: ZecSend)

    @Throws(TransactionProposalNotCreatedException::class)
    suspend fun createZip321Proposal(zip321Uri: String)

    @Throws(TransactionProposalNotCreatedException::class)
    suspend fun createShieldProposal()

    @Throws(PcztException.AddProofsToPcztException::class, PcztException.CreatePcztFromProposalException::class)
    suspend fun createPCZTFromProposal()

    @Throws(IllegalStateException::class)
    suspend fun createPCZTEncoder(): UREncoder

    @Throws(ParsePCZTException::class)
    suspend fun parsePCZT(ur: UR)

    @Throws(IllegalStateException::class)
    fun extractPCZT()

    fun clear()

    suspend fun getTransactionProposal(): TransactionProposal

    fun getProposalPCZT(): Pczt?
}

class ParsePCZTException : Exception()

sealed interface SubmitProposalState {
    data object Submitting : SubmitProposalState

    data class Result(val submitResult: SubmitResult) : SubmitProposalState
}

@Suppress("TooManyFunctions")
class KeystoneProposalRepositoryImpl(
    private val accountDataSource: AccountDataSource,
    private val proposalDataSource: ProposalDataSource,
) : KeystoneProposalRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val transactionProposal = MutableStateFlow<TransactionProposal?>(null)

    override val submitState = MutableStateFlow<SubmitProposalState?>(null)

    private val pcztWithProofs = MutableStateFlow(PcztState(isLoading = false, pczt = null))
    private var proposalPczt: Pczt? = null
    private var pcztWithSignatures: Pczt? = null

    private val keystoneSDK = KeystoneSDK()
    private val keystoneZcashSDK = keystoneSDK.zcash
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
                pcztWithProofs.update {
                    PcztState(isLoading = true, pczt = null)
                }
                // Copy the original PZCT proposal data so we pass one copy to the KeyStone device and the second one
                // to the Rust Backend
                val result =
                    runCatching {
                        proposalDataSource.addProofsToPczt(
                            pczt = proposalPczt.clonePczt()
                        )
                    }.getOrNull()
                pcztWithProofs.update {
                    PcztState(isLoading = false, pczt = result)
                }
            }
    }

    @Suppress("UseCheckOrError")
    override suspend fun createPCZTEncoder(): UREncoder =
        withContext(Dispatchers.IO) {
            val pczt = proposalPczt ?: throw IllegalStateException("Proposal not created")
            keystoneZcashSDK.generatePczt(
                pczt = pczt.clonePczt().toByteArray()
            )
        }

    override suspend fun parsePCZT(ur: UR) =
        withContext(Dispatchers.IO) {
            try {
                pcztWithSignatures = Pczt(keystoneZcashSDK.parsePczt(ur))
            } catch (_: Exception) {
                throw ParsePCZTException()
            }
        }

    @OptIn(ExperimentalStdlibApi::class)
    @Suppress("UseCheckOrError")
    override fun extractPCZT() {
        fun createErrorState(message: String) =
            SubmitProposalState.Result(
                SubmitResult.SimpleTrxFailure.SimpleTrxFailureOther(NullPointerException(message))
            )

        extractPCZTJob?.cancel()
        extractPCZTJob =
            scope.launch {
                val pcztWithSignatures = pcztWithSignatures

                if (pcztWithSignatures == null) {
                    submitState.update { createErrorState("pcztWithSignatures is null") }
                    return@launch
                }

                submitState.update { SubmitProposalState.Submitting }

                val pcztWithProofs = pcztWithProofs.filter { !it.isLoading }.first().pczt

                if (pcztWithProofs == null) {
                    submitState.update { createErrorState("pcztWithProofs is null") }
                    return@launch
                }

                Twig.info { "Pczt: ${proposalPczt?.toByteArray()?.toHexString()}" }
                Twig.info { "PcztWithProofs: ${pcztWithProofs.toByteArray().toHexString()}" }
                Twig.info { "PcztWithSignatures: ${pcztWithSignatures.toByteArray().toHexString()}" }

                val result =
                    proposalDataSource.submitTransaction(
                        pcztWithProofs = pcztWithProofs,
                        pcztWithSignatures = pcztWithSignatures
                    )
                submitState.update { SubmitProposalState.Result(result) }
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

private data class PcztState(
    val isLoading: Boolean,
    val pczt: Pczt?
)
