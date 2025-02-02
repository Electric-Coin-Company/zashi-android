package co.electriccoin.zcash.ui.common.repository

import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.MetadataDataSource
import co.electriccoin.zcash.ui.common.model.Metadata
import co.electriccoin.zcash.ui.common.model.TransactionMetadata
import co.electriccoin.zcash.ui.common.provider.MetadataKeyStorageProvider
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider
import co.electriccoin.zcash.ui.common.serialization.metada.MetadataKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

interface MetadataRepository {
    val metadata: Flow<Metadata?>

    suspend fun markTxAsBookmark(
        txId: String,
        isBookmark: Boolean
    )

    suspend fun createOrUpdateTxNote(
        txId: String,
        note: String
    )

    suspend fun deleteTxNote(txId: String)

    suspend fun markTxMemoAsRead(txId: String)

    suspend fun resetMetadata()

    fun observeTransactionMetadataByTxId(txId: String): Flow<TransactionMetadata?>
}

class MetadataRepositoryImpl(
    private val metadataDataSource: MetadataDataSource,
    private val metadataKeyStorageProvider: MetadataKeyStorageProvider,
    private val accountDataSource: AccountDataSource,
    private val persistableWalletProvider: PersistableWalletProvider,
) : MetadataRepository {
    private val semaphore = Mutex()

    private val cache = MutableStateFlow<Metadata?>(null)

    override val metadata: Flow<Metadata?> =
        cache
            .onSubscription {
                withNonCancellableSemaphore {
                    ensureSynchronization()
                }
            }

    override suspend fun markTxAsBookmark(
        txId: String,
        isBookmark: Boolean
    ) = mutateMetadata {
        metadataDataSource.markTxAsBookmark(
            txId = txId,
            key = getMetadataKey(),
            isBookmark = isBookmark
        )
    }

    override suspend fun createOrUpdateTxNote(
        txId: String,
        note: String
    ) = mutateMetadata {
        metadataDataSource.createOrUpdateTxNote(
            txId = txId,
            note = note,
            key = getMetadataKey(),
        )
    }

    override suspend fun deleteTxNote(txId: String) =
        mutateMetadata {
            metadataDataSource.deleteTxNote(
                txId = txId,
                key = getMetadataKey()
            )
        }

    override suspend fun markTxMemoAsRead(txId: String) =
        mutateMetadata {
            metadataDataSource.markTxMemoAsRead(
                txId = txId,
                key = getMetadataKey(),
            )
        }

    override suspend fun resetMetadata() {
        withNonCancellableSemaphore {
            metadataDataSource.resetMetadata()
            cache.update { null }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeTransactionMetadataByTxId(txId: String): Flow<TransactionMetadata?> =
        metadata
            .mapLatest { metadata ->
                metadata?.transactions?.firstOrNull { it.txId == txId }
            }
            .distinctUntilChanged()

    private suspend fun ensureSynchronization() {
        if (cache.value == null) {
            val metadata = metadataDataSource.getMetadata(key = getMetadataKey())
            metadataDataSource.save(metadata = metadata, key = getMetadataKey())
            cache.update { metadata }
        }
    }

    private suspend fun mutateMetadata(block: suspend () -> Metadata) =
        withNonCancellableSemaphore {
            ensureSynchronization()
            val new = block()
            cache.update { new }
        }

    private suspend fun withNonCancellableSemaphore(block: suspend () -> Unit) =
        withContext(NonCancellable + Dispatchers.Default) {
            semaphore.withLock { block() }
        }

    private suspend fun getMetadataKey(): MetadataKey {
        val key = metadataKeyStorageProvider.get()

        return if (key != null) {
            key
        } else {
            val account = accountDataSource.getZashiAccount()
            val persistableWallet = persistableWalletProvider.getPersistableWallet()
            val newKey =
                MetadataKey.derive(
                    seedPhrase = persistableWallet.seedPhrase,
                    network = persistableWallet.network,
                    account = account
                )
            metadataKeyStorageProvider.store(newKey)
            newKey
        }
    }
}
