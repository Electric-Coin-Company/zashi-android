package co.electriccoin.zcash.ui.common.datasource

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.exception.PcztException
import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.model.Memo
import cash.z.ecc.android.sdk.model.Pczt
import cash.z.ecc.android.sdk.model.Proposal
import cash.z.ecc.android.sdk.model.TransactionSubmitResult
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.model.proposeSend
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.model.SubmitResult
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.screen.balances.DEFAULT_SHIELDING_THRESHOLD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.zecdev.zip321.ZIP321

interface ProposalDataSource {
    @Throws(TransactionProposalNotCreatedException::class)
    suspend fun createProposal(
        account: WalletAccount,
        send: ZecSend
    ): RegularTransactionProposal

    @Throws(TransactionProposalNotCreatedException::class)
    suspend fun createZip321Proposal(
        account: WalletAccount,
        zip321Uri: String
    ): Zip321TransactionProposal

    @Throws(TransactionProposalNotCreatedException::class)
    suspend fun createShieldProposal(account: WalletAccount): ShieldTransactionProposal

    @Throws(PcztException.CreatePcztFromProposalException::class)
    suspend fun createPcztFromProposal(
        account: WalletAccount,
        proposal: Proposal
    ): Pczt

    @Throws(PcztException.AddProofsToPcztException::class)
    suspend fun addProofsToPczt(pczt: Pczt): Pczt

    suspend fun submitTransaction(
        pcztWithProofs: Pczt,
        pcztWithSignatures: Pczt
    ): SubmitResult

    suspend fun submitTransaction(
        proposal: Proposal,
        usk: UnifiedSpendingKey
    ): SubmitResult

    @Throws(PcztException.RedactPcztForSignerException::class)
    suspend fun redactPcztForSigner(pczt: Pczt): Pczt
}

class TransactionProposalNotCreatedException(
    reason: Exception
) : Exception(reason)

@Suppress("TooManyFunctions")
class ProposalDataSourceImpl(
    private val synchronizerProvider: SynchronizerProvider,
) : ProposalDataSource {
    override suspend fun createProposal(
        account: WalletAccount,
        send: ZecSend
    ): RegularTransactionProposal =
        withContext(Dispatchers.IO) {
            getOrThrow {
                RegularTransactionProposal(
                    destination = send.destination,
                    amount = send.amount,
                    memo = send.memo,
                    proposal =
                        synchronizerProvider.getSynchronizer().proposeSend(
                            account = account.sdkAccount,
                            send = send
                        ),
                )
            }
        }

    override suspend fun createZip321Proposal(
        account: WalletAccount,
        zip321Uri: String
    ): Zip321TransactionProposal =
        withContext(Dispatchers.IO) {
            val synchronizer = synchronizerProvider.getSynchronizer()

            val request =
                getOrThrow {
                    ZIP321.request(uriString = zip321Uri, validatingRecipients = null)
                }
            val payment =
                when (request) {
                    is ZIP321.ParserResult.Request -> request.paymentRequest.payments[0]
                    else -> throw TransactionProposalNotCreatedException(IllegalArgumentException("Invalid ZIP321 URI"))
                }

            getOrThrow {
                Zip321TransactionProposal(
                    destination =
                        synchronizer
                            .validateAddress(payment.recipientAddress.value)
                            .toWalletAddress(payment.recipientAddress.value),
                    amount = payment.nonNegativeAmount.value.convertZecToZatoshi(),
                    memo = Memo(payment.memo?.let { String(it.data, Charsets.UTF_8) } ?: ""),
                    proposal = synchronizer.proposeFulfillingPaymentUri(account = account.sdkAccount, uri = zip321Uri),
                )
            }
        }

    override suspend fun createShieldProposal(account: WalletAccount): ShieldTransactionProposal =
        withContext(Dispatchers.IO) {
            getOrThrow {
                val newProposal =
                    synchronizerProvider.getSynchronizer().proposeShielding(
                        account = account.sdkAccount,
                        shieldingThreshold = Zatoshi(DEFAULT_SHIELDING_THRESHOLD),
                        // Using empty string for memo to clear the default memo prefix value defined in the SDK
                        memo = "",
                        // Using null will select whichever of the account's trans. receivers has funds to shield
                        transparentReceiver = null
                    )

                newProposal
                    ?.let { ShieldTransactionProposal(proposal = it) }
                    ?: throw NullPointerException("transparent balance  is zero or below `shieldingThreshold`")
            }
        }

    override suspend fun createPcztFromProposal(
        account: WalletAccount,
        proposal: Proposal
    ): Pczt =
        withContext(Dispatchers.IO) {
            val synchronizer = synchronizerProvider.getSynchronizer()
            synchronizer.createPcztFromProposal(
                accountUuid = account.sdkAccount.accountUuid,
                proposal = proposal
            )
        }

    override suspend fun addProofsToPczt(pczt: Pczt): Pczt =
        withContext(Dispatchers.IO) {
            synchronizerProvider
                .getSynchronizer()
                .addProofsToPczt(pczt)
        }

    override suspend fun submitTransaction(
        pcztWithProofs: Pczt,
        pcztWithSignatures: Pczt
    ): SubmitResult =
        submitTransactionInternal {
            it.createTransactionFromPczt(
                pcztWithProofs = pcztWithProofs,
                pcztWithSignatures = pcztWithSignatures,
            )
        }

    override suspend fun submitTransaction(
        proposal: Proposal,
        usk: UnifiedSpendingKey
    ): SubmitResult =
        submitTransactionInternal {
            it.createProposedTransactions(
                proposal = proposal,
                usk = usk,
            )
        }

    override suspend fun redactPcztForSigner(pczt: Pczt): Pczt =
        withContext(Dispatchers.IO) {
            synchronizerProvider
                .getSynchronizer()
                .redactPcztForSigner(pczt)
        }

    private suspend inline fun submitTransactionInternal(
        crossinline block: suspend (Synchronizer) -> Flow<TransactionSubmitResult>
    ): SubmitResult =
        withContext(Dispatchers.IO) {
            val synchronizer = synchronizerProvider.getSdkSynchronizer()

            val submitResults = mutableListOf<TransactionSubmitResult>()

            val result =
                runCatching {
                    block(synchronizer).collect { submitResult ->
                        Twig.info { "Transaction submit result: $submitResult" }
                        submitResults.add(submitResult)
                    }
                    if (submitResults.find { it is TransactionSubmitResult.Failure } != null) {
                        if (submitResults.size == 1) {
                            val result = (submitResults[0] as TransactionSubmitResult.Failure)
                            if (result.grpcError) {
                                SubmitResult.SimpleTrxFailure.SimpleTrxFailureGrpc(result)
                            } else {
                                SubmitResult.SimpleTrxFailure.SimpleTrxFailureSubmit(result)
                            }
                        } else {
                            SubmitResult.MultipleTrxFailure(submitResults)
                        }
                    } else {
                        // All transaction submissions were successful
                        SubmitResult.Success(
                            submitResults
                                .filterIsInstance<TransactionSubmitResult.Success>()
                                .map {
                                    it.txIdString()
                                }
                        )
                    }
                }.onSuccess {
                    Twig.debug { "Transactions submitted successfully" }
                }.onFailure {
                    Twig.error(it) { "Transactions submission failed" }
                }.getOrElse {
                    SubmitResult.SimpleTrxFailure.SimpleTrxFailureOther(it)
                }

            synchronizer.refreshTransactions()
            synchronizer.refreshAllBalances()

            result
        }

    @Suppress("TooGenericExceptionCaught")
    private inline fun <T : Any> getOrThrow(block: () -> T): T =
        try {
            block()
        } catch (e: Exception) {
            throw TransactionProposalNotCreatedException(e)
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
