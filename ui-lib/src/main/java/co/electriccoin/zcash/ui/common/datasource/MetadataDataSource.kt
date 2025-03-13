package co.electriccoin.zcash.ui.common.datasource

import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.model.AccountMetadata
import co.electriccoin.zcash.ui.common.model.AnnotationMetadata
import co.electriccoin.zcash.ui.common.model.BookmarkMetadata
import co.electriccoin.zcash.ui.common.model.Metadata
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

    suspend fun flipTxAsBookmarked(
        txId: String,
        key: MetadataKey
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
}

@Suppress("TooManyFunctions")
class MetadataDataSourceImpl(
    private val metadataStorageProvider: MetadataStorageProvider,
    private val metadataProvider: MetadataProvider,
) : MetadataDataSource {
    private val mutex = Mutex()

    override suspend fun getMetadata(key: MetadataKey): Metadata =
        mutex.withLock {
            getMetadataInternal(key)
        }

    override suspend fun flipTxAsBookmarked(
        txId: String,
        key: MetadataKey,
    ): Metadata =
        mutex.withLock {
            updateMetadataBookmark(txId = txId, key = key) {
                it.copy(
                    isBookmarked = !it.isBookmarked,
                    lastUpdated = Instant.now(),
                )
            }
        }

    override suspend fun createOrUpdateTxNote(
        txId: String,
        note: String,
        key: MetadataKey
    ): Metadata =
        mutex.withLock {
            updateMetadataAnnotation(
                txId = txId,
                key = key
            ) {
                it.copy(
                    content = note,
                    lastUpdated = Instant.now(),
                )
            }
        }

    override suspend fun deleteTxNote(
        txId: String,
        key: MetadataKey
    ): Metadata =
        mutex.withLock {
            updateMetadataAnnotation(
                txId = txId,
                key = key
            ) {
                it.copy(
                    content = null,
                    lastUpdated = Instant.now(),
                )
            }
        }

    override suspend fun markTxMemoAsRead(
        txId: String,
        key: MetadataKey
    ): Metadata =
        mutex.withLock {
            updateMetadata(
                key = key,
                transform = { metadata ->
                    metadata.copy(
                        read = (metadata.read.toSet() + txId).toList()
                    )
                }
            )
        }

    override suspend fun save(
        metadata: Metadata,
        key: MetadataKey
    ) = mutex.withLock {
        writeToLocalStorage(metadata, key)
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
            var new: Metadata? = readLocalFileToMetadata(key)
            if (new == null) {
                new =
                    Metadata(
                        version = METADATA_SERIALIZATION_V1,
                        lastUpdated = Instant.now(),
                        accountMetadata = defaultAccountMetadata(),
                    )
                writeToLocalStorage(new, key)
            }
            new
        }
    }

    private suspend fun writeToLocalStorage(
        metadata: Metadata,
        key: MetadataKey
    ) {
        withContext(Dispatchers.IO) {
            runCatching {
                val file = metadataStorageProvider.getOrCreateStorageFile(key)
                metadataProvider.writeMetadataToFile(file, metadata, key)
            }.onFailure { e -> Twig.warn(e) { "Failed to write address book" } }
        }
    }

    private suspend fun updateMetadataAnnotation(
        txId: String,
        key: MetadataKey,
        transform: (AnnotationMetadata) -> AnnotationMetadata
    ): Metadata =
        updateMetadata(
            key = key,
            transform = { metadata ->
                metadata.copy(
                    annotations =
                        metadata.annotations
                            .replaceOrAdd(
                                predicate = { it.txId == txId },
                                transform = {
                                    val bookmarkMetadata = it ?: defaultAnnotationMetadata(txId)
                                    transform(bookmarkMetadata)
                                }
                            )
                )
            }
        )

    private suspend fun updateMetadataBookmark(
        txId: String,
        key: MetadataKey,
        transform: (BookmarkMetadata) -> BookmarkMetadata
    ): Metadata =
        updateMetadata(
            key = key,
            transform = { metadata ->
                metadata.copy(
                    bookmarked =
                        metadata.bookmarked
                            .replaceOrAdd(
                                predicate = { it.txId == txId },
                                transform = {
                                    val bookmarkMetadata = it ?: defaultBookmarkMetadata(txId)
                                    transform(bookmarkMetadata)
                                }
                            )
                )
            }
        )

    private suspend fun updateMetadata(
        key: MetadataKey,
        transform: (AccountMetadata) -> AccountMetadata
    ): Metadata =
        withContext(Dispatchers.IO) {
            val metadata = getMetadataInternal(key)

            val accountMetadata = metadata.accountMetadata

            val updatedMetadata =
                metadata.copy(
                    lastUpdated = Instant.now(),
                    accountMetadata = transform(accountMetadata)
                )

            writeToLocalStorage(updatedMetadata, key)

            updatedMetadata
        }
}

private fun defaultAccountMetadata() =
    AccountMetadata(
        bookmarked = emptyList(),
        read = emptyList(),
        annotations = emptyList(),
    )

private fun defaultBookmarkMetadata(txId: String) =
    BookmarkMetadata(
        txId = txId,
        lastUpdated = Instant.now(),
        isBookmarked = false
    )

private fun defaultAnnotationMetadata(txId: String) =
    AnnotationMetadata(
        txId = txId,
        lastUpdated = Instant.now(),
        content = null
    )

private fun <T : Any> List<T>.replaceOrAdd(
    predicate: (T) -> Boolean,
    transform: (T?) -> T
): List<T> {
    val index = this.indexOfFirst(predicate)
    return if (index != -1) {
        this
            .toMutableList()
            .apply {
                set(index, transform(this[index]))
            }.toList()
    } else {
        this + transform(null)
    }
}
