package co.electriccoin.zcash.ui.common.repository

import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.MetadataDataSource
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.SimpleSwapAsset
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.model.SwapMode
import co.electriccoin.zcash.ui.common.model.SwapStatus
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.model.ZecSimpleSwapAsset
import co.electriccoin.zcash.ui.common.model.metadata.SwapMetadataV3
import co.electriccoin.zcash.ui.common.provider.MetadataKeyStorageProvider
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider
import co.electriccoin.zcash.ui.common.provider.SimpleSwapAssetProvider
import co.electriccoin.zcash.ui.common.serialization.metada.MetadataKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.math.BigDecimal
import java.time.Instant

interface MetadataRepository {
    fun flipTxBookmark(txId: String)

    fun createOrUpdateTxNote(txId: String, note: String)

    fun deleteTxNote(txId: String)

    fun markTxMemoAsRead(txId: String)

    fun markTxAsSwap(
        depositAddress: String,
        provider: String,
        origin: SwapAsset,
        destination: SwapAsset,
        totalFees: Zatoshi,
        totalFeesUsd: BigDecimal,
        amountOutFormatted: BigDecimal,
        mode: SwapMode,
        status: SwapStatus,
    )

    fun updateSwap(
        depositAddress: String,
        amountOutFormatted: BigDecimal,
        status: SwapStatus,
        mode: SwapMode,
        origin: SwapAsset,
        destination: SwapAsset,
    )

    // fun deleteSwap(depositAddress: String)

    fun addSwapAssetToHistory(tokenTicker: String, chainTicker: String)

    fun observeTransactionMetadata(transaction: Transaction): Flow<TransactionMetadata>

    fun observeORSwapMetadata(): Flow<List<TransactionSwapMetadata>?>

    fun observeLastUsedAssetHistory(): Flow<Set<SimpleSwapAsset>?>
}

@Suppress("TooManyFunctions")
class MetadataRepositoryImpl(
    private val accountDataSource: AccountDataSource,
    private val metadataDataSource: MetadataDataSource,
    private val metadataKeyStorageProvider: MetadataKeyStorageProvider,
    private val persistableWalletProvider: PersistableWalletProvider,
    private val simpleSwapAssetProvider: SimpleSwapAssetProvider
) : MetadataRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val mutex = Mutex()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val metadata =
        accountDataSource
            .selectedAccount
            .distinctUntilChangedBy { it?.sdkAccount?.accountUuid }
            .map { getMetadataKey(it ?: return@map null) }
            .distinctUntilChanged()
            .flatMapLatest { if (it == null) flowOf(null) else metadataDataSource.observe(it) }
            .shareIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(0, 0),
                replay = 1
            )

    override fun flipTxBookmark(txId: String) =
        updateMetadata {
            metadataDataSource.flipTxAsBookmarked(txId = txId, key = it)
        }

    override fun createOrUpdateTxNote(txId: String, note: String) =
        updateMetadata {
            metadataDataSource.createOrUpdateTxNote(txId = txId, key = it, note = note)
        }

    override fun deleteTxNote(txId: String) =
        updateMetadata {
            metadataDataSource.deleteTxNote(txId = txId, key = it)
        }

    override fun markTxMemoAsRead(txId: String) =
        updateMetadata {
            metadataDataSource.markTxMemoAsRead(txId = txId, key = it)
        }

    override fun markTxAsSwap(
        depositAddress: String,
        provider: String,
        origin: SwapAsset,
        destination: SwapAsset,
        totalFees: Zatoshi,
        totalFeesUsd: BigDecimal,
        amountOutFormatted: BigDecimal,
        mode: SwapMode,
        status: SwapStatus,
    ) = updateMetadata {
        metadataDataSource.markTxAsSwap(
            depositAddress = depositAddress,
            provider = provider,
            totalFees = totalFees,
            totalFeesUsd = totalFeesUsd,
            amountOutFormatted = amountOutFormatted,
            key = it,
            origin = simpleSwapAssetProvider
                .get(tokenTicker = origin.tokenTicker, chainTicker = origin.chainTicker),
            destination = simpleSwapAssetProvider
                .get(tokenTicker = destination.tokenTicker, chainTicker = destination.chainTicker),
            mode = mode,
            status = status,
        )
    }

    override fun updateSwap(
        depositAddress: String,
        amountOutFormatted: BigDecimal,
        status: SwapStatus,
        mode: SwapMode,
        origin: SwapAsset,
        destination: SwapAsset,
    ) = updateMetadata {
        metadataDataSource.updateSwap(
            depositAddress = depositAddress,
            amountOutFormatted = amountOutFormatted,
            status = status,
            mode = mode,
            origin = simpleSwapAssetProvider
                .get(tokenTicker = origin.tokenTicker, chainTicker = origin.chainTicker),
            destination = simpleSwapAssetProvider
                .get(tokenTicker = destination.tokenTicker, chainTicker = destination.chainTicker),
            key = it
        )
    }

    // override fun deleteSwap(depositAddress: String) =
    //     updateMetadata {
    //         metadataDataSource.deleteSwap(depositAddress = depositAddress, key = it)
    //     }

    override fun addSwapAssetToHistory(tokenTicker: String, chainTicker: String) =
        updateMetadata {
            metadataDataSource.addSwapAssetToHistory(tokenTicker = tokenTicker, chainTicker = chainTicker, key = it)
        }

    override fun observeTransactionMetadata(transaction: Transaction): Flow<TransactionMetadata> {
        val txId = transaction.id.txIdString()
        val depositAddress = transaction.recipient?.address

        return metadata
            .map { metadata ->
                val accountMetadata = metadata?.accountMetadata
                val swapMetadata =
                    if (depositAddress != null) {
                        accountMetadata?.swaps?.swapIds?.find { it.depositAddress == depositAddress }
                    } else {
                        null
                    }
                TransactionMetadata(
                    isBookmarked = accountMetadata?.bookmarked?.find { it.txId == txId }?.isBookmarked == true,
                    isRead = accountMetadata?.read?.any { it == txId } == true,
                    note = accountMetadata?.annotations?.find { it.txId == txId }?.content,
                    swapMetadata = swapMetadata?.toBusinessObject()
                )
            }.distinctUntilChanged()
    }

    private fun SwapMetadataV3.toBusinessObject(): TransactionSwapMetadata =
        TransactionSwapMetadata(
            depositAddress = depositAddress,
            lastUpdated = lastUpdated,
            origin =
                fromAsset.let {
                    simpleSwapAssetProvider.get(tokenTicker = it.token, chainTicker = it.chain)
                },
            destination =
                toAsset.let {
                    simpleSwapAssetProvider.get(tokenTicker = it.token, chainTicker = it.chain)
                },
            mode =
                when (exactInput) {
                    true -> SwapMode.EXACT_INPUT
                    false -> SwapMode.EXACT_OUTPUT
                    null -> SwapMode.EXACT_INPUT
                },
            status = status ?: SwapStatus.SUCCESS,
            amountOutFormatted = amountOutFormatted ?: BigDecimal(0),
            provider = provider,
            totalFees = totalFees,
            totalFeesUsd = totalFeesUsd,
        )

    override fun observeORSwapMetadata(): Flow<List<TransactionSwapMetadata>?> =
        metadata
            .map { metadata ->
                metadata
                    ?.accountMetadata
                    ?.swaps
                    ?.swapIds
                    ?.map { it.toBusinessObject() }
                    ?.filter { it.origin is ZecSimpleSwapAsset }
            }.distinctUntilChanged()

    override fun observeLastUsedAssetHistory(): Flow<Set<SimpleSwapAsset>?> =
        metadata
            .map {
                it
                    ?.accountMetadata
                    ?.swaps
                    ?.lastUsedAssetHistory
                    ?.toSimpleAssetSet()
            }.distinctUntilChanged()

    private fun updateMetadata(block: suspend (MetadataKey) -> Unit) {
        scope.launch {
            mutex.withLock {
                val selectedAccount = accountDataSource.getSelectedAccount()
                val key = getMetadataKey(selectedAccount)
                block(key)
            }
        }
    }

    private suspend fun getMetadataKey(selectedAccount: WalletAccount): MetadataKey {
        val key = metadataKeyStorageProvider.get(selectedAccount.sdkAccount)

        return if (key != null) {
            key
        } else {
            val persistableWallet = persistableWalletProvider.requirePersistableWallet()
            val zashiAccount = accountDataSource.getZashiAccount()
            val newKey =
                MetadataKey.derive(
                    seedPhrase = persistableWallet.seedPhrase,
                    network = persistableWallet.network,
                    zashiAccount = zashiAccount,
                    ufvk =
                        when (selectedAccount) {
                            is KeystoneAccount -> selectedAccount.sdkAccount.ufvk
                            is ZashiAccount -> null
                        }
                )
            metadataKeyStorageProvider.store(newKey, selectedAccount.sdkAccount)
            newKey
        }
    }

    private fun Set<String>.toSimpleAssetSet() =
        this
            .map {
                val data = it.split(":")
                simpleSwapAssetProvider.get(tokenTicker = data[0], chainTicker = data[1])
            }.toSet()
}

data class TransactionMetadata(
    val isBookmarked: Boolean,
    val isRead: Boolean,
    val note: String?,
    val swapMetadata: TransactionSwapMetadata?
)

data class TransactionSwapMetadata(
    val depositAddress: String,
    val provider: String,
    val totalFees: Zatoshi,
    val totalFeesUsd: BigDecimal,
    val lastUpdated: Instant,
    val origin: SimpleSwapAsset,
    val destination: SimpleSwapAsset,
    val mode: SwapMode,
    val status: SwapStatus,
    val amountOutFormatted: BigDecimal,
)
