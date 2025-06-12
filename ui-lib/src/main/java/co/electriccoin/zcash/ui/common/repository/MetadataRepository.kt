package co.electriccoin.zcash.ui.common.repository

import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.MetadataDataSource
import co.electriccoin.zcash.ui.common.model.Metadata
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.provider.MetadataKeyStorageProvider
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider
import co.electriccoin.zcash.ui.common.serialization.metada.MetadataKey
import co.electriccoin.zcash.ui.util.CloseableScopeHolder
import co.electriccoin.zcash.ui.util.CloseableScopeHolderImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

interface MetadataRepository {
    val metadata: Flow<Metadata?>

    suspend fun flipTxBookmark(txId: String)

    suspend fun createOrUpdateTxNote(
        txId: String,
        note: String
    )

    suspend fun deleteTxNote(txId: String)

    suspend fun markTxMemoAsRead(txId: String)

    fun observeTransactionMetadataByTxId(txId: String): Flow<TransactionMetadata>
}

class MetadataRepositoryImpl(
    private val accountDataSource: AccountDataSource,
    private val metadataDataSource: MetadataDataSource,
    private val metadataKeyStorageProvider: MetadataKeyStorageProvider,
    private val persistableWalletProvider: PersistableWalletProvider,
) : MetadataRepository,
    CloseableScopeHolder by CloseableScopeHolderImpl(Dispatchers.IO) {
    private val command = Channel<Command>()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val metadata: Flow<Metadata?> =
        accountDataSource
            .selectedAccount
            .flatMapLatest { account ->
                channelFlow {
                    send(null)

                    if (account != null) {
                        val metadataKey = getMetadataKey(account)
                        send(metadataDataSource.getMetadata(metadataKey))

                        launch {
                            command
                                .receiveAsFlow()
                                .filter {
                                    it.account.sdkAccount.accountUuid == account.sdkAccount.accountUuid
                                }.collect { command ->
                                    val new =
                                        when (command) {
                                            is Command.CreateOrUpdateTxNote ->
                                                metadataDataSource.createOrUpdateTxNote(
                                                    txId = command.txId,
                                                    key = metadataKey,
                                                    note = command.note
                                                )

                                            is Command.DeleteTxNote ->
                                                metadataDataSource.deleteTxNote(
                                                    txId = command.txId,
                                                    key = metadataKey,
                                                )

                                            is Command.FlipTxBookmark ->
                                                metadataDataSource.flipTxAsBookmarked(
                                                    txId = command.txId,
                                                    key = metadataKey,
                                                )

                                            is Command.MarkTxMemoAsRead ->
                                                metadataDataSource.markTxMemoAsRead(
                                                    txId = command.txId,
                                                    key = metadataKey,
                                                )
                                        }

                                    send(new)
                                }
                        }
                    }

                    awaitClose {
                        // do nothing
                    }
                }
            }.stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = null
            )

    override suspend fun flipTxBookmark(txId: String) {
        scope.launch {
            command.send(
                Command.FlipTxBookmark(
                    txId = txId,
                    account = accountDataSource.getSelectedAccount()
                )
            )
        }
    }

    override suspend fun createOrUpdateTxNote(
        txId: String,
        note: String
    ) {
        scope.launch {
            command.send(
                Command.CreateOrUpdateTxNote(
                    txId = txId,
                    note = note,
                    account = accountDataSource.getSelectedAccount()
                )
            )
        }
    }

    override suspend fun deleteTxNote(txId: String) {
        scope.launch {
            command.send(
                Command.DeleteTxNote(
                    txId = txId,
                    account = accountDataSource.getSelectedAccount()
                )
            )
        }
    }

    override suspend fun markTxMemoAsRead(txId: String) {
        scope.launch {
            command.send(
                Command.MarkTxMemoAsRead(
                    txId = txId,
                    account = accountDataSource.getSelectedAccount()
                )
            )
        }
    }

    override fun observeTransactionMetadataByTxId(txId: String): Flow<TransactionMetadata> =
        metadata
            .map { metadata ->
                val accountMetadata = metadata?.accountMetadata

                TransactionMetadata(
                    isBookmarked = accountMetadata?.bookmarked?.find { it.txId == txId }?.isBookmarked == true,
                    isRead = accountMetadata?.read?.any { it == txId } == true,
                    note = accountMetadata?.annotations?.find { it.txId == txId }?.content,
                )
            }.distinctUntilChanged()

    private suspend fun getMetadataKey(selectedAccount: WalletAccount): MetadataKey {
        val key = metadataKeyStorageProvider.get(selectedAccount)

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
                    selectedAccount = selectedAccount
                )
            metadataKeyStorageProvider.store(newKey, selectedAccount)
            newKey
        }
    }
}

data class TransactionMetadata(
    val isBookmarked: Boolean,
    val isRead: Boolean,
    val note: String?
)

private sealed interface Command {
    val account: WalletAccount

    data class FlipTxBookmark(
        val txId: String,
        override val account: WalletAccount
    ) : Command

    data class CreateOrUpdateTxNote(
        val txId: String,
        val note: String,
        override val account: WalletAccount
    ) : Command

    data class DeleteTxNote(
        val txId: String,
        override val account: WalletAccount
    ) : Command

    data class MarkTxMemoAsRead(
        val txId: String,
        override val account: WalletAccount
    ) : Command
}
