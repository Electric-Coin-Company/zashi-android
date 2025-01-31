package co.electriccoin.zcash.ui.common.datasource

import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.model.Metadata
import co.electriccoin.zcash.ui.common.model.Note
import co.electriccoin.zcash.ui.common.model.TransactionMetadata
import co.electriccoin.zcash.ui.common.provider.MetadataProvider
import co.electriccoin.zcash.ui.common.provider.MetadataStorageProvider
import co.electriccoin.zcash.ui.common.serialization.METADATA_SERIALIZATION_V1
import co.electriccoin.zcash.ui.common.serialization.metada.MetadataKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.Instant

interface MetadataDataSource {
    suspend fun getMetadata(key: MetadataKey): Metadata

    suspend fun markTxAsBookmark(
        txId: String,
        key: MetadataKey,
        isBookmark: Boolean
    ): Metadata

    suspend fun createOrUpdateTxNote(
        txId: String,
        note: String,
        key: MetadataKey
    ): Metadata

    suspend fun deleteTxNote(
        txId: String,
        key: MetadataKey
    ): Metadata

    suspend fun markTxMemoAsRead(
        txId: String,
        key: MetadataKey
    ): Metadata

    suspend fun save(
        metadata: Metadata,
        key: MetadataKey
    )

    suspend fun resetMetadata()
}

class MetadataDataSourceImpl(
    private val metadataStorageProvider: MetadataStorageProvider,
    private val metadataProvider: MetadataProvider,
) : MetadataDataSource {
    private var metadata: Metadata? = null

    private val mutex = Mutex()

    override suspend fun getMetadata(key: MetadataKey): Metadata =
        mutex.withLock {
            getMetadataInternal(key)
        }

    override suspend fun markTxAsBookmark(
        txId: String,
        key: MetadataKey,
        isBookmark: Boolean
    ): Metadata =
        mutex.withLock {
            updateMetadataTransactions(txId = txId, key = key) {
                it?.copy(
                    isBookmark = isBookmark,
                    lastUpdated = Instant.now(),
                ) ?: TransactionMetadata(
                    txId = txId,
                    lastUpdated = Instant.now(),
                    notes = emptyList(),
                    isMemoRead = false,
                    isBookmark = isBookmark,
                )
            }
        }

    override suspend fun createOrUpdateTxNote(
        txId: String,
        note: String,
        key: MetadataKey
    ): Metadata =
        mutex.withLock {
            updateMetadataTransactions(txId = txId, key = key) {
                val newNotes = listOf(Note(content = note))
                it?.copy(
                    notes = newNotes,
                    lastUpdated = Instant.now(),
                ) ?: TransactionMetadata(
                    txId = txId,
                    lastUpdated = Instant.now(),
                    notes = newNotes,
                    isMemoRead = false,
                    isBookmark = false,
                )
            }
        }

    override suspend fun deleteTxNote(
        txId: String,
        key: MetadataKey
    ): Metadata =
        mutex.withLock {
            updateMetadataTransactions(txId = txId, key = key) {
                val newNotes = emptyList<Note>()
                it?.copy(
                    notes = newNotes,
                    lastUpdated = Instant.now(),
                ) ?: TransactionMetadata(
                    txId = txId,
                    lastUpdated = Instant.now(),
                    notes = newNotes,
                    isMemoRead = false,
                    isBookmark = false,
                )
            }
        }

    override suspend fun markTxMemoAsRead(
        txId: String,
        key: MetadataKey
    ): Metadata =
        mutex.withLock {
            updateMetadataTransactions(txId = txId, key = key) {
                it?.copy(
                    isMemoRead = true,
                    lastUpdated = Instant.now(),
                ) ?: TransactionMetadata(
                    txId = txId,
                    lastUpdated = Instant.now(),
                    notes = emptyList(),
                    isMemoRead = true,
                    isBookmark = false,
                )
            }
        }

    override suspend fun save(
        metadata: Metadata,
        key: MetadataKey
    ) = mutex.withLock {
        writeToLocalStorage(metadata, key)
        this.metadata = metadata
    }

    override suspend fun resetMetadata() =
        mutex.withLock {
            metadata = null
        }

    private suspend fun getMetadataInternal(key: MetadataKey): Metadata {
        fun readLocalFileToMetadata(key: MetadataKey): Metadata? {
            val encryptedFile =
                runCatching { metadataStorageProvider.getStorageFile(key) }.getOrNull()
                    ?: return null

            return runCatching {
                metadataProvider.readMetadataFromFile(encryptedFile, key)
            }.onFailure { e -> Twig.warn(e) { "Failed to decrypt metadata" } }.getOrNull()
        }

        return withContext(Dispatchers.IO) {
            val inMemory = metadata

            if (inMemory == null) {
                var new: Metadata? = readLocalFileToMetadata(key)
                if (new == null) {
                    new =
                        Metadata(
                            version = 1,
                            lastUpdated = Instant.now(),
                            transactions = emptyList(),
                        ).also {
                            this@MetadataDataSourceImpl.metadata = it
                        }
                    writeToLocalStorage(new, key)
                }
                new
            } else {
                inMemory
            }
        }
    }

    private suspend fun writeToLocalStorage(
        metadata: Metadata,
        key: MetadataKey
    ) = withContext(Dispatchers.IO) {
        runCatching {
            val file = metadataStorageProvider.getOrCreateStorageFile(key)
            metadataProvider.writeMetadataToFile(file, metadata, key)
        }.onFailure { e -> Twig.warn(e) { "Failed to write address book" } }
    }

    private suspend fun updateMetadataTransactions(
        txId: String,
        key: MetadataKey,
        transform: (TransactionMetadata?) -> TransactionMetadata
    ): Metadata {
        fun List<TransactionMetadata>.replaceOrAdd(
            txId: String,
            transform: (TransactionMetadata?) -> TransactionMetadata
        ) = if (this.any { it.txId == txId }) {
            this.map { if (it.txId == txId) transform(it) else it }
        } else {
            this + transform(null)
        }

        return withContext(Dispatchers.IO) {
            Metadata(
                lastUpdated = Instant.now(),
                version = METADATA_SERIALIZATION_V1,
                transactions = getMetadataInternal(key).transactions.replaceOrAdd(txId, transform),
            ).also {
                this@MetadataDataSourceImpl.metadata = it
                writeToLocalStorage(it, key)
            }
        }
    }
}
