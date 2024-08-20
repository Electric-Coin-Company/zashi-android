package co.electriccoin.zcash.ui.fixture

import android.content.Context
import cash.z.ecc.android.sdk.CloseableSynchronizer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.block.processor.CompactBlockProcessor
import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.FastestServersResult
import cash.z.ecc.android.sdk.model.ObserveFiatCurrencyResult
import cash.z.ecc.android.sdk.model.PercentDecimal
import cash.z.ecc.android.sdk.model.Proposal
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient
import cash.z.ecc.android.sdk.model.TransactionSubmitResult
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import cash.z.ecc.android.sdk.model.WalletBalance
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
    override val latestBirthdayHeight: BlockHeight
        get() = error("Intentionally not implemented yet.")

    override suspend fun getFastestServers(
        context: Context,
        servers: List<LightWalletEndpoint>
    ): Flow<FastestServersResult> {
        error("Intentionally not implemented yet.")
    }

    override val latestHeight: BlockHeight
        get() = error("Intentionally not implemented yet.")

    override val network: ZcashNetwork
        get() = error("Intentionally not implemented yet.")

    override val networkHeight: StateFlow<BlockHeight?>
        get() = error("Intentionally not implemented yet.")

    override var onChainErrorHandler: ((BlockHeight, BlockHeight) -> Any)?
        get() = error("Intentionally not implemented yet.")
        set(value) = error("Intentionally not implemented yet.")

    override var onCriticalErrorHandler: ((Throwable?) -> Boolean)?
        get() = error("Intentionally not implemented yet.")
        set(value) = error("Intentionally not implemented yet.")

    override var onProcessorErrorHandler: ((Throwable?) -> Boolean)?
        get() = error("Intentionally not implemented yet.")
        set(value) = error("Intentionally not implemented yet.")

    override var onSetupErrorHandler: ((Throwable?) -> Boolean)?
        get() = error("Intentionally not implemented yet.")
        set(value) = error("Intentionally not implemented yet.")

    override var onSubmissionErrorHandler: ((Throwable?) -> Boolean)?
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
        set(value) = error("Intentionally not implemented yet.")

    override val orchardBalances: StateFlow<WalletBalance?>
        get() = error("Intentionally not implemented yet.")

    override val processorInfo: Flow<CompactBlockProcessor.ProcessorInfo>
        get() = error("Intentionally not implemented yet.")

    override val progress: Flow<PercentDecimal>
        get() = error("Intentionally not implemented yet.")

    override val saplingBalances: StateFlow<WalletBalance?>
        get() = error("Intentionally not implemented yet.")

    override val status: Flow<Synchronizer.Status>
        get() = error("Intentionally not implemented yet.")

    override val transactions: Flow<List<TransactionOverview>>
        get() = error("Intentionally not implemented yet.")

    override val transparentBalance: StateFlow<Zatoshi?>
        get() = error("Intentionally not implemented yet.")

    override val exchangeRateUsd: StateFlow<ObserveFiatCurrencyResult>
        get() = error("Intentionally not implemented yet.")

    override fun close() {
        error("Intentionally not implemented yet.")
    }

    override suspend fun createProposedTransactions(
        proposal: Proposal,
        usk: UnifiedSpendingKey
    ): Flow<TransactionSubmitResult> {
        error("Intentionally not implemented yet.")
    }

    override fun getMemos(transactionOverview: TransactionOverview): Flow<String> {
        error("Intentionally not implemented yet.")
    }

    override suspend fun getNearestRewindHeight(height: BlockHeight): BlockHeight {
        error("Intentionally not implemented yet.")
    }

    override fun getRecipients(transactionOverview: TransactionOverview): Flow<TransactionRecipient> {
        error("Intentionally not implemented yet.")
    }

    override suspend fun getSaplingAddress(account: Account): String {
        error("Intentionally not implemented yet.")
    }

    override suspend fun getTransparentAddress(account: Account): String {
        error("Intentionally not implemented yet.")
    }

    override suspend fun refreshExchangeRateUsd() {
        error("Intentionally not implemented yet.")
    }

    override suspend fun getTransparentBalance(tAddr: String): Zatoshi {
        error("Intentionally not implemented yet.")
    }

    override suspend fun getUnifiedAddress(account: Account): String {
        error("Intentionally not implemented yet.")
    }

    override suspend fun isValidShieldedAddr(address: String): Boolean {
        error("Intentionally not implemented yet.")
    }

    override suspend fun isValidTransparentAddr(address: String): Boolean {
        error("Intentionally not implemented yet.")
    }

    override suspend fun isValidUnifiedAddr(address: String): Boolean {
        error("Intentionally not implemented yet.")
    }

    override suspend fun isValidTexAddr(address: String): Boolean {
        error("Intentionally not implemented yet.")
    }

    override suspend fun proposeShielding(
        account: Account,
        shieldingThreshold: Zatoshi,
        memo: String,
        transparentReceiver: String?
    ): Proposal? {
        error("Intentionally not implemented yet.")
    }

    override suspend fun proposeTransfer(
        account: Account,
        recipient: String,
        amount: Zatoshi,
        memo: String
    ): Proposal {
        error("Intentionally not implemented yet.")
    }

    override suspend fun quickRewind() {
        error("Intentionally not implemented yet.")
    }

    override suspend fun refreshUtxos(
        account: Account,
        since: BlockHeight
    ): Int? {
        error("Intentionally not implemented yet.")
    }

    override suspend fun rewindToNearestHeight(height: BlockHeight) {
        error("Intentionally not implemented yet.")
    }

    @Deprecated(
        "Upcoming SDK 2.1 will create multiple transactions at once for some recipients.",
        replaceWith =
            ReplaceWith(
                "createProposedTransactions(proposeTransfer(usk.account, toAddress, amount, memo), usk)"
            )
    )
    override suspend fun sendToAddress(
        usk: UnifiedSpendingKey,
        amount: Zatoshi,
        toAddress: String,
        memo: String
    ): Long {
        return 1
    }

    @Deprecated(
        "Upcoming SDK 2.1 will create multiple transactions at once for some recipients.",
        replaceWith =
            ReplaceWith(
                "proposeShielding(usk.account, shieldingThreshold, memo)?.let { createProposedTransactions(it, usk) }"
            )
    )
    override suspend fun shieldFunds(
        usk: UnifiedSpendingKey,
        memo: String
    ): Long {
        error("Intentionally not implemented yet.")
    }

    override suspend fun validateAddress(address: String): AddressType = AddressType.Unified

    override suspend fun validateConsensusBranch(): ConsensusMatchType {
        error("Intentionally not implemented yet.")
    }

    override suspend fun validateServerEndpoint(
        context: Context,
        endpoint: LightWalletEndpoint
    ): ServerValidation {
        error("Intentionally not implemented yet.")
    }

    override suspend fun getExistingDataDbFilePath(
        context: Context,
        network: ZcashNetwork,
        alias: String
    ): String {
        error("Intentionally not implemented yet.")
    }

    companion object {
        fun new() = MockSynchronizer()
    }
}
