package co.electriccoin.zcash.ui.common.repository

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.exception.PcztException
import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.model.Memo
import cash.z.ecc.android.sdk.model.Pczt
import cash.z.ecc.android.sdk.model.Proposal
import cash.z.ecc.android.sdk.model.TransactionSubmitResult
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.model.proposeSend
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.ZashiSpendingKeyDataSource
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.screen.balances.DEFAULT_SHIELDING_THRESHOLD
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SubmitResult
import co.electriccoin.zcash.ui.util.zcash
import com.keystone.sdk.KeystoneSDK
import com.sparrowwallet.hummingbird.UR
import com.sparrowwallet.hummingbird.UREncoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.zecdev.zip321.ZIP321
import kotlin.jvm.Throws

interface KeystoneProposalRepository {
    val transactionProposal: Flow<TransactionProposal?>

    val submitState: Flow<SubmitProposalState?>

    /**
     * 1st step.
     */
    suspend fun createProposal(zecSend: ZecSend)

    /**
     * 1st step for zip 321.
     */
    suspend fun createZip321Proposal(zip321Uri: String)

    /**
     * 1st step for shielding
     */
    suspend fun createShieldProposal()

    /**
     * 2nd step
     */
    @Throws(PcztException.AddProofsToPcztException::class, PcztException.CreatePcztFromProposalException::class)
    suspend fun createPCZTFromProposal()

    /**
     * 3rd step
     */
    // suspend fun addPCZTToProofs()

    /**
     * 4rd step - encode qr
     */
    @Throws(PcztNotCreatedException::class)
    suspend fun createPCZTEncoder(): UREncoder

    /**
     * 4rd step - parse qr
     */
    @Throws(ParsePCZTException::class)
    suspend fun parsePCZT(ur: UR)

    /**
     * 5th step - extract pczt
     */
    @Throws(PcztNotCreatedException::class)
    fun extractPCZT()

    fun clear()

    suspend fun getTransactionProposal(): TransactionProposal
}

class ParsePCZTException: Exception()
class PcztNotCreatedException: Exception()

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

    override val transactionProposal = MutableStateFlow<TransactionProposal?>(null)

    override val submitState = MutableStateFlow<SubmitProposalState?>(null)

    private val keystoneSDK = KeystoneSDK()

    private val keystoneZcashSDK = keystoneSDK.zcash

    private var proposalPczt: Pczt? = null
    private var pcztWithProofs: Pczt? = null
    private var pcztWithSignatures: Pczt? = null

    override suspend fun createProposal(zecSend: ZecSend) {
        val result =
            runCatching {
                val newProposal =
                    synchronizerProvider.getSynchronizer().proposeSend(
                        account = accountDataSource.getSelectedAccount().sdkAccount,
                        send = zecSend
                    )

                RegularTransactionProposal(
                    destination = zecSend.destination,
                    amount = zecSend.amount,
                    memo = zecSend.memo,
                    proposal = newProposal,
                )
            }.getOrNull()

        transactionProposal.update { result }
    }

    override suspend fun createZip321Proposal(zip321Uri: String) {
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
                    transactionProposal.update { null }
                    // return false // TODO
                    return
                }
            }

        val proposal =
            runCatching {
                synchronizer.proposeFulfillingPaymentUri(account.sdkAccount, zip321Uri)
            }.getOrNull()

        if (proposal == null) {
            transactionProposal.update { null }
            // return false // TODO
            return
        }

        val result =
            runCatching {
                Zip321TransactionProposal(
                    destination =
                        synchronizer
                            .validateAddress(payment.recipientAddress.value)
                            .toWalletAddress(payment.recipientAddress.value),
                    amount = payment.nonNegativeAmount.value.convertZecToZatoshi(),
                    memo = Memo(payment.memo?.let { String(it.data, Charsets.UTF_8) } ?: ""),
                    proposal = proposal,
                )
            }.getOrNull()

        transactionProposal.update { result }
        // return result != null // TODO
    }

    override suspend fun createShieldProposal() {
        val account = accountDataSource.getSelectedAccount()

        val result =
            runCatching {
                val newProposal =
                    synchronizerProvider.getSynchronizer().proposeShielding(
                        account = account.sdkAccount,
                        shieldingThreshold = Zatoshi(DEFAULT_SHIELDING_THRESHOLD),
                        // Using empty string for memo to clear the default memo prefix value defined in the SDK
                        memo = "",
                        // Using null will select whichever of the account's trans. receivers has funds to shield
                        transparentReceiver = null
                    )

                newProposal?.let {
                    ShieldTransactionProposal(
                        proposal = it,
                    )
                }
            }.getOrNull()

        transactionProposal.update { result }
    }

    override suspend fun createPCZTFromProposal() {
        val proposalPczt = synchronizerProvider.getSynchronizer().createPcztFromProposal(
            accountUuid = accountDataSource.getSelectedAccount().sdkAccount.accountUuid,
            proposal = getTransactionProposal().proposal
        )

        val pcztWithProofs = synchronizerProvider.getSynchronizer().addProofsToPczt(proposalPczt)

        this.proposalPczt = proposalPczt
        this.pcztWithProofs = pcztWithProofs
    }

    // override suspend fun addPCZTToProofs() {
    //     // TODO keystone PCZT using our sdk
    // }

    override suspend fun createPCZTEncoder(): UREncoder {
        val pczt = proposalPczt ?: throw PcztNotCreatedException()
        return keystoneZcashSDK.generatePczt(pczt.toByteArray())
    }

    override suspend fun parsePCZT(ur: UR) {
        try {
            pcztWithSignatures = Pczt(keystoneZcashSDK.parsePczt(ur))
        } catch (_: Exception) {
            throw ParsePCZTException()
        }
    }

    override fun extractPCZT() {
        val pcztWithProofs = pcztWithProofs ?: throw PcztNotCreatedException()
        val pcztWithSignatures = pcztWithSignatures ?: throw PcztNotCreatedException()

        scope.launch {
            submitState.update { SubmitProposalState.Submitting }
            val result = submitTransaction(
                pcztWithProofs = pcztWithProofs,
                pcztWithSignatures = pcztWithSignatures
            )
            submitState.update { SubmitProposalState.Result(result) }
        }
    }

    override suspend fun getTransactionProposal(): TransactionProposal = transactionProposal.filterNotNull().first()

    override fun clear() {
        transactionProposal.update { null }
        submitState.update { null }
        proposalPczt = null
        pcztWithProofs = null
        pcztWithSignatures = null
    }

    private suspend fun submitTransaction(
        pcztWithProofs: Pczt,
        pcztWithSignatures: Pczt,
    ): SubmitResult {
        val synchronizer = synchronizerProvider.getSdkSynchronizer()

        val submitResult =
            runCreateTransactions(
                synchronizer = synchronizer,
                pcztWithProofs = pcztWithProofs,
                pcztWithSignatures = pcztWithSignatures
            )

        synchronizer.refreshTransactions()
        synchronizer.refreshAllBalances()

        return submitResult
    }

    private suspend fun runCreateTransactions(
        synchronizer: Synchronizer,
        pcztWithProofs: Pczt,
        pcztWithSignatures: Pczt,
    ): SubmitResult {
        val submitResults = mutableListOf<TransactionSubmitResult>()

        return runCatching {
            synchronizer.createTransactionFromPczt(
                pcztWithProofs = pcztWithProofs,
                pcztWithSignatures = pcztWithSignatures
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

sealed interface TransactionProposal {
    val proposal: Proposal
}

sealed interface SendTransactionProposal : TransactionProposal {
    val destination: WalletAddress
    val amount: Zatoshi
    val memo: Memo
}

data class ShieldTransactionProposal(
    override val proposal: Proposal,
) : TransactionProposal

data class RegularTransactionProposal(
    override val destination: WalletAddress,
    override val amount: Zatoshi,
    override val memo: Memo,
    override val proposal: Proposal
) : SendTransactionProposal

data class Zip321TransactionProposal(
    override val destination: WalletAddress,
    override val amount: Zatoshi,
    override val memo: Memo,
    override val proposal: Proposal
) : SendTransactionProposal
