package co.electriccoin.zcash.ui.fixture

import android.content.Context
import cash.z.ecc.android.sdk.CloseableSynchronizer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.block.processor.CompactBlockProcessor
import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.PercentDecimal
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import cash.z.ecc.android.sdk.model.WalletBalance
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.android.sdk.type.AddressType
import cash.z.ecc.android.sdk.type.ConsensusMatchType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Mocked Synchronizer that can be used instead of the production SdkSynchronizer e.g. for tests.
 */
@Suppress("TooManyFunctions", "UNUSED_PARAMETER")
internal class MockSynchronizer : CloseableSynchronizer {

    override val latestBirthdayHeight: BlockHeight
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")

    override val latestHeight: BlockHeight
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")

    override val network: ZcashNetwork
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")

    override val networkHeight: StateFlow<BlockHeight?>
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

    override val orchardBalances: StateFlow<WalletBalance?>
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")

    override val processorInfo: Flow<CompactBlockProcessor.ProcessorInfo>
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    override val progress: Flow<PercentDecimal>
        get() = TODO("Not yet implemented")

    override val saplingBalances: StateFlow<WalletBalance?>
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")

    override val status: Flow<Synchronizer.Status>
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    override val transactions: Flow<List<TransactionOverview>>
        get() = TODO("Not yet implemented")

    override val transparentBalances: StateFlow<WalletBalance?>
        get() = error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")

    override fun close() {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override fun getMemos(transactionOverview: TransactionOverview): Flow<String> {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun getNearestRewindHeight(height: BlockHeight): BlockHeight {
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

    override suspend fun getTransparentBalance(tAddr: String): WalletBalance {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun getUnifiedAddress(account: Account): String {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun isValidShieldedAddr(address: String): Boolean {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun isValidTransparentAddr(address: String): Boolean {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun isValidUnifiedAddr(address: String): Boolean {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun quickRewind() {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun refreshUtxos(account: Account, since: BlockHeight): Int? {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun rewindToNearestHeight(height: BlockHeight) {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun sendToAddress(
        usk: UnifiedSpendingKey,
        amount: Zatoshi,
        toAddress: String,
        memo: String
    ): Long {
        return 1
    }

    override suspend fun shieldFunds(usk: UnifiedSpendingKey, memo: String): Long {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun validateAddress(address: String): AddressType {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun validateConsensusBranch(): ConsensusMatchType {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    override suspend fun getExistingDataDbFilePath(context: Context, network: ZcashNetwork, alias: String): String {
        error("Intentionally not implemented in ${MockSynchronizer::class.simpleName} implementation.")
    }

    companion object {
        fun new() = MockSynchronizer()
    }
}
