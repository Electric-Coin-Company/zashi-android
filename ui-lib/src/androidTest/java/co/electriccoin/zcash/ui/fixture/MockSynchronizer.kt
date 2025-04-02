package co.electriccoin.zcash.ui.fixture

import android.content.Context
import cash.z.ecc.android.sdk.CloseableSynchronizer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.block.processor.CompactBlockProcessor
import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.AccountBalance
import cash.z.ecc.android.sdk.model.AccountImportSetup
import cash.z.ecc.android.sdk.model.AccountUuid
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.FastestServersResult
import cash.z.ecc.android.sdk.model.ObserveFiatCurrencyResult
import cash.z.ecc.android.sdk.model.Pczt
import cash.z.ecc.android.sdk.model.PercentDecimal
import cash.z.ecc.android.sdk.model.Proposal
import cash.z.ecc.android.sdk.model.TransactionId
import cash.z.ecc.android.sdk.model.TransactionOutput
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient
import cash.z.ecc.android.sdk.model.TransactionSubmitResult
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.android.sdk.type.AddressType
import cash.z.ecc.android.sdk.type.ConsensusMatchType
import cash.z.ecc.android.sdk.type.ServerValidation
import co.electriccoin.lightwallet.client.model.LightWalletEndpoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Mocked Synchronizer that can be used instead of the production SdkSynchronizer e.g. for tests.
 */
@Suppress("TooManyFunctions", "UNUSED_PARAMETER")
internal class MockSynchronizer : CloseableSynchronizer {
    override val exchangeRateUsd: StateFlow<ObserveFiatCurrencyResult>
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")

    override val latestBirthdayHeight: BlockHeight
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")

    override suspend fun getAccounts(): List<Account> {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override val accountsFlow: Flow<List<Account>?>
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")

    override val allTransactions: Flow<List<TransactionOverview>>
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")

    override suspend fun importAccountByUfvk(setup: AccountImportSetup): Account {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override val latestHeight: BlockHeight
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")

    override val network: ZcashNetwork
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")

    override val networkHeight: StateFlow<BlockHeight?>
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")

    override val walletBalances: StateFlow<Map<AccountUuid, AccountBalance>?>
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")

    override var onChainErrorHandler: ((BlockHeight, BlockHeight) -> Any)?
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
        set(value) {}

    override var onCriticalErrorHandler: ((Throwable?) -> Boolean)?
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
        set(value) {}

    override var onProcessorErrorHandler: ((Throwable?) -> Boolean)?
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
        set(value) {}

    override var onSetupErrorHandler: ((Throwable?) -> Boolean)?
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
        set(value) {}

    override var onSubmissionErrorHandler: ((Throwable?) -> Boolean)?
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
        set(value) {}

    override val processorInfo: Flow<CompactBlockProcessor.ProcessorInfo>
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")

    override val progress: Flow<PercentDecimal>
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")

    override val status: Flow<Synchronizer.Status>
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")

    override fun close() {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun createProposedTransactions(
        proposal: Proposal,
        usk: UnifiedSpendingKey
    ): Flow<TransactionSubmitResult> {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} yet.")
    }

    override suspend fun createPcztFromProposal(
        accountUuid: AccountUuid,
        proposal: Proposal
    ): Pczt {
        TODO("Not yet implemented")
    }

    override suspend fun addProofsToPczt(pczt: Pczt): Pczt {
        TODO("Not yet implemented")
    }

    override suspend fun createTransactionFromPczt(
        pcztWithProofs: Pczt,
        pcztWithSignatures: Pczt
    ): Flow<TransactionSubmitResult> {
        TODO("Not yet implemented")
    }

    override fun getMemos(transactionOverview: TransactionOverview): Flow<String> {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override fun getRecipients(transactionOverview: TransactionOverview): Flow<TransactionRecipient> {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun getSaplingAddress(account: Account): String {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun getTransparentAddress(account: Account): String {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun getTransparentBalance(tAddr: String): Zatoshi {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun getUnifiedAddress(account: Account): String {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun isValidShieldedAddr(address: String): Boolean {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun isValidTexAddr(address: String): Boolean {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun isValidTransparentAddr(address: String): Boolean {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun isValidUnifiedAddr(address: String): Boolean {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun proposeShielding(
        account: Account,
        shieldingThreshold: Zatoshi,
        memo: String,
        transparentReceiver: String?
    ): Proposal? {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} yet.")
    }

    override suspend fun proposeTransfer(
        account: Account,
        recipient: String,
        amount: Zatoshi,
        memo: String
    ): Proposal {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} yet.")
    }

    override suspend fun proposeFulfillingPaymentUri(
        account: Account,
        uri: String
    ): Proposal {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} yet.")
    }

    override suspend fun refreshExchangeRateUsd() {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun refreshUtxos(
        account: Account,
        since: BlockHeight
    ): Int? {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun rewindToNearestHeight(height: BlockHeight): BlockHeight? {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun validateAddress(address: String): AddressType = AddressType.Unified

    override suspend fun validateConsensusBranch(): ConsensusMatchType {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun validateServerEndpoint(
        context: Context,
        endpoint: LightWalletEndpoint
    ): ServerValidation {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun getExistingDataDbFilePath(
        context: Context,
        network: ZcashNetwork,
        alias: String
    ): String {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun getFastestServers(
        context: Context,
        servers: List<LightWalletEndpoint>
    ): Flow<FastestServersResult> {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun getTransactionOutputs(transactionOverview: TransactionOverview): List<TransactionOutput> {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun getTransactions(accountUuid: AccountUuid): Flow<List<TransactionOverview>> {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override fun getTransactionsByMemoSubstring(query: String): Flow<List<TransactionId>> {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun pcztRequiresSaplingProofs(pczt: Pczt): Boolean {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun redactPcztForSigner(pczt: Pczt): Pczt {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    companion object {
        fun new() = MockSynchronizer()
    }
}
