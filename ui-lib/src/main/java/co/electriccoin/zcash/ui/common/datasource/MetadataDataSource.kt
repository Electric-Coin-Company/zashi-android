package co.electriccoin.zcash.ui.common.datasource

import cash.z.ecc.android.sdk.model.AccountUuid
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.model.AccountMetadata
import co.electriccoin.zcash.ui.common.model.AnnotationMetadata
import co.electriccoin.zcash.ui.common.model.BookmarkMetadata
import co.electriccoin.zcash.ui.common.model.Metadata
import co.electriccoin.zcash.ui.common.provider.MetadataProvider
import co.electriccoin.zcash.ui.common.provider.MetadataStorageProvider
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
        account: AccountUuid,
        key: MetadataKey
    ): Metadata

    suspend fun createOrUpdateTxNote(
        txId: String,
        note: String,
        account: AccountUuid,
        key: MetadataKey
    ): Metadata

    suspend fun deleteTxNote(
        txId: String,
        account: AccountUuid,
        key: MetadataKey
    ): Metadata

    suspend fun markTxMemoAsRead(
        txId: String,
        account: AccountUuid,
        key: MetadataKey
    ): Metadata

    suspend fun save(
        metadata: Metadata,
        key: MetadataKey
    )

    suspend fun resetMetadata()
}

@Suppress("TooManyFunctions")
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

    override suspend fun flipTxAsBookmarked(
        txId: String,
        account: AccountUuid,
        key: MetadataKey,
    ): Metadata =
        mutex.withLock {
            updateMetadataBookmark(txId = txId, account = account, key = key) {
                it.copy(
                    isBookmarked = !it.isBookmarked,
                    lastUpdated = Instant.now(),
                )
            }
        }

    override suspend fun createOrUpdateTxNote(
        txId: String,
        note: String,
        account: AccountUuid,
        key: MetadataKey
    ): Metadata =
        mutex.withLock {
            updateMetadataAnnotation(
                txId = txId,
                account = account,
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
        account: AccountUuid,
        key: MetadataKey
    ): Metadata =
        mutex.withLock {
            updateMetadataAnnotation(
                txId = txId,
                account = account,
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
        account: AccountUuid,
        key: MetadataKey
    ): Metadata =
        mutex.withLock {
            updateMetadata(
                account = account,
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
                            accountMetadata = emptyMap(),
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

    private suspend fun updateMetadataAnnotation(
        txId: String,
        account: AccountUuid,
        key: MetadataKey,
        transform: (AnnotationMetadata) -> AnnotationMetadata
    ): Metadata {
        return updateMetadata(
            account = account,
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
    }

    private suspend fun updateMetadataBookmark(
        txId: String,
        account: AccountUuid,
        key: MetadataKey,
        transform: (BookmarkMetadata) -> BookmarkMetadata
    ): Metadata {
        return updateMetadata(
            account = account,
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
    }

    @OptIn(ExperimentalStdlibApi::class)
    private suspend fun updateMetadata(
        account: AccountUuid,
        key: MetadataKey,
        transform: (AccountMetadata) -> AccountMetadata
    ): Metadata {
        return withContext(Dispatchers.IO) {
            val metadata = getMetadataInternal(key)

            val accountMetadata = metadata.accountMetadata[account.value.toHexString()] ?: defaultAccountMetadata()

            val updatedMetadata =
                metadata.copy(
                    lastUpdated = Instant.now(),
                    accountMetadata =
                        metadata.accountMetadata
                            .toMutableMap()
                            .apply {
                                put(account.value.toHexString(), transform(accountMetadata))
                            }
                            .toMap()
                )

            this@MetadataDataSourceImpl.metadata = updatedMetadata
            writeToLocalStorage(updatedMetadata, key)

            updatedMetadata
        }
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
        this.toMutableList()
            .apply {
                set(index, transform(this[index]))
            }
            .toList()
    } else {
        this + transform(null)
    }
}
